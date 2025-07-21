package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.resignation.ResignationRequestCreateDto;
import com.zanzibar.csms.dto.resignation.ResignationRequestResponseDto;
import com.zanzibar.csms.dto.resignation.ResignationRequestUpdateDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.ResignationRequest;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.ResignationType;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.ResignationRequestMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.ResignationRequestRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResignationRequestService {

    private final ResignationRequestRepository resignationRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ResignationRequestMapper resignationRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto createResignationRequest(ResignationRequestCreateDto createDto, String submittedById) {
        log.info("Creating resignation request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for resignation
        Employee employee = validateEmployeeForResignation(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate resignation request
        validateResignationRequest(createDto, employee);

        // Create resignation request
        ResignationRequest resignationRequest = new ResignationRequest();
        resignationRequest.setRequestNumber(generateRequestNumber());
        resignationRequest.setEmployee(employee);
        resignationRequest.setSubmittedBy(submittedBy);
        resignationRequest.setSubmissionDate(LocalDateTime.now());
        resignationRequest.setStatus(RequestStatus.SUBMITTED);
        resignationRequest.setResignationType(createDto.getResignationType());
        resignationRequest.setResignationDate(createDto.getResignationDate());
        resignationRequest.setLastWorkingDate(createDto.getLastWorkingDate());
        resignationRequest.setReason(createDto.getReason());
        resignationRequest.setPaymentAmount(createDto.getPaymentAmount());
        resignationRequest.setPaymentConfirmed(createDto.getPaymentConfirmed());
        resignationRequest.setClearanceCompleted(false);
        resignationRequest.setHandoverCompleted(false);

        ResignationRequest savedRequest = resignationRequestRepository.save(resignationRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_RESIGNATION_REQUEST",
            "ResignationRequest",
            savedRequest.getId(),
            null,
            "Created resignation request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        // notificationService.notifyResignationRequestSubmitted(savedRequest);

        log.info("Successfully created resignation request with ID: {}", savedRequest.getId());
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto approveResignationRequest(String requestId, String approverId, String comments) {
        log.info("Approving resignation request: {}", requestId);

        ResignationRequest request = getResignationRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending resignation requests can be approved");
        }

        // Validate request has required documents
        if (!request.hasRequiredDocuments()) {
            throw new ValidationException("Request is missing required documents. Please ensure all mandatory documents are uploaded.");
        }

        // For immediate resignation with payment, validate payment is confirmed
        if (request.getResignationType() == ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT) {
            if (!Boolean.TRUE.equals(request.getPaymentConfirmed())) {
                throw new ValidationException("Payment must be confirmed for immediate resignation requests");
            }
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);

        // Update employee status to resigned
        Employee employee = request.getEmployee();
        employee.setEmploymentStatus(EmploymentStatus.RESIGNED);
        employeeRepository.save(employee);

        ResignationRequest savedRequest = resignationRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_RESIGNATION_REQUEST",
            "ResignationRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyResignationRequestApproved(savedRequest);

        log.info("Successfully approved resignation request: {}", requestId);
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto rejectResignationRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting resignation request: {}", requestId);

        ResignationRequest request = getResignationRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending resignation requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        ResignationRequest savedRequest = resignationRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_RESIGNATION_REQUEST",
            "ResignationRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyResignationRequestRejected(savedRequest);

        log.info("Successfully rejected resignation request: {}", requestId);
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public ResignationRequestResponseDto updateResignationRequest(String requestId, ResignationRequestUpdateDto updateDto) {
        log.info("Updating resignation request: {}", requestId);

        ResignationRequest request = getResignationRequestEntityById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending resignation requests can be updated");
        }

        // Update fields if provided
        if (updateDto.getResignationType() != null) {
            request.setResignationType(updateDto.getResignationType());
        }
        if (updateDto.getResignationDate() != null) {
            request.setResignationDate(updateDto.getResignationDate());
        }
        if (updateDto.getLastWorkingDate() != null) {
            request.setLastWorkingDate(updateDto.getLastWorkingDate());
        }
        if (updateDto.getReason() != null) {
            request.setReason(updateDto.getReason());
        }
        if (updateDto.getPaymentAmount() != null) {
            request.setPaymentAmount(updateDto.getPaymentAmount());
        }
        if (updateDto.getPaymentConfirmed() != null) {
            request.setPaymentConfirmed(updateDto.getPaymentConfirmed());
        }
        if (updateDto.getClearanceCompleted() != null) {
            request.setClearanceCompleted(updateDto.getClearanceCompleted());
        }
        if (updateDto.getHandoverCompleted() != null) {
            request.setHandoverCompleted(updateDto.getHandoverCompleted());
        }

        ResignationRequest savedRequest = resignationRequestRepository.save(request);

        log.info("Successfully updated resignation request: {}", requestId);
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto confirmPayment(String requestId, String confirmerId, BigDecimal paymentAmount) {
        log.info("Confirming payment for resignation request: {}", requestId);

        ResignationRequest request = getResignationRequestEntityById(requestId);
        User confirmer = userRepository.findById(confirmerId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + confirmerId));

        if (request.getResignationType() != ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT) {
            throw new ValidationException("Payment confirmation is only applicable for immediate resignation requests");
        }

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Cannot confirm payment for finalized requests");
        }

        request.setPaymentAmount(paymentAmount);
        request.setPaymentConfirmed(true);

        ResignationRequest savedRequest = resignationRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            confirmerId,
            confirmer.getUsername(),
            "CONFIRM_RESIGNATION_PAYMENT",
            "ResignationRequest",
            savedRequest.getId(),
            "Payment Amount: " + paymentAmount.toString(),
            "Payment Confirmed",
            true,
            null
        );

        log.info("Successfully confirmed payment for resignation request: {}", requestId);
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto completeClearance(String requestId, String completerId) {
        log.info("Completing clearance for resignation request: {}", requestId);

        ResignationRequest request = getResignationRequestEntityById(requestId);
        User completer = userRepository.findById(completerId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + completerId));

        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new ValidationException("Clearance can only be completed for approved requests");
        }

        request.setClearanceCompleted(true);
        ResignationRequest savedRequest = resignationRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            completerId,
            completer.getUsername(),
            "COMPLETE_RESIGNATION_CLEARANCE",
            "ResignationRequest",
            savedRequest.getId(),
            null,
            "Clearance Completed",
            true,
            null
        );

        log.info("Successfully completed clearance for resignation request: {}", requestId);
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto completeHandover(String requestId, String completerId) {
        log.info("Completing handover for resignation request: {}", requestId);

        ResignationRequest request = getResignationRequestEntityById(requestId);
        User completer = userRepository.findById(completerId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + completerId));

        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new ValidationException("Handover can only be completed for approved requests");
        }

        request.setHandoverCompleted(true);
        ResignationRequest savedRequest = resignationRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            completerId,
            completer.getUsername(),
            "COMPLETE_RESIGNATION_HANDOVER",
            "ResignationRequest",
            savedRequest.getId(),
            null,
            "Handover Completed",
            true,
            null
        );

        log.info("Successfully completed handover for resignation request: {}", requestId);
        return resignationRequestMapper.toResponseDto(savedRequest);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResignationRequestResponseDto getResignationRequestById(String requestId) {
        log.info("Getting resignation request by ID: {}", requestId);
        
        ResignationRequest request = getResignationRequestEntityById(requestId);
        return resignationRequestMapper.toResponseDto(request);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<ResignationRequestResponseDto> getAllResignationRequests(Pageable pageable) {
        log.info("Getting all resignation requests");
        
        return resignationRequestRepository.findAll(pageable)
            .map(resignationRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<ResignationRequestResponseDto> getResignationRequestsByStatus(RequestStatus status, Pageable pageable) {
        log.info("Getting resignation requests by status: {}", status);
        
        return resignationRequestRepository.findByStatus(status, pageable)
            .map(resignationRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<ResignationRequestResponseDto> getResignationRequestsByEmployee(String employeeId, Pageable pageable) {
        log.info("Getting resignation requests by employee: {}", employeeId);
        
        return resignationRequestRepository.findByEmployeeId(employeeId, pageable)
            .map(resignationRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<ResignationRequestResponseDto> getResignationRequestsByType(ResignationType resignationType, Pageable pageable) {
        log.info("Getting resignation requests by type: {}", resignationType);
        
        return resignationRequestRepository.findByResignationType(resignationType, pageable)
            .map(resignationRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<ResignationRequestResponseDto> searchResignationRequests(String searchTerm, Pageable pageable) {
        log.info("Searching resignation requests with term: {}", searchTerm);
        
        return resignationRequestRepository.searchResignationRequests(searchTerm, pageable)
            .map(resignationRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public List<ResignationRequestResponseDto> getUpcomingResignations(LocalDate targetDate) {
        log.info("Getting upcoming resignations for date: {}", targetDate);
        
        return resignationRequestRepository.findUpcomingResignations(targetDate)
            .stream()
            .map(resignationRequestMapper::toResponseDto)
            .toList();
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public Page<ResignationRequestResponseDto> getPendingPaymentRequests(Pageable pageable) {
        log.info("Getting pending payment requests");
        
        return resignationRequestRepository.findPendingPaymentRequests(pageable)
            .map(resignationRequestMapper::toResponseDto);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public List<ResignationRequestResponseDto> getPendingClearanceRequests() {
        log.info("Getting pending clearance requests");
        
        return resignationRequestRepository.findPendingClearanceRequests()
            .stream()
            .map(resignationRequestMapper::toResponseDto)
            .toList();
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public List<ResignationRequestResponseDto> getPendingHandoverRequests() {
        log.info("Getting pending handover requests");
        
        return resignationRequestRepository.findPendingHandoverRequests()
            .stream()
            .map(resignationRequestMapper::toResponseDto)
            .toList();
    }

    // Statistics methods
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public long countResignationRequestsByStatus(RequestStatus status) {
        return resignationRequestRepository.countByStatus(status);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public long countResignationRequestsByType(ResignationType resignationType) {
        return resignationRequestRepository.countByResignationType(resignationType);
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public BigDecimal getTotalPaymentAmount() {
        return resignationRequestRepository.getTotalPaymentAmount();
    }

    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public BigDecimal getAveragePaymentAmount() {
        return resignationRequestRepository.getAveragePaymentAmount();
    }

    // Private helper methods
    private ResignationRequest getResignationRequestEntityById(String requestId) {
        return resignationRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Resignation request not found with id: " + requestId));
    }

    private Employee validateEmployeeForResignation(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (employee.getEmploymentStatus() != EmploymentStatus.ACTIVE) {
            throw new ValidationException("Only active employees can submit resignation requests");
        }

        // Check if employee already has a pending resignation
        if (Boolean.TRUE.equals(resignationRequestRepository.hasActivePendingResignation(employeeId))) {
            throw new ValidationException("Employee already has a pending resignation request");
        }

        return employee;
    }

    private void validateResignationRequest(ResignationRequestCreateDto createDto, Employee employee) {
        if (createDto.getResignationType() == ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT) {
            if (createDto.getPaymentAmount() == null || createDto.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Payment amount is required for immediate resignation requests");
            }
        }

        if (createDto.getResignationDate() != null && createDto.getLastWorkingDate() != null) {
            if (createDto.getLastWorkingDate().isBefore(createDto.getResignationDate())) {
                throw new ValidationException("Last working date cannot be before resignation date");
            }
        }
    }

    private String generateRequestNumber() {
        return "RES-" + System.currentTimeMillis();
    }
}