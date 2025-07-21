package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.dismissal.DismissalRequestCreateDto;
import com.zanzibar.csms.dto.dismissal.DismissalRequestResponseDto;
import com.zanzibar.csms.dto.dismissal.DismissalRequestUpdateDto;
import com.zanzibar.csms.entity.DismissalRequest;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.DismissalReason;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.DismissalRequestMapper;
import com.zanzibar.csms.repository.DismissalRequestRepository;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DismissalRequestService {

    private final DismissalRequestRepository dismissalRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DismissalRequestMapper dismissalRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public DismissalRequestResponseDto createDismissalRequest(DismissalRequestCreateDto createDto, String submittedById) {
        log.info("Creating dismissal request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for dismissal
        Employee employee = validateEmployeeForDismissal(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate dismissal request
        validateDismissalRequest(createDto, employee);

        // Create dismissal request
        DismissalRequest dismissalRequest = DismissalRequest.dismissalBuilder()
            .requestNumber(generateRequestNumber())
            .employee(employee)
            .submittedBy(submittedBy)
            .submissionDate(LocalDateTime.now())
            .status(RequestStatus.SUBMITTED)
            .dismissalReason(createDto.getDismissalReason())
            .detailedCharges(createDto.getDetailedCharges())
            .incidentDate(createDto.getIncidentDate())
            .investigationStartDate(createDto.getInvestigationStartDate())
            .investigationEndDate(createDto.getInvestigationEndDate())
            .investigationSummary(createDto.getInvestigationSummary())
            .investigationOfficer(createDto.getInvestigationOfficer())
            .disciplinaryHistory(createDto.getDisciplinaryHistory())
            .priorWarningsCount(createDto.getPriorWarningsCount())
            .showCauseDate(createDto.getShowCauseDate())
            .employeeResponse(createDto.getEmployeeResponse())
            .hearingDate(createDto.getHearingDate())
            .hearingOutcome(createDto.getHearingOutcome())
            .mitigatingFactors(createDto.getMitigatingFactors())
            .aggravatingFactors(createDto.getAggravatingFactors())
            .hrRecommendations(createDto.getHrRecommendations())
            .legalConsultation(createDto.getLegalConsultation())
            .legalAdvice(createDto.getLegalAdvice())
            .unionNotificationDate(createDto.getUnionNotificationDate())
            .unionResponse(createDto.getUnionResponse())
            .finalSettlementAmount(createDto.getFinalSettlementAmount())
            .effectiveDismissalDate(createDto.getEffectiveDismissalDate())
            .build();

        DismissalRequest savedRequest = dismissalRequestRepository.save(dismissalRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_DISMISSAL_REQUEST",
            "DismissalRequest",
            savedRequest.getId(),
            null,
            "Created dismissal request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        // notificationService.notifyDismissalRequestSubmitted(savedRequest);

        log.info("Successfully created dismissal request with ID: {}", savedRequest.getId());
        return dismissalRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('DO')")
    public DismissalRequestResponseDto approveDismissalRequest(String requestId, String approverId, String comments) {
        log.info("Approving dismissal request: {}", requestId);

        DismissalRequest request = getDismissalRequestById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending dismissal requests can be approved");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);

        // Update employee status to dismissed
        Employee employee = request.getEmployee();
        EmploymentStatus oldEmploymentStatus = employee.getEmploymentStatus();
        employee.setEmploymentStatus(EmploymentStatus.DISMISSED);
        employeeRepository.save(employee);

        DismissalRequest savedRequest = dismissalRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_DISMISSAL_REQUEST",
            "DismissalRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        auditService.logAction(
            approverId,
            approver.getUsername(),
            "UPDATE_EMPLOYEE_STATUS",
            "Employee",
            employee.getId(),
            oldEmploymentStatus.name(),
            employee.getEmploymentStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyDismissalRequestApproved(savedRequest);

        log.info("Successfully approved dismissal request: {}", requestId);
        return dismissalRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('DO')")
    public DismissalRequestResponseDto rejectDismissalRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting dismissal request: {}", requestId);

        DismissalRequest request = getDismissalRequestById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending dismissal requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        DismissalRequest savedRequest = dismissalRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_DISMISSAL_REQUEST",
            "DismissalRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyDismissalRequestRejected(savedRequest);

        log.info("Successfully rejected dismissal request: {}", requestId);
        return dismissalRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public DismissalRequestResponseDto updateDismissalRequest(String requestId, DismissalRequestUpdateDto updateDto) {
        log.info("Updating dismissal request: {}", requestId);

        DismissalRequest request = getDismissalRequestById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending dismissal requests can be updated");
        }

        DismissalRequestResponseDto oldData = dismissalRequestMapper.toResponseDto(request);

        // Update fields if provided
        if (updateDto.getDismissalReason() != null) {
            request.setDismissalReason(updateDto.getDismissalReason());
        }
        if (updateDto.getDetailedCharges() != null) {
            request.setDetailedCharges(updateDto.getDetailedCharges());
        }
        if (updateDto.getIncidentDate() != null) {
            request.setIncidentDate(updateDto.getIncidentDate());
        }
        if (updateDto.getInvestigationStartDate() != null) {
            request.setInvestigationStartDate(updateDto.getInvestigationStartDate());
        }
        if (updateDto.getInvestigationEndDate() != null) {
            request.setInvestigationEndDate(updateDto.getInvestigationEndDate());
        }
        if (updateDto.getInvestigationSummary() != null) {
            request.setInvestigationSummary(updateDto.getInvestigationSummary());
        }
        if (updateDto.getInvestigationOfficer() != null) {
            request.setInvestigationOfficer(updateDto.getInvestigationOfficer());
        }
        if (updateDto.getDisciplinaryHistory() != null) {
            request.setDisciplinaryHistory(updateDto.getDisciplinaryHistory());
        }
        if (updateDto.getPriorWarningsCount() != null) {
            request.setPriorWarningsCount(updateDto.getPriorWarningsCount());
        }
        if (updateDto.getShowCauseDate() != null) {
            request.setShowCauseDate(updateDto.getShowCauseDate());
        }
        if (updateDto.getEmployeeResponse() != null) {
            request.setEmployeeResponse(updateDto.getEmployeeResponse());
        }
        if (updateDto.getHearingDate() != null) {
            request.setHearingDate(updateDto.getHearingDate());
        }
        if (updateDto.getHearingOutcome() != null) {
            request.setHearingOutcome(updateDto.getHearingOutcome());
        }
        if (updateDto.getMitigatingFactors() != null) {
            request.setMitigatingFactors(updateDto.getMitigatingFactors());
        }
        if (updateDto.getAggravatingFactors() != null) {
            request.setAggravatingFactors(updateDto.getAggravatingFactors());
        }
        if (updateDto.getHrRecommendations() != null) {
            request.setHrRecommendations(updateDto.getHrRecommendations());
        }
        if (updateDto.getLegalConsultation() != null) {
            request.setLegalConsultation(updateDto.getLegalConsultation());
        }
        if (updateDto.getLegalAdvice() != null) {
            request.setLegalAdvice(updateDto.getLegalAdvice());
        }
        if (updateDto.getUnionNotificationDate() != null) {
            request.setUnionNotificationDate(updateDto.getUnionNotificationDate());
        }
        if (updateDto.getUnionResponse() != null) {
            request.setUnionResponse(updateDto.getUnionResponse());
        }
        if (updateDto.getFinalSettlementAmount() != null) {
            request.setFinalSettlementAmount(updateDto.getFinalSettlementAmount());
        }
        if (updateDto.getEffectiveDismissalDate() != null) {
            request.setEffectiveDismissalDate(updateDto.getEffectiveDismissalDate());
        }

        DismissalRequest savedRequest = dismissalRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            null, // Will be filled by security context
            null, // Will be filled by security context
            "UPDATE_DISMISSAL_REQUEST",
            "DismissalRequest",
            savedRequest.getId(),
            "Updated",
            "Updated",
            true,
            null
        );

        log.info("Successfully updated dismissal request: {}", requestId);
        return dismissalRequestMapper.toResponseDto(savedRequest);
    }

    public DismissalRequestResponseDto getDismissalRequestByIdDto(String requestId) {
        DismissalRequest request = dismissalRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Dismissal request not found with id: " + requestId));
        return dismissalRequestMapper.toResponseDto(request);
    }

    public Page<DismissalRequestResponseDto> getAllDismissalRequests(Pageable pageable) {
        Page<DismissalRequest> requests = dismissalRequestRepository.findAll(pageable);
        return requests.map(dismissalRequestMapper::toResponseDto);
    }

    public Page<DismissalRequestResponseDto> getDismissalRequestsByStatus(RequestStatus status, Pageable pageable) {
        Page<DismissalRequest> requests = dismissalRequestRepository.findByStatus(status, pageable);
        return requests.map(dismissalRequestMapper::toResponseDto);
    }

    public Page<DismissalRequestResponseDto> getDismissalRequestsByEmployee(String employeeId, Pageable pageable) {
        Page<DismissalRequest> requests = dismissalRequestRepository.findByEmployeeId(employeeId, pageable);
        return requests.map(dismissalRequestMapper::toResponseDto);
    }

    public Page<DismissalRequestResponseDto> getDismissalRequestsByInstitution(String institutionId, Pageable pageable) {
        Page<DismissalRequest> requests = dismissalRequestRepository.findByInstitutionId(institutionId, pageable);
        return requests.map(dismissalRequestMapper::toResponseDto);
    }

    public Page<DismissalRequestResponseDto> searchDismissalRequests(String searchTerm, Pageable pageable) {
        Page<DismissalRequest> requests = dismissalRequestRepository.searchDismissalRequests(searchTerm, pageable);
        return requests.map(dismissalRequestMapper::toResponseDto);
    }

    public List<DismissalRequest> findOverdueDismissalRequests(int slaDays) {
        LocalDateTime slaDeadline = LocalDateTime.now().minusDays(slaDays);
        return dismissalRequestRepository.findOverdueDismissalRequests(slaDeadline);
    }

    public List<DismissalRequest> findActiveInvestigations() {
        return dismissalRequestRepository.findActiveInvestigations();
    }

    public List<DismissalRequest> findRequestsWithExpiredAppealPeriod() {
        return dismissalRequestRepository.findRequestsWithExpiredAppealPeriod(LocalDateTime.now());
    }

    public List<DismissalRequest> findRequestsWithAppealPeriodExpiringSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime alertTime = now.plusDays(days);
        return dismissalRequestRepository.findRequestsWithAppealPeriodExpiringSoon(now, alertTime);
    }

    // Helper methods
    private Employee validateEmployeeForDismissal(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if employee is eligible for dismissal (confirmed employees only)
        if (employee.getEmploymentStatus() != EmploymentStatus.CONFIRMED) {
            throw new ValidationException("Only confirmed employees can be dismissed. Use termination process for unconfirmed employees.");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.DISMISSED) {
            throw new ValidationException("Employee is already dismissed");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.TERMINATED) {
            throw new ValidationException("Employee is already terminated");
        }

        // Check if there's already a pending dismissal request
        Boolean hasPendingDismissal = dismissalRequestRepository.hasActivePendingDismissal(employeeId);
        if (hasPendingDismissal) {
            throw new ValidationException("Employee already has a pending dismissal request");
        }

        return employee;
    }

    private void validateDismissalRequest(DismissalRequestCreateDto createDto, Employee employee) {
        DismissalReason reason = createDto.getDismissalReason();
        
        // Validate investigation requirements
        if (reason.requiresInvestigation()) {
            if (createDto.getInvestigationSummary() == null || createDto.getInvestigationSummary().trim().isEmpty()) {
                throw new ValidationException("Investigation summary is required for " + reason.getDescription());
            }
            
            if (createDto.getInvestigationOfficer() == null || createDto.getInvestigationOfficer().trim().isEmpty()) {
                throw new ValidationException("Investigation officer is required for " + reason.getDescription());
            }
        }
        
        // Validate prior warnings requirements
        if (reason.requiresPriorWarnings()) {
            if (createDto.getPriorWarningsCount() == null || createDto.getPriorWarningsCount() == 0) {
                throw new ValidationException("Prior warnings are required for " + reason.getDescription());
            }
            
            if (createDto.getDisciplinaryHistory() == null || createDto.getDisciplinaryHistory().trim().isEmpty()) {
                throw new ValidationException("Disciplinary history is required for " + reason.getDescription());
            }
        }

        // Validate that incident date is not in the future
        if (createDto.getIncidentDate() != null && createDto.getIncidentDate().isAfter(LocalDateTime.now().toLocalDate())) {
            throw new ValidationException("Incident date cannot be in the future");
        }

        // Validate criminal conviction cases require legal consultation
        if (reason == DismissalReason.CRIMINAL_CONVICTION || reason == DismissalReason.FRAUD_OR_THEFT) {
            if (createDto.getLegalConsultation() == null || !createDto.getLegalConsultation()) {
                throw new ValidationException("Legal consultation is mandatory for " + reason.getDescription());
            }
        }
    }

    private String generateRequestNumber() {
        // Generate unique request number with format: DIS-YYYY-NNNNNN
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = dismissalRequestRepository.count() + 1;
        return String.format("DIS-%s-%06d", year, count);
    }

    private DismissalRequest getDismissalRequestById(String requestId) {
        return dismissalRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Dismissal request not found with id: " + requestId));
    }
}