package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.promotion.PromotionRequestCreateDto;
import com.zanzibar.csms.dto.promotion.PromotionRequestResponseDto;
import com.zanzibar.csms.dto.promotion.PromotionRequestUpdateDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.PromotionRequest;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.PromotionType;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.PromotionEligibilityException;
import com.zanzibar.csms.exception.PromotionValidationException;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.PromotionRequestMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.PromotionRequestRepository;
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
public class PromotionRequestService {

    private final PromotionRequestRepository promotionRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PromotionRequestMapper promotionRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public PromotionRequestResponseDto createPromotionRequest(PromotionRequestCreateDto createDto, String submittedById) {
        log.info("Creating promotion request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for promotion
        Employee employee = validateEmployeeForPromotion(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate promotion request
        validatePromotionRequest(createDto);

        // Create promotion request
        PromotionRequest promotionRequest = new PromotionRequest();
        promotionRequest.setRequestNumber(generateRequestNumber());
        promotionRequest.setEmployee(employee);
        promotionRequest.setSubmittedBy(submittedBy);
        promotionRequest.setSubmissionDate(LocalDateTime.now());
        promotionRequest.setStatus(RequestStatus.SUBMITTED);
        promotionRequest.setPromotionType(createDto.getPromotionType());
        promotionRequest.setCurrentPosition(createDto.getCurrentPosition());
        promotionRequest.setCurrentGrade(createDto.getCurrentGrade());
        promotionRequest.setProposedPosition(createDto.getProposedPosition());
        promotionRequest.setProposedGrade(createDto.getProposedGrade());
        promotionRequest.setCurrentSalary(createDto.getCurrentSalary());
        promotionRequest.setProposedSalary(createDto.getProposedSalary());
        promotionRequest.setEffectiveDate(createDto.getEffectiveDate());
        promotionRequest.setJustification(createDto.getJustification());
        promotionRequest.setPerformanceRating(createDto.getPerformanceRating());
        promotionRequest.setYearsInCurrentPosition(createDto.getYearsInCurrentPosition());
        promotionRequest.setQualificationsMet(createDto.getQualificationsMet());
        promotionRequest.setSupervisorRecommendation(createDto.getSupervisorRecommendation());

        PromotionRequest savedRequest = promotionRequestRepository.save(promotionRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_PROMOTION_REQUEST",
            "PromotionRequest",
            savedRequest.getId(),
            null,
            "Created promotion request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        // notificationService.notifyPromotionRequestSubmitted(savedRequest);

        log.info("Successfully created promotion request with ID: {}", savedRequest.getId());
        return promotionRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public PromotionRequestResponseDto approvePromotionRequest(String requestId, String approverId, String comments) {
        log.info("Approving promotion request: {}", requestId);

        PromotionRequest request = getPromotionRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending promotion requests can be approved");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);

        // Update employee information after approval
        Employee employee = request.getEmployee();
        employee.setRank(request.getProposedGrade());
        // Update other employee fields as needed based on promotion
        employeeRepository.save(employee);

        PromotionRequest savedRequest = promotionRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_PROMOTION_REQUEST",
            "PromotionRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyPromotionRequestApproved(savedRequest);

        log.info("Successfully approved promotion request: {}", requestId);
        return promotionRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public PromotionRequestResponseDto rejectPromotionRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting promotion request: {}", requestId);

        PromotionRequest request = getPromotionRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending promotion requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        PromotionRequest savedRequest = promotionRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_PROMOTION_REQUEST",
            "PromotionRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyPromotionRequestRejected(savedRequest);

        log.info("Successfully rejected promotion request: {}", requestId);
        return promotionRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public PromotionRequestResponseDto updatePromotionRequest(String requestId, PromotionRequestUpdateDto updateDto) {
        log.info("Updating promotion request: {}", requestId);

        PromotionRequest request = getPromotionRequestEntityById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending promotion requests can be updated");
        }

        // Update fields if provided
        if (updateDto.getPromotionType() != null) {
            request.setPromotionType(updateDto.getPromotionType());
        }
        if (updateDto.getCurrentPosition() != null) {
            request.setCurrentPosition(updateDto.getCurrentPosition());
        }
        if (updateDto.getCurrentGrade() != null) {
            request.setCurrentGrade(updateDto.getCurrentGrade());
        }
        if (updateDto.getProposedPosition() != null) {
            request.setProposedPosition(updateDto.getProposedPosition());
        }
        if (updateDto.getProposedGrade() != null) {
            request.setProposedGrade(updateDto.getProposedGrade());
        }
        if (updateDto.getCurrentSalary() != null) {
            request.setCurrentSalary(updateDto.getCurrentSalary());
        }
        if (updateDto.getProposedSalary() != null) {
            request.setProposedSalary(updateDto.getProposedSalary());
        }
        if (updateDto.getEffectiveDate() != null) {
            request.setEffectiveDate(updateDto.getEffectiveDate());
        }
        if (updateDto.getJustification() != null) {
            request.setJustification(updateDto.getJustification());
        }
        if (updateDto.getPerformanceRating() != null) {
            request.setPerformanceRating(updateDto.getPerformanceRating());
        }
        if (updateDto.getYearsInCurrentPosition() != null) {
            request.setYearsInCurrentPosition(updateDto.getYearsInCurrentPosition());
        }
        if (updateDto.getQualificationsMet() != null) {
            request.setQualificationsMet(updateDto.getQualificationsMet());
        }
        if (updateDto.getSupervisorRecommendation() != null) {
            request.setSupervisorRecommendation(updateDto.getSupervisorRecommendation());
        }

        PromotionRequest savedRequest = promotionRequestRepository.save(request);

        log.info("Successfully updated promotion request: {}", requestId);
        return promotionRequestMapper.toResponseDto(savedRequest);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public PromotionRequestResponseDto getPromotionRequestById(String requestId) {
        log.info("Getting promotion request by ID: {}", requestId);
        
        PromotionRequest request = getPromotionRequestEntityById(requestId);
        return promotionRequestMapper.toResponseDto(request);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<PromotionRequestResponseDto> getAllPromotionRequests(Pageable pageable) {
        log.info("Getting all promotion requests");
        
        return promotionRequestRepository.findAll(pageable)
            .map(promotionRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<PromotionRequestResponseDto> getPromotionRequestsByStatus(RequestStatus status, Pageable pageable) {
        log.info("Getting promotion requests by status: {}", status);
        
        return promotionRequestRepository.findByStatus(status, pageable)
            .map(promotionRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<PromotionRequestResponseDto> getPromotionRequestsByEmployee(String employeeId, Pageable pageable) {
        log.info("Getting promotion requests by employee: {}", employeeId);
        
        return promotionRequestRepository.findByEmployeeId(employeeId, pageable)
            .map(promotionRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<PromotionRequestResponseDto> getPromotionRequestsByType(PromotionType promotionType, Pageable pageable) {
        log.info("Getting promotion requests by type: {}", promotionType);
        
        return promotionRequestRepository.findByPromotionType(promotionType, pageable)
            .map(promotionRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<PromotionRequestResponseDto> searchPromotionRequests(String searchTerm, Pageable pageable) {
        log.info("Searching promotion requests with term: {}", searchTerm);
        
        return promotionRequestRepository.searchPromotionRequests(searchTerm, pageable)
            .map(promotionRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public List<PromotionRequestResponseDto> getUpcomingPromotions(LocalDate targetDate) {
        log.info("Getting upcoming promotions for date: {}", targetDate);
        
        return promotionRequestRepository.findUpcomingPromotions(targetDate)
            .stream()
            .map(promotionRequestMapper::toResponseDto)
            .toList();
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public List<PromotionRequestResponseDto> getEmployeePromotionHistory(String employeeId) {
        log.info("Getting promotion history for employee: {}", employeeId);
        
        return promotionRequestRepository.findEmployeePromotionHistory(employeeId)
            .stream()
            .map(promotionRequestMapper::toResponseDto)
            .toList();
    }

    // Statistics methods
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public long countPromotionRequestsByStatus(RequestStatus status) {
        return promotionRequestRepository.countByStatus(status);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public long countPromotionRequestsByType(PromotionType promotionType) {
        return promotionRequestRepository.countByPromotionType(promotionType);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Double getAverageSalaryIncrease() {
        return promotionRequestRepository.getAverageSalaryIncrease();
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Double getTotalSalaryIncrease() {
        return promotionRequestRepository.getTotalSalaryIncrease();
    }

    // Private helper methods
    private PromotionRequest getPromotionRequestEntityById(String requestId) {
        return promotionRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Promotion request not found with id: " + requestId));
    }

    private Employee validateEmployeeForPromotion(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (employee.getEmploymentStatus() != EmploymentStatus.ACTIVE) {
            throw new PromotionEligibilityException("Only active employees can be promoted");
        }

        // Check if employee already has a pending promotion
        if (Boolean.TRUE.equals(promotionRequestRepository.hasActivePendingPromotion(employeeId))) {
            throw new PromotionEligibilityException("Employee already has a pending promotion request");
        }

        // Check if employee has an approved promotion that hasn't taken effect yet
        if (Boolean.TRUE.equals(promotionRequestRepository.hasApprovedPromotionPending(employeeId, LocalDate.now()))) {
            throw new PromotionEligibilityException("Employee has an approved promotion that hasn't taken effect yet");
        }

        return employee;
    }

    private void validatePromotionRequest(PromotionRequestCreateDto createDto) {
        // Validate minimum years in current position (FR8.3: minimum 2 years)
        if (createDto.getYearsInCurrentPosition() == null || createDto.getYearsInCurrentPosition() < 2) {
            throw new PromotionEligibilityException("Employee must have served minimum 2 years in current position");
        }

        // Validate promotion type specific requirements
        if (createDto.getPromotionType() == PromotionType.EDUCATIONAL) {
            validateEducationalPromotion(createDto);
        } else if (createDto.getPromotionType() == PromotionType.PERFORMANCE) {
            validatePerformancePromotion(createDto);
        }

        // Validate salary increase
        if (createDto.getCurrentSalary() != null && createDto.getProposedSalary() != null) {
            if (createDto.getProposedSalary() <= createDto.getCurrentSalary()) {
                throw new PromotionValidationException("Proposed salary must be greater than current salary");
            }
            
            // Check maximum salary increase (50% cap)
            double increasePercentage = ((createDto.getProposedSalary() - createDto.getCurrentSalary()) / createDto.getCurrentSalary()) * 100;
            if (increasePercentage > 50) {
                throw new PromotionValidationException("Salary increase cannot exceed 50% of current salary");
            }
        }

        // Validate position progression
        if (createDto.getCurrentPosition() != null && createDto.getProposedPosition() != null && 
            createDto.getCurrentPosition().equals(createDto.getProposedPosition())) {
            throw new PromotionValidationException("Proposed position must be different from current position");
        }

        // Validate grade progression
        if (createDto.getCurrentGrade() != null && createDto.getProposedGrade() != null && 
            createDto.getCurrentGrade().equals(createDto.getProposedGrade())) {
            throw new PromotionValidationException("Proposed grade must be different from current grade");
        }

        // Validate effective date
        if (createDto.getEffectiveDate() != null && createDto.getEffectiveDate().isBefore(LocalDate.now())) {
            throw new PromotionValidationException("Effective date must be in the future");
        }
    }

    private void validateEducationalPromotion(PromotionRequestCreateDto createDto) {
        // For educational promotions, qualifications must be specified
        if (createDto.getQualificationsMet() == null || createDto.getQualificationsMet().trim().isEmpty()) {
            throw new PromotionValidationException("Educational qualifications must be specified for educational promotions");
        }
    }

    private void validatePerformancePromotion(PromotionRequestCreateDto createDto) {
        // For performance promotions, performance rating is required
        if (createDto.getPerformanceRating() == null || createDto.getPerformanceRating().trim().isEmpty()) {
            throw new PromotionValidationException("Performance rating is required for performance promotions");
        }
        
        // Performance rating must be satisfactory or above
        if ("UNSATISFACTORY".equals(createDto.getPerformanceRating()) || 
            "NEEDS_IMPROVEMENT".equals(createDto.getPerformanceRating())) {
            throw new PromotionEligibilityException("Performance rating must be satisfactory or above for promotion");
        }
        
        // Supervisor recommendation is required
        if (createDto.getSupervisorRecommendation() == null || createDto.getSupervisorRecommendation().trim().isEmpty()) {
            throw new PromotionValidationException("Supervisor recommendation is required for performance promotions");
        }
    }

    private String generateRequestNumber() {
        return "PROM-" + System.currentTimeMillis();
    }
}