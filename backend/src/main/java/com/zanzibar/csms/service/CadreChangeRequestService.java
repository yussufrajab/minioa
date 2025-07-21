package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.cadre.CadreChangeRequestCreateDto;
import com.zanzibar.csms.dto.cadre.CadreChangeRequestResponseDto;
import com.zanzibar.csms.dto.cadre.CadreChangeRequestUpdateDto;
import com.zanzibar.csms.entity.CadreChangeRequest;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.CadreChangeRequestMapper;
import com.zanzibar.csms.repository.CadreChangeRequestRepository;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CadreChangeRequestService {

    private final CadreChangeRequestRepository cadreChangeRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CadreChangeRequestMapper cadreChangeRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public CadreChangeRequestResponseDto createCadreChangeRequest(CadreChangeRequestCreateDto createDto, String submittedById) {
        log.info("Creating cadre change request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for cadre change
        Employee employee = validateEmployeeForCadreChange(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate cadre change request
        validateCadreChangeRequest(createDto, employee);

        // Create cadre change request
        CadreChangeRequest cadreChangeRequest = new CadreChangeRequest();
        cadreChangeRequest.setRequestNumber(generateRequestNumber());
        cadreChangeRequest.setEmployee(employee);
        cadreChangeRequest.setSubmittedBy(submittedBy);
        cadreChangeRequest.setSubmissionDate(LocalDateTime.now());
        cadreChangeRequest.setStatus(RequestStatus.SUBMITTED);
        cadreChangeRequest.setCurrentCadre(createDto.getCurrentCadre());
        cadreChangeRequest.setProposedCadre(createDto.getProposedCadre());
        cadreChangeRequest.setEducationLevel(createDto.getEducationLevel());
        cadreChangeRequest.setEducationCompletionYear(createDto.getEducationCompletionYear());
        cadreChangeRequest.setInstitutionAttended(createDto.getInstitutionAttended());
        cadreChangeRequest.setQualificationObtained(createDto.getQualificationObtained());
        cadreChangeRequest.setJustification(createDto.getJustification());
        cadreChangeRequest.setCurrentSalaryScale(createDto.getCurrentSalaryScale());
        cadreChangeRequest.setProposedSalaryScale(createDto.getProposedSalaryScale());
        cadreChangeRequest.setYearsOfExperience(createDto.getYearsOfExperience());
        cadreChangeRequest.setRelevantExperience(createDto.getRelevantExperience());
        cadreChangeRequest.setTrainingCompleted(createDto.getTrainingCompleted());
        cadreChangeRequest.setSkillsAcquired(createDto.getSkillsAcquired());
        cadreChangeRequest.setPerformanceRating(createDto.getPerformanceRating());
        cadreChangeRequest.setSupervisorRecommendation(createDto.getSupervisorRecommendation());
        cadreChangeRequest.setEffectiveDate(createDto.getEffectiveDate());
        cadreChangeRequest.setTcuVerificationRequired(createDto.getTcuVerificationRequired());
        cadreChangeRequest.setTcuVerificationStatus(createDto.getTcuVerificationStatus());
        cadreChangeRequest.setTcuVerificationDate(createDto.getTcuVerificationDate());
        cadreChangeRequest.setHrAssessment(createDto.getHrAssessment());
        cadreChangeRequest.setBudgetaryImplications(createDto.getBudgetaryImplications());

        CadreChangeRequest savedRequest = cadreChangeRequestRepository.save(cadreChangeRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_CADRE_CHANGE_REQUEST",
            "CadreChangeRequest",
            savedRequest.getId(),
            null,
            "Created cadre change request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        // notificationService.notifyCadreChangeRequestSubmitted(savedRequest);

        log.info("Successfully created cadre change request with ID: {}", savedRequest.getId());
        return cadreChangeRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public CadreChangeRequestResponseDto approveCadreChangeRequest(String requestId, String approverId, String comments) {
        log.info("Approving cadre change request: {}", requestId);

        CadreChangeRequest request = getCadreChangeRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending cadre change requests can be approved");
        }

        // Validate request is eligible for processing
        if (!request.isEligibleForProcessing()) {
            throw new ValidationException("Request is not eligible for processing. Please ensure all required documents are uploaded and verified.");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);

        // Update employee cadre information
        Employee employee = request.getEmployee();
        String oldCadre = employee.getRank(); // Assuming rank field stores cadre
        employee.setRank(request.getProposedCadre());
        employeeRepository.save(employee);

        CadreChangeRequest savedRequest = cadreChangeRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_CADRE_CHANGE_REQUEST",
            "CadreChangeRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        auditService.logAction(
            approverId,
            approver.getUsername(),
            "UPDATE_EMPLOYEE_CADRE",
            "Employee",
            employee.getId(),
            oldCadre,
            employee.getRank(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyCadreChangeRequestApproved(savedRequest);

        log.info("Successfully approved cadre change request: {}", requestId);
        return cadreChangeRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public CadreChangeRequestResponseDto rejectCadreChangeRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting cadre change request: {}", requestId);

        CadreChangeRequest request = getCadreChangeRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending cadre change requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        CadreChangeRequest savedRequest = cadreChangeRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_CADRE_CHANGE_REQUEST",
            "CadreChangeRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyCadreChangeRequestRejected(savedRequest);

        log.info("Successfully rejected cadre change request: {}", requestId);
        return cadreChangeRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public CadreChangeRequestResponseDto updateCadreChangeRequest(String requestId, CadreChangeRequestUpdateDto updateDto) {
        log.info("Updating cadre change request: {}", requestId);

        CadreChangeRequest request = getCadreChangeRequestEntityById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending cadre change requests can be updated");
        }

        // Update fields if provided
        if (updateDto.getCurrentCadre() != null) {
            request.setCurrentCadre(updateDto.getCurrentCadre());
        }
        if (updateDto.getProposedCadre() != null) {
            request.setProposedCadre(updateDto.getProposedCadre());
        }
        if (updateDto.getEducationLevel() != null) {
            request.setEducationLevel(updateDto.getEducationLevel());
        }
        if (updateDto.getEducationCompletionYear() != null) {
            request.setEducationCompletionYear(updateDto.getEducationCompletionYear());
        }
        if (updateDto.getInstitutionAttended() != null) {
            request.setInstitutionAttended(updateDto.getInstitutionAttended());
        }
        if (updateDto.getQualificationObtained() != null) {
            request.setQualificationObtained(updateDto.getQualificationObtained());
        }
        if (updateDto.getJustification() != null) {
            request.setJustification(updateDto.getJustification());
        }
        if (updateDto.getCurrentSalaryScale() != null) {
            request.setCurrentSalaryScale(updateDto.getCurrentSalaryScale());
        }
        if (updateDto.getProposedSalaryScale() != null) {
            request.setProposedSalaryScale(updateDto.getProposedSalaryScale());
        }
        if (updateDto.getYearsOfExperience() != null) {
            request.setYearsOfExperience(updateDto.getYearsOfExperience());
        }
        if (updateDto.getRelevantExperience() != null) {
            request.setRelevantExperience(updateDto.getRelevantExperience());
        }
        if (updateDto.getTrainingCompleted() != null) {
            request.setTrainingCompleted(updateDto.getTrainingCompleted());
        }
        if (updateDto.getSkillsAcquired() != null) {
            request.setSkillsAcquired(updateDto.getSkillsAcquired());
        }
        if (updateDto.getPerformanceRating() != null) {
            request.setPerformanceRating(updateDto.getPerformanceRating());
        }
        if (updateDto.getSupervisorRecommendation() != null) {
            request.setSupervisorRecommendation(updateDto.getSupervisorRecommendation());
        }
        if (updateDto.getEffectiveDate() != null) {
            request.setEffectiveDate(updateDto.getEffectiveDate());
        }
        if (updateDto.getTcuVerificationRequired() != null) {
            request.setTcuVerificationRequired(updateDto.getTcuVerificationRequired());
        }
        if (updateDto.getTcuVerificationStatus() != null) {
            request.setTcuVerificationStatus(updateDto.getTcuVerificationStatus());
        }
        if (updateDto.getTcuVerificationDate() != null) {
            request.setTcuVerificationDate(updateDto.getTcuVerificationDate());
        }
        if (updateDto.getHrAssessment() != null) {
            request.setHrAssessment(updateDto.getHrAssessment());
        }
        if (updateDto.getBudgetaryImplications() != null) {
            request.setBudgetaryImplications(updateDto.getBudgetaryImplications());
        }

        CadreChangeRequest savedRequest = cadreChangeRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            null, // Will be filled by security context
            null, // Will be filled by security context
            "UPDATE_CADRE_CHANGE_REQUEST",
            "CadreChangeRequest",
            savedRequest.getId(),
            "Updated",
            "Updated",
            true,
            null
        );

        log.info("Successfully updated cadre change request: {}", requestId);
        return cadreChangeRequestMapper.toResponseDto(savedRequest);
    }

    public CadreChangeRequestResponseDto getCadreChangeRequestById(String requestId) {
        CadreChangeRequest request = getCadreChangeRequestEntityById(requestId);
        return cadreChangeRequestMapper.toResponseDto(request);
    }

    public Page<CadreChangeRequestResponseDto> getAllCadreChangeRequests(Pageable pageable) {
        Page<CadreChangeRequest> requests = cadreChangeRequestRepository.findAll(pageable);
        return requests.map(cadreChangeRequestMapper::toResponseDto);
    }

    public Page<CadreChangeRequestResponseDto> getCadreChangeRequestsByStatus(RequestStatus status, Pageable pageable) {
        Page<CadreChangeRequest> requests = cadreChangeRequestRepository.findByStatus(status, pageable);
        return requests.map(cadreChangeRequestMapper::toResponseDto);
    }

    public Page<CadreChangeRequestResponseDto> getCadreChangeRequestsByEmployee(String employeeId, Pageable pageable) {
        Page<CadreChangeRequest> requests = cadreChangeRequestRepository.findByEmployeeId(employeeId, pageable);
        return requests.map(cadreChangeRequestMapper::toResponseDto);
    }

    public Page<CadreChangeRequestResponseDto> getCadreChangeRequestsByInstitution(String institutionId, Pageable pageable) {
        Page<CadreChangeRequest> requests = cadreChangeRequestRepository.findByInstitutionId(institutionId, pageable);
        return requests.map(cadreChangeRequestMapper::toResponseDto);
    }

    public Page<CadreChangeRequestResponseDto> searchCadreChangeRequests(String searchTerm, Pageable pageable) {
        Page<CadreChangeRequest> requests = cadreChangeRequestRepository.searchCadreChangeRequests(searchTerm, pageable);
        return requests.map(cadreChangeRequestMapper::toResponseDto);
    }

    public List<CadreChangeRequest> findOverdueCadreChangeRequests(int slaDays) {
        LocalDateTime slaDeadline = LocalDateTime.now().minusDays(slaDays);
        return cadreChangeRequestRepository.findOverdueCadreChangeRequests(slaDeadline);
    }

    public List<CadreChangeRequest> findRequestsRequiringTcuVerification() {
        return cadreChangeRequestRepository.findRequestsRequiringTcuVerification(Pageable.unpaged()).getContent();
    }

    public List<CadreChangeRequest> findRequestsForImplementation() {
        return cadreChangeRequestRepository.findApprovedRequestsForImplementation(LocalDate.now());
    }

    // Helper methods
    private Employee validateEmployeeForCadreChange(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if employee is eligible for cadre change
        if (employee.getEmploymentStatus() != EmploymentStatus.CONFIRMED) {
            throw new ValidationException("Only confirmed employees are eligible for cadre change");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.TERMINATED) {
            throw new ValidationException("Terminated employees cannot request cadre change");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.DISMISSED) {
            throw new ValidationException("Dismissed employees cannot request cadre change");
        }

        // Check if there's already a pending cadre change request
        Boolean hasPendingCadreChange = cadreChangeRequestRepository.hasActivePendingCadreChange(employeeId);
        if (hasPendingCadreChange) {
            throw new ValidationException("Employee already has a pending cadre change request");
        }

        return employee;
    }

    private void validateCadreChangeRequest(CadreChangeRequestCreateDto createDto, Employee employee) {
        // Validate current cadre matches employee's current cadre
        if (employee.getRank() != null && !employee.getRank().equals(createDto.getCurrentCadre())) {
            throw new ValidationException("Current cadre does not match employee's current cadre");
        }

        // Validate education completion year
        if (createDto.getEducationCompletionYear() != null) {
            int currentYear = LocalDate.now().getYear();
            if (createDto.getEducationCompletionYear() > currentYear) {
                throw new ValidationException("Education completion year cannot be in the future");
            }
        }

        // Validate minimum experience requirement
        if (createDto.getYearsOfExperience() != null && createDto.getYearsOfExperience() < 2) {
            throw new ValidationException("Minimum 2 years of experience required for cadre change");
        }

        // Validate effective date
        if (createDto.getEffectiveDate() != null && createDto.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Effective date must be in the future");
        }
    }

    private String generateRequestNumber() {
        // Generate unique request number with format: CC-YYYY-NNNNNN
        String year = String.valueOf(LocalDate.now().getYear());
        long count = cadreChangeRequestRepository.count() + 1;
        return String.format("CC-%s-%06d", year, count);
    }
    
    private CadreChangeRequest getCadreChangeRequestEntityById(String requestId) {
        return cadreChangeRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Cadre change request not found with id: " + requestId));
    }
}