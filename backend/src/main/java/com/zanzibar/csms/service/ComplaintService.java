package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.complaint.*;
import com.zanzibar.csms.entity.*;
import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import com.zanzibar.csms.entity.enums.ComplaintType;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.repository.ComplaintRepository;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    private static final List<ComplaintStatus> TERMINAL_STATUSES = Arrays.asList(
        ComplaintStatus.RESOLVED,
        ComplaintStatus.CLOSED,
        ComplaintStatus.DISMISSED,
        ComplaintStatus.WITHDRAWN
    );

    public ComplaintResponseDto createComplaint(ComplaintCreateDto createDto, String username) {
        Employee complainant = employeeRepository.findById(createDto.getComplainantId())
            .orElseThrow(() -> new ResourceNotFoundException("Complainant not found"));

        Employee respondent = null;
        if (createDto.getRespondentId() != null) {
            respondent = employeeRepository.findById(createDto.getRespondentId())
                .orElseThrow(() -> new ResourceNotFoundException("Respondent not found"));
        }

        User submittedBy = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Complaint complaint = Complaint.builder()
            .complaintNumber(generateComplaintNumber())
            .complainant(complainant)
            .respondent(respondent)
            .submittedBy(submittedBy)
            .complaintType(createDto.getComplaintType())
            .severity(createDto.getSeverity())
            .title(createDto.getTitle())
            .description(createDto.getDescription())
            .incidentDate(createDto.getIncidentDate())
            .incidentLocation(createDto.getIncidentLocation())
            .isAnonymous(createDto.getIsAnonymous())
            .isConfidential(createDto.getIsConfidential())
            .witnessNames(createDto.getWitnessNames())
            .evidenceDescription(createDto.getEvidenceDescription())
            .build();

        Complaint savedComplaint = complaintRepository.save(complaint);

        logComplaintActivity(savedComplaint, submittedBy, "COMPLAINT_CREATED", 
            "Complaint created", null, ComplaintStatus.DRAFT);

        auditService.logAction(submittedBy.getId(), username, "CREATE_COMPLAINT", 
            "Complaint", savedComplaint.getId(), null, savedComplaint.getComplaintNumber(), 
            true, "Complaint created successfully");

        return mapToResponseDto(savedComplaint);
    }

    public ComplaintResponseDto updateComplaint(String id, ComplaintUpdateDto updateDto, String username) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ComplaintStatus previousStatus = complaint.getStatus();

        if (updateDto.getStatus() != null && !updateDto.getStatus().equals(previousStatus)) {
            validateStatusTransition(previousStatus, updateDto.getStatus());
            complaint.setStatus(updateDto.getStatus());

            if (updateDto.getStatus() == ComplaintStatus.ACKNOWLEDGED) {
                complaint.setAcknowledgmentDate(LocalDateTime.now());
            } else if (updateDto.getStatus() == ComplaintStatus.UNDER_INVESTIGATION) {
                complaint.setInvestigationStartDate(LocalDateTime.now());
            } else if (TERMINAL_STATUSES.contains(updateDto.getStatus())) {
                complaint.setActualResolutionDate(LocalDateTime.now());
            }
        }

        if (updateDto.getSeverity() != null) {
            complaint.setSeverity(updateDto.getSeverity());
            complaint.setTargetResolutionDate(LocalDateTime.now().plusDays(updateDto.getSeverity().getDaysToResolve()));
        }

        if (updateDto.getTitle() != null) complaint.setTitle(updateDto.getTitle());
        if (updateDto.getDescription() != null) complaint.setDescription(updateDto.getDescription());
        if (updateDto.getIncidentDate() != null) complaint.setIncidentDate(updateDto.getIncidentDate());
        if (updateDto.getIncidentLocation() != null) complaint.setIncidentLocation(updateDto.getIncidentLocation());
        if (updateDto.getResolutionSummary() != null) complaint.setResolutionSummary(updateDto.getResolutionSummary());
        if (updateDto.getDisciplinaryAction() != null) complaint.setDisciplinaryAction(updateDto.getDisciplinaryAction());
        if (updateDto.getWitnessNames() != null) complaint.setWitnessNames(updateDto.getWitnessNames());
        if (updateDto.getEvidenceDescription() != null) complaint.setEvidenceDescription(updateDto.getEvidenceDescription());
        if (updateDto.getInvestigationNotes() != null) complaint.setInvestigationNotes(updateDto.getInvestigationNotes());
        if (updateDto.getComplainantSatisfactionRating() != null) complaint.setComplainantSatisfactionRating(updateDto.getComplainantSatisfactionRating());
        if (updateDto.getFollowUpRequired() != null) complaint.setFollowUpRequired(updateDto.getFollowUpRequired());
        if (updateDto.getFollowUpDate() != null) complaint.setFollowUpDate(updateDto.getFollowUpDate());

        if (updateDto.getAssignedInvestigatorId() != null) {
            User investigator = userRepository.findById(updateDto.getAssignedInvestigatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Investigator not found"));
            complaint.setAssignedInvestigator(investigator);
        }

        Complaint savedComplaint = complaintRepository.save(complaint);

        if (!previousStatus.equals(savedComplaint.getStatus())) {
            logComplaintActivity(savedComplaint, user, "STATUS_CHANGED", 
                String.format("Status changed from %s to %s", previousStatus, savedComplaint.getStatus()),
                previousStatus, savedComplaint.getStatus());

            notificationService.sendComplaintStatusNotification(savedComplaint, previousStatus);
        }

        auditService.logAction(user.getId(), username, "UPDATE_COMPLAINT", 
            "Complaint", savedComplaint.getId(), null, savedComplaint.getComplaintNumber(), 
            true, "Complaint updated successfully");

        return mapToResponseDto(savedComplaint);
    }

    public ComplaintResponseDto getComplaintById(String id) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        return mapToResponseDto(complaint);
    }

    public ComplaintResponseDto getComplaintByNumber(String complaintNumber) {
        Complaint complaint = complaintRepository.findByComplaintNumber(complaintNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));
        return mapToResponseDto(complaint);
    }

    public Page<ComplaintResponseDto> getComplaints(Pageable pageable) {
        return complaintRepository.findAll(pageable)
            .map(this::mapToResponseDto);
    }

    public Page<ComplaintResponseDto> getComplaintsByComplainant(String complainantId, Pageable pageable) {
        return complaintRepository.findByComplainantId(complainantId, pageable)
            .map(this::mapToResponseDto);
    }

    public Page<ComplaintResponseDto> getComplaintsByStatus(ComplaintStatus status, Pageable pageable) {
        return complaintRepository.findByStatus(status, pageable)
            .map(this::mapToResponseDto);
    }

    public Page<ComplaintResponseDto> getComplaintsByInvestigator(String investigatorId, Pageable pageable) {
        return complaintRepository.findByAssignedInvestigatorId(investigatorId, pageable)
            .map(this::mapToResponseDto);
    }

    public Page<ComplaintResponseDto> searchComplaints(String searchTerm, Pageable pageable) {
        return complaintRepository.searchComplaints(searchTerm, pageable)
            .map(this::mapToResponseDto);
    }

    public Page<ComplaintResponseDto> getComplaintsWithFilters(
            ComplaintType complaintType, ComplaintStatus status, ComplaintSeverity severity,
            String complainantId, String respondentId, String investigatorId,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        
        return complaintRepository.findComplaintsWithFilters(
            complaintType, status, severity, complainantId, respondentId, 
            investigatorId, startDate, endDate, pageable)
            .map(this::mapToResponseDto);
    }

    public List<Complaint> getOverdueComplaints() {
        return complaintRepository.findOverdueComplaints(LocalDateTime.now(), TERMINAL_STATUSES);
    }

    public List<Complaint> getComplaintsRequiringFollowUp() {
        return complaintRepository.findComplaintsRequiringFollowUp(LocalDateTime.now());
    }

    public ComplaintResponseDto assignInvestigator(String complaintId, String investigatorId, String username) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        User investigator = userRepository.findById(investigatorId)
            .orElseThrow(() -> new ResourceNotFoundException("Investigator not found"));

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        complaint.setAssignedInvestigator(investigator);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        Complaint savedComplaint = complaintRepository.save(complaint);

        logComplaintActivity(savedComplaint, user, "INVESTIGATOR_ASSIGNED", 
            String.format("Investigator assigned: %s", investigator.getFullName()),
            null, ComplaintStatus.ASSIGNED);

        notificationService.sendInvestigatorAssignmentNotification(savedComplaint, investigator);

        auditService.logAction(user.getId(), username, "ASSIGN_INVESTIGATOR", 
            "Complaint", savedComplaint.getId(), null, savedComplaint.getComplaintNumber(), 
            true, "Investigator assigned successfully");

        return mapToResponseDto(savedComplaint);
    }

    private String generateComplaintNumber() {
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long count = complaintRepository.count() + 1;
        return String.format("COMP-%s-%04d", datePrefix, count);
    }

    private void validateStatusTransition(ComplaintStatus currentStatus, ComplaintStatus newStatus) {
        boolean isValidTransition = switch (currentStatus) {
            case DRAFT -> newStatus == ComplaintStatus.SUBMITTED;
            case SUBMITTED -> newStatus == ComplaintStatus.ACKNOWLEDGED || newStatus == ComplaintStatus.REJECTED;
            case ACKNOWLEDGED -> newStatus == ComplaintStatus.ASSIGNED || newStatus == ComplaintStatus.UNDER_REVIEW;
            case ASSIGNED -> newStatus == ComplaintStatus.UNDER_INVESTIGATION || newStatus == ComplaintStatus.RETURNED;
            case UNDER_INVESTIGATION -> newStatus == ComplaintStatus.UNDER_REVIEW || 
                                      newStatus == ComplaintStatus.EVIDENCE_COLLECTION || 
                                      newStatus == ComplaintStatus.RESOLVED;
            case EVIDENCE_COLLECTION -> newStatus == ComplaintStatus.UNDER_INVESTIGATION || 
                                       newStatus == ComplaintStatus.UNDER_REVIEW;
            case UNDER_REVIEW -> newStatus == ComplaintStatus.PENDING_DECISION || 
                               newStatus == ComplaintStatus.RETURNED;
            case PENDING_DECISION -> newStatus == ComplaintStatus.RESOLVED || 
                                   newStatus == ComplaintStatus.DISMISSED || 
                                   newStatus == ComplaintStatus.ESCALATED;
            case ESCALATED -> newStatus == ComplaintStatus.UNDER_REVIEW || 
                            newStatus == ComplaintStatus.RESOLVED;
            case RESOLVED -> newStatus == ComplaintStatus.CLOSED || newStatus == ComplaintStatus.APPEALED;
            case APPEALED -> newStatus == ComplaintStatus.APPEAL_REVIEW || newStatus == ComplaintStatus.CLOSED;
            case APPEAL_REVIEW -> newStatus == ComplaintStatus.APPEAL_UPHELD || 
                                newStatus == ComplaintStatus.APPEAL_DISMISSED;
            case APPEAL_UPHELD, APPEAL_DISMISSED -> newStatus == ComplaintStatus.CLOSED;
            default -> false;
        };

        if (!isValidTransition) {
            throw new IllegalStateException(String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }

    private void logComplaintActivity(Complaint complaint, User user, String activityType, 
                                    String description, ComplaintStatus previousStatus, 
                                    ComplaintStatus newStatus) {
        ComplaintActivity activity = ComplaintActivity.builder()
            .complaint(complaint)
            .user(user)
            .activityType(activityType)
            .activityDescription(description)
            .previousStatus(previousStatus)
            .newStatus(newStatus)
            .activityDate(LocalDateTime.now())
            .isInternal(false)
            .build();

        complaint.getActivities().add(activity);
    }

    private ComplaintResponseDto mapToResponseDto(Complaint complaint) {
        return ComplaintResponseDto.builder()
            .id(complaint.getId())
            .complaintNumber(complaint.getComplaintNumber())
            .complainantId(complaint.getComplainant().getId())
            .complainantName(complaint.getComplainant().getFullName())
            .respondentId(complaint.getRespondent() != null ? complaint.getRespondent().getId() : null)
            .respondentName(complaint.getRespondent() != null ? complaint.getRespondent().getFullName() : null)
            .submittedById(complaint.getSubmittedBy() != null ? complaint.getSubmittedBy().getId() : null)
            .submittedByName(complaint.getSubmittedBy() != null ? complaint.getSubmittedBy().getFullName() : null)
            .complaintType(complaint.getComplaintType())
            .status(complaint.getStatus())
            .severity(complaint.getSeverity())
            .title(complaint.getTitle())
            .description(complaint.getDescription())
            .incidentDate(complaint.getIncidentDate())
            .incidentLocation(complaint.getIncidentLocation())
            .submissionDate(complaint.getSubmissionDate())
            .acknowledgmentDate(complaint.getAcknowledgmentDate())
            .assignedInvestigatorId(complaint.getAssignedInvestigator() != null ? complaint.getAssignedInvestigator().getId() : null)
            .assignedInvestigatorName(complaint.getAssignedInvestigator() != null ? complaint.getAssignedInvestigator().getFullName() : null)
            .investigationStartDate(complaint.getInvestigationStartDate())
            .targetResolutionDate(complaint.getTargetResolutionDate())
            .actualResolutionDate(complaint.getActualResolutionDate())
            .resolutionSummary(complaint.getResolutionSummary())
            .disciplinaryAction(complaint.getDisciplinaryAction())
            .isAnonymous(complaint.getIsAnonymous())
            .isConfidential(complaint.getIsConfidential())
            .witnessNames(complaint.getWitnessNames())
            .evidenceDescription(complaint.getEvidenceDescription())
            .complainantSatisfactionRating(complaint.getComplainantSatisfactionRating())
            .followUpRequired(complaint.getFollowUpRequired())
            .followUpDate(complaint.getFollowUpDate())
            .isOverdue(complaint.isOverdue())
            .daysToResolve(complaint.getDaysToResolve())
            .createdAt(complaint.getCreatedAt())
            .updatedAt(complaint.getUpdatedAt())
            .build();
    }
}