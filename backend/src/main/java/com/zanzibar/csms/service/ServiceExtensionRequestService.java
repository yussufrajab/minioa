package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestCreateDto;
import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestResponseDto;
import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestUpdateDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.ServiceExtensionRequest;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.ServiceExtensionRequestMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.ServiceExtensionRequestRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServiceExtensionRequestService {

    private final ServiceExtensionRequestRepository serviceExtensionRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ServiceExtensionRequestMapper serviceExtensionRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ServiceExtensionRequestResponseDto createServiceExtensionRequest(ServiceExtensionRequestCreateDto createDto, String submittedById) {
        log.info("Creating service extension request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for service extension
        Employee employee = validateEmployeeForServiceExtension(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate service extension request
        validateServiceExtensionRequest(createDto, employee);

        // Create service extension request
        ServiceExtensionRequest serviceExtensionRequest = new ServiceExtensionRequest();
        serviceExtensionRequest.setRequestNumber(generateRequestNumber());
        serviceExtensionRequest.setEmployee(employee);
        serviceExtensionRequest.setSubmittedBy(submittedBy);
        serviceExtensionRequest.setSubmissionDate(LocalDateTime.now());
        serviceExtensionRequest.setStatus(RequestStatus.SUBMITTED);
        serviceExtensionRequest.setExtensionDurationYears(createDto.getExtensionDurationYears());
        serviceExtensionRequest.setExpirationWarningSent(false);

        ServiceExtensionRequest savedRequest = serviceExtensionRequestRepository.save(serviceExtensionRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_SERVICE_EXTENSION_REQUEST",
            "ServiceExtensionRequest",
            savedRequest.getId(),
            null,
            "Created service extension request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        // notificationService.notifyServiceExtensionRequestSubmitted(savedRequest);

        log.info("Successfully created service extension request with ID: {}", savedRequest.getId());
        return serviceExtensionRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ServiceExtensionRequestResponseDto approveServiceExtensionRequest(String requestId, String approverId, String comments) {
        log.info("Approving service extension request: {}", requestId);

        ServiceExtensionRequest request = getServiceExtensionRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending service extension requests can be approved");
        }

        // Validate request has required documents
        if (!request.hasRequiredDocuments()) {
            throw new ValidationException("Request is missing required documents. Please ensure request letter and employee consent letter are uploaded.");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);


        ServiceExtensionRequest savedRequest = serviceExtensionRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_SERVICE_EXTENSION_REQUEST",
            "ServiceExtensionRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyServiceExtensionRequestApproved(savedRequest);

        log.info("Successfully approved service extension request: {}", requestId);
        return serviceExtensionRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ServiceExtensionRequestResponseDto rejectServiceExtensionRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting service extension request: {}", requestId);

        ServiceExtensionRequest request = getServiceExtensionRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending service extension requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        ServiceExtensionRequest savedRequest = serviceExtensionRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_SERVICE_EXTENSION_REQUEST",
            "ServiceExtensionRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyServiceExtensionRequestRejected(savedRequest);

        log.info("Successfully rejected service extension request: {}", requestId);
        return serviceExtensionRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public ServiceExtensionRequestResponseDto updateServiceExtensionRequest(String requestId, ServiceExtensionRequestUpdateDto updateDto) {
        log.info("Updating service extension request: {}", requestId);

        ServiceExtensionRequest request = getServiceExtensionRequestEntityById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending service extension requests can be updated");
        }

        // Update fields if provided
        if (updateDto.getExtensionDurationYears() != null) {
            request.setExtensionDurationYears(updateDto.getExtensionDurationYears());
        }

        ServiceExtensionRequest savedRequest = serviceExtensionRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            null, // Will be filled by security context
            null, // Will be filled by security context
            "UPDATE_SERVICE_EXTENSION_REQUEST",
            "ServiceExtensionRequest",
            savedRequest.getId(),
            "Updated",
            "Updated",
            true,
            null
        );

        log.info("Successfully updated service extension request: {}", requestId);
        return serviceExtensionRequestMapper.toResponseDto(savedRequest);
    }

    public ServiceExtensionRequestResponseDto getServiceExtensionRequestById(String requestId) {
        ServiceExtensionRequest request = getServiceExtensionRequestEntityById(requestId);
        return serviceExtensionRequestMapper.toResponseDto(request);
    }

    public Page<ServiceExtensionRequestResponseDto> getAllServiceExtensionRequests(Pageable pageable) {
        Page<ServiceExtensionRequest> requests = serviceExtensionRequestRepository.findAll(pageable);
        return requests.map(serviceExtensionRequestMapper::toResponseDto);
    }

    public Page<ServiceExtensionRequestResponseDto> getServiceExtensionRequestsByStatus(RequestStatus status, Pageable pageable) {
        Page<ServiceExtensionRequest> requests = serviceExtensionRequestRepository.findByStatus(status, pageable);
        return requests.map(serviceExtensionRequestMapper::toResponseDto);
    }

    public Page<ServiceExtensionRequestResponseDto> getServiceExtensionRequestsByEmployee(String employeeId, Pageable pageable) {
        Page<ServiceExtensionRequest> requests = serviceExtensionRequestRepository.findByEmployeeId(employeeId, pageable);
        return requests.map(serviceExtensionRequestMapper::toResponseDto);
    }

    public Page<ServiceExtensionRequestResponseDto> getServiceExtensionRequestsByInstitution(String institutionId, Pageable pageable) {
        Page<ServiceExtensionRequest> requests = serviceExtensionRequestRepository.findByInstitutionId(institutionId, pageable);
        return requests.map(serviceExtensionRequestMapper::toResponseDto);
    }

    public Page<ServiceExtensionRequestResponseDto> searchServiceExtensionRequests(String searchTerm, Pageable pageable) {
        Page<ServiceExtensionRequest> requests = serviceExtensionRequestRepository.searchServiceExtensionRequests(searchTerm, pageable);
        return requests.map(serviceExtensionRequestMapper::toResponseDto);
    }

    public List<ServiceExtensionRequest> findOverdueServiceExtensionRequests(int slaDays) {
        LocalDateTime slaDeadline = LocalDateTime.now().minusDays(slaDays);
        return serviceExtensionRequestRepository.findOverdueServiceExtensionRequests(slaDeadline, Pageable.unpaged()).getContent();
    }

    public List<ServiceExtensionRequest> findExpiringExtensions(int days) {
        return serviceExtensionRequestRepository.findExpiringExtensions(days);
    }

    public List<ServiceExtensionRequest> findActiveExtensions() {
        return serviceExtensionRequestRepository.findActiveExtensions(LocalDate.now());
    }

    @Transactional
    public void processExpirationNotifications() {
        List<ServiceExtensionRequest> requestsRequiringNotification = 
            serviceExtensionRequestRepository.findRequiringExpirationNotification();
        
        for (ServiceExtensionRequest request : requestsRequiringNotification) {
            // Send expiration warning notification
            // notificationService.notifyServiceExtensionExpiring(request);
            
            request.setExpirationWarningSent(true);
            request.setNotificationSentDate(LocalDateTime.now());
            serviceExtensionRequestRepository.save(request);
        }
    }

    @Transactional
    public void updateExpiredExtensions() {
        List<ServiceExtensionRequest> expiredRequests = 
            serviceExtensionRequestRepository.findExpiredExtensions(LocalDate.now());
        
        for (ServiceExtensionRequest request : expiredRequests) {
            serviceExtensionRequestRepository.save(request);
        }
    }

    // Helper methods
    private Employee validateEmployeeForServiceExtension(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if employee is eligible for service extension
        if (employee.getEmploymentStatus() != EmploymentStatus.CONFIRMED) {
            throw new ValidationException("Only confirmed employees are eligible for service extension");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.TERMINATED) {
            throw new ValidationException("Terminated employees cannot request service extension");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.DISMISSED) {
            throw new ValidationException("Dismissed employees cannot request service extension");
        }

        // Check if there's already a pending service extension request
        Boolean hasPendingServiceExtension = serviceExtensionRequestRepository.hasActivePendingServiceExtension(employeeId);
        if (hasPendingServiceExtension) {
            throw new ValidationException("Employee already has a pending service extension request");
        }

        return employee;
    }

    private void validateServiceExtensionRequest(ServiceExtensionRequestCreateDto createDto, Employee employee) {
        // Validate extension duration
        if (createDto.getExtensionDurationYears() == null || createDto.getExtensionDurationYears() <= 0) {
            throw new ValidationException("Extension duration in years is required and must be greater than 0");
        }
    }

    private String generateRequestNumber() {
        // Generate unique request number with format: SE-YYYY-NNNNNN
        String year = String.valueOf(LocalDate.now().getYear());
        long count = serviceExtensionRequestRepository.count() + 1;
        return String.format("SE-%s-%06d", year, count);
    }
    
    private ServiceExtensionRequest getServiceExtensionRequestEntityById(String requestId) {
        return serviceExtensionRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Service extension request not found with id: " + requestId));
    }
}