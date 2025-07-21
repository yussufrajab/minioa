package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.retirement.RetirementRequestCreateDto;
import com.zanzibar.csms.dto.retirement.RetirementRequestResponseDto;
import com.zanzibar.csms.dto.retirement.RetirementRequestUpdateDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.RetirementRequest;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RetirementType;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.RetirementRequestMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.RetirementRequestRepository;
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
public class RetirementRequestService {

    private final RetirementRequestRepository retirementRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RetirementRequestMapper retirementRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public RetirementRequestResponseDto createRetirementRequest(RetirementRequestCreateDto createDto, String submittedById) {
        log.info("Creating retirement request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for retirement
        Employee employee = validateEmployeeForRetirement(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate retirement request
        validateRetirementRequest(createDto, employee);

        // Create retirement request
        RetirementRequest retirementRequest = new RetirementRequest();
        retirementRequest.setRequestNumber(generateRequestNumber());
        retirementRequest.setEmployee(employee);
        retirementRequest.setSubmittedBy(submittedBy);
        retirementRequest.setSubmissionDate(LocalDateTime.now());
        retirementRequest.setStatus(RequestStatus.SUBMITTED);
        retirementRequest.setRetirementType(createDto.getRetirementType());
        retirementRequest.setRetirementDate(createDto.getRetirementDate());
        retirementRequest.setLastWorkingDate(createDto.getLastWorkingDate());
        retirementRequest.setPensionEligibilityConfirmed(createDto.getPensionEligibilityConfirmed());
        retirementRequest.setClearanceCompleted(false);

        RetirementRequest savedRequest = retirementRequestRepository.save(retirementRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_RETIREMENT_REQUEST",
            "RetirementRequest",
            savedRequest.getId(),
            null,
            "Created retirement request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        // notificationService.notifyRetirementRequestSubmitted(savedRequest);

        log.info("Successfully created retirement request with ID: {}", savedRequest.getId());
        return retirementRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public RetirementRequestResponseDto approveRetirementRequest(String requestId, String approverId, String comments) {
        log.info("Approving retirement request: {}", requestId);

        RetirementRequest request = getRetirementRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending retirement requests can be approved");
        }

        // Validate request has required documents
        if (!request.hasRequiredDocuments()) {
            throw new ValidationException("Request is missing required documents. Please ensure all mandatory documents are uploaded.");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);

        // Update employee status to retired
        Employee employee = request.getEmployee();
        employee.setEmploymentStatus(EmploymentStatus.RETIRED);
        employeeRepository.save(employee);

        RetirementRequest savedRequest = retirementRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_RETIREMENT_REQUEST",
            "RetirementRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyRetirementRequestApproved(savedRequest);

        log.info("Successfully approved retirement request: {}", requestId);
        return retirementRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public RetirementRequestResponseDto rejectRetirementRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting retirement request: {}", requestId);

        RetirementRequest request = getRetirementRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending retirement requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        RetirementRequest savedRequest = retirementRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_RETIREMENT_REQUEST",
            "RetirementRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        // notificationService.notifyRetirementRequestRejected(savedRequest);

        log.info("Successfully rejected retirement request: {}", requestId);
        return retirementRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public RetirementRequestResponseDto updateRetirementRequest(String requestId, RetirementRequestUpdateDto updateDto) {
        log.info("Updating retirement request: {}", requestId);

        RetirementRequest request = getRetirementRequestEntityById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending retirement requests can be updated");
        }

        // Update fields if provided
        if (updateDto.getRetirementType() != null) {
            request.setRetirementType(updateDto.getRetirementType());
        }
        if (updateDto.getRetirementDate() != null) {
            request.setRetirementDate(updateDto.getRetirementDate());
        }
        if (updateDto.getLastWorkingDate() != null) {
            request.setLastWorkingDate(updateDto.getLastWorkingDate());
        }
        if (updateDto.getPensionEligibilityConfirmed() != null) {
            request.setPensionEligibilityConfirmed(updateDto.getPensionEligibilityConfirmed());
        }
        if (updateDto.getClearanceCompleted() != null) {
            request.setClearanceCompleted(updateDto.getClearanceCompleted());
        }

        RetirementRequest savedRequest = retirementRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            null, // Will be filled by security context
            null, // Will be filled by security context
            "UPDATE_RETIREMENT_REQUEST",
            "RetirementRequest",
            savedRequest.getId(),
            "Updated",
            "Updated",
            true,
            null
        );

        log.info("Successfully updated retirement request: {}", requestId);
        return retirementRequestMapper.toResponseDto(savedRequest);
    }

    public RetirementRequestResponseDto getRetirementRequestById(String requestId) {
        RetirementRequest request = getRetirementRequestEntityById(requestId);
        return retirementRequestMapper.toResponseDto(request);
    }

    public Page<RetirementRequestResponseDto> getAllRetirementRequests(Pageable pageable) {
        Page<RetirementRequest> requests = retirementRequestRepository.findAll(pageable);
        return requests.map(retirementRequestMapper::toResponseDto);
    }

    public Page<RetirementRequestResponseDto> getRetirementRequestsByStatus(RequestStatus status, Pageable pageable) {
        Page<RetirementRequest> requests = retirementRequestRepository.findByStatus(status, pageable);
        return requests.map(retirementRequestMapper::toResponseDto);
    }

    public Page<RetirementRequestResponseDto> getRetirementRequestsByEmployee(String employeeId, Pageable pageable) {
        Page<RetirementRequest> requests = retirementRequestRepository.findByEmployeeId(employeeId, pageable);
        return requests.map(retirementRequestMapper::toResponseDto);
    }

    public Page<RetirementRequestResponseDto> getRetirementRequestsByInstitution(String institutionId, Pageable pageable) {
        Page<RetirementRequest> requests = retirementRequestRepository.findByInstitutionId(institutionId, pageable);
        return requests.map(retirementRequestMapper::toResponseDto);
    }

    public Page<RetirementRequestResponseDto> getRetirementRequestsByType(RetirementType retirementType, Pageable pageable) {
        Page<RetirementRequest> requests = retirementRequestRepository.findByRetirementType(retirementType, pageable);
        return requests.map(retirementRequestMapper::toResponseDto);
    }

    public Page<RetirementRequestResponseDto> searchRetirementRequests(String searchTerm, Pageable pageable) {
        Page<RetirementRequest> requests = retirementRequestRepository.searchRetirementRequests(searchTerm, pageable);
        return requests.map(retirementRequestMapper::toResponseDto);
    }

    public List<RetirementRequest> findOverdueRetirementRequests(int slaDays) {
        LocalDateTime slaDeadline = LocalDateTime.now().minusDays(slaDays);
        return retirementRequestRepository.findOverdueRetirementRequests(slaDeadline, Pageable.unpaged()).getContent();
    }

    public List<RetirementRequest> findUpcomingRetirements(int days) {
        LocalDate targetDate = LocalDate.now().plusDays(days);
        return retirementRequestRepository.findUpcomingRetirements(targetDate);
    }

    // Helper methods
    private Employee validateEmployeeForRetirement(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if employee is eligible for retirement
        if (employee.getEmploymentStatus() == EmploymentStatus.TERMINATED) {
            throw new ValidationException("Terminated employees cannot request retirement");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.DISMISSED) {
            throw new ValidationException("Dismissed employees cannot request retirement");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.RETIRED) {
            throw new ValidationException("Employee is already retired");
        }

        // Check if there's already a pending retirement request
        Boolean hasPendingRetirement = retirementRequestRepository.hasActivePendingRetirement(employeeId);
        if (hasPendingRetirement) {
            throw new ValidationException("Employee already has a pending retirement request");
        }

        return employee;
    }

    private void validateRetirementRequest(RetirementRequestCreateDto createDto, Employee employee) {
        // Validate retirement date is not in the past
        if (createDto.getRetirementDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Retirement date cannot be in the past");
        }

        // Validate last working date is before retirement date
        if (createDto.getLastWorkingDate() != null && 
            !createDto.getLastWorkingDate().isBefore(createDto.getRetirementDate())) {
            throw new ValidationException("Last working date must be before retirement date");
        }

        // Additional validation based on retirement type
        if (createDto.getRetirementType() == RetirementType.COMPULSORY) {
            // Validate employee age for compulsory retirement (typically 60 years)
            LocalDate birthDate = employee.getDateOfBirth();
            if (birthDate != null) {
                int age = LocalDate.now().getYear() - birthDate.getYear();
                if (age < 60) {
                    throw new ValidationException("Employee must be at least 60 years old for compulsory retirement");
                }
            }
        }

        if (createDto.getRetirementType() == RetirementType.VOLUNTARY) {
            // Validate employee has minimum service years for voluntary retirement
            LocalDate employmentDate = employee.getEmploymentDate();
            if (employmentDate != null) {
                int serviceYears = LocalDate.now().getYear() - employmentDate.getYear();
                if (serviceYears < 10) {
                    throw new ValidationException("Employee must have at least 10 years of service for voluntary retirement");
                }
            }
        }
    }

    private String generateRequestNumber() {
        // Generate unique request number with format: RET-YYYY-NNNNNN
        String year = String.valueOf(LocalDate.now().getYear());
        long count = retirementRequestRepository.count() + 1;
        return String.format("RET-%s-%06d", year, count);
    }
    
    private RetirementRequest getRetirementRequestEntityById(String requestId) {
        return retirementRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Retirement request not found with id: " + requestId));
    }
}