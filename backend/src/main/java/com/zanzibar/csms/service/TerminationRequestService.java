package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.termination.TerminationRequestCreateDto;
import com.zanzibar.csms.dto.termination.TerminationRequestResponseDto;
import com.zanzibar.csms.dto.termination.TerminationRequestUpdateDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.TerminationRequest;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.TerminationScenario;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.mapper.TerminationRequestMapper;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.TerminationRequestRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TerminationRequestService {

    private final TerminationRequestRepository terminationRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final TerminationRequestMapper terminationRequestMapper;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Transactional
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public TerminationRequestResponseDto createTerminationRequest(TerminationRequestCreateDto createDto, String submittedById) {
        log.info("Creating termination request for employee: {}", createDto.getEmployeeId());

        // Validate employee exists and is eligible for termination
        Employee employee = validateEmployeeForTermination(createDto.getEmployeeId());
        
        // Get submitting user
        User submittedBy = userRepository.findById(submittedById)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + submittedById));

        // Validate termination request
        validateTerminationRequest(createDto, employee);

        // Create termination request
        TerminationRequest terminationRequest = new TerminationRequest();
        terminationRequest.setRequestNumber(generateRequestNumber());
        terminationRequest.setEmployee(employee);
        terminationRequest.setSubmittedBy(submittedBy);
        terminationRequest.setSubmissionDate(LocalDateTime.now());
        terminationRequest.setStatus(RequestStatus.SUBMITTED);
        terminationRequest.setScenario(createDto.getScenario());
        terminationRequest.setReason(createDto.getReason());
        terminationRequest.setIncidentDate(createDto.getIncidentDate());
        terminationRequest.setProbationEndDate(createDto.getProbationEndDate());
        terminationRequest.setInvestigationSummary(createDto.getInvestigationSummary());
        terminationRequest.setPriorWarningsCount(createDto.getPriorWarningsCount());
        terminationRequest.setDisciplinaryActions(createDto.getDisciplinaryActions());
        terminationRequest.setHrRecommendations(createDto.getHrRecommendations());

        TerminationRequest savedRequest = terminationRequestRepository.save(terminationRequest);

        // Create audit log
        auditService.logAction(
            submittedById,
            submittedBy.getUsername(),
            "CREATE_TERMINATION_REQUEST",
            "TerminationRequest",
            savedRequest.getId(),
            null,
            "Created termination request for employee: " + employee.getPayrollNumber(),
            true,
            null
        );

        // Send notifications to approvers
        notificationService.notifyTerminationRequestSubmitted(savedRequest);

        log.info("Successfully created termination request with ID: {}", savedRequest.getId());
        return terminationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('DO')")
    public TerminationRequestResponseDto approveTerminationRequest(String requestId, String approverId, String comments) {
        log.info("Approving termination request: {}", requestId);

        TerminationRequest request = getTerminationRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending termination requests can be approved");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.APPROVED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(comments);

        // Update employee status to terminated
        Employee employee = request.getEmployee();
        EmploymentStatus oldEmploymentStatus = employee.getEmploymentStatus();
        employee.setEmploymentStatus(EmploymentStatus.TERMINATED);
        employeeRepository.save(employee);

        TerminationRequest savedRequest = terminationRequestRepository.save(request);

        // Create audit logs
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "APPROVE_TERMINATION_REQUEST",
            "TerminationRequest",
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
        notificationService.notifyTerminationRequestApproved(savedRequest);

        log.info("Successfully approved termination request: {}", requestId);
        return terminationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HHRMD') or hasRole('DO')")
    public TerminationRequestResponseDto rejectTerminationRequest(String requestId, String approverId, String rejectionReason) {
        log.info("Rejecting termination request: {}", requestId);

        TerminationRequest request = getTerminationRequestEntityById(requestId);
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new ResourceNotFoundException("Approver not found with id: " + approverId));

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending termination requests can be rejected");
        }

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.REJECTED);
        request.setApprover(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setComments(rejectionReason);

        TerminationRequest savedRequest = terminationRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            approverId,
            approver.getUsername(),
            "REJECT_TERMINATION_REQUEST",
            "TerminationRequest",
            savedRequest.getId(),
            oldStatus.name(),
            savedRequest.getStatus().name(),
            true,
            null
        );

        // Send notifications
        notificationService.notifyTerminationRequestRejected(savedRequest);

        log.info("Successfully rejected termination request: {}", requestId);
        return terminationRequestMapper.toResponseDto(savedRequest);
    }

    @Transactional
    @PreAuthorize("hasRole('HRO')")
    public TerminationRequestResponseDto updateTerminationRequest(String requestId, TerminationRequestUpdateDto updateDto) {
        log.info("Updating termination request: {}", requestId);

        TerminationRequest request = getTerminationRequestEntityById(requestId);

        if (request.getStatus().isTerminal()) {
            throw new ValidationException("Only pending termination requests can be updated");
        }

        TerminationRequestResponseDto oldData = terminationRequestMapper.toResponseDto(request);

        // Update fields if provided
        if (updateDto.getScenario() != null) {
            request.setScenario(updateDto.getScenario());
        }
        if (updateDto.getReason() != null) {
            request.setReason(updateDto.getReason());
        }
        if (updateDto.getIncidentDate() != null) {
            request.setIncidentDate(updateDto.getIncidentDate());
        }
        if (updateDto.getProbationEndDate() != null) {
            request.setProbationEndDate(updateDto.getProbationEndDate());
        }
        if (updateDto.getInvestigationSummary() != null) {
            request.setInvestigationSummary(updateDto.getInvestigationSummary());
        }
        if (updateDto.getPriorWarningsCount() != null) {
            request.setPriorWarningsCount(updateDto.getPriorWarningsCount());
        }
        if (updateDto.getDisciplinaryActions() != null) {
            request.setDisciplinaryActions(updateDto.getDisciplinaryActions());
        }
        if (updateDto.getHrRecommendations() != null) {
            request.setHrRecommendations(updateDto.getHrRecommendations());
        }

        TerminationRequest savedRequest = terminationRequestRepository.save(request);

        // Create audit log
        auditService.logAction(
            null, // Will be filled by security context
            null, // Will be filled by security context
            "UPDATE_TERMINATION_REQUEST",
            "TerminationRequest",
            savedRequest.getId(),
            "Updated",
            "Updated",
            true,
            null
        );

        log.info("Successfully updated termination request: {}", requestId);
        return terminationRequestMapper.toResponseDto(savedRequest);
    }

    public TerminationRequestResponseDto getTerminationRequestById(String requestId) {
        TerminationRequest request = terminationRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Termination request not found with id: " + requestId));
        return terminationRequestMapper.toResponseDto(request);
    }

    public Page<TerminationRequestResponseDto> getAllTerminationRequests(Pageable pageable) {
        Page<TerminationRequest> requests = terminationRequestRepository.findAll(pageable);
        return requests.map(terminationRequestMapper::toResponseDto);
    }

    public Page<TerminationRequestResponseDto> getTerminationRequestsByStatus(RequestStatus status, Pageable pageable) {
        Page<TerminationRequest> requests = terminationRequestRepository.findByStatus(status, pageable);
        return requests.map(terminationRequestMapper::toResponseDto);
    }

    public Page<TerminationRequestResponseDto> getTerminationRequestsByEmployee(String employeeId, Pageable pageable) {
        Page<TerminationRequest> requests = terminationRequestRepository.findByEmployeeId(employeeId, pageable);
        return requests.map(terminationRequestMapper::toResponseDto);
    }

    public Page<TerminationRequestResponseDto> getTerminationRequestsByInstitution(String institutionId, Pageable pageable) {
        Page<TerminationRequest> requests = terminationRequestRepository.findByInstitutionId(institutionId, pageable);
        return requests.map(terminationRequestMapper::toResponseDto);
    }

    public Page<TerminationRequestResponseDto> searchTerminationRequests(String searchTerm, Pageable pageable) {
        Page<TerminationRequest> requests = terminationRequestRepository.searchTerminationRequests(searchTerm, pageable);
        return requests.map(terminationRequestMapper::toResponseDto);
    }

    public List<TerminationRequest> findOverdueTerminationRequests(int slaDays) {
        LocalDateTime slaDeadline = LocalDateTime.now().minusDays(slaDays);
        return terminationRequestRepository.findOverdueTerminationRequests(slaDeadline);
    }

    // Helper methods
    private Employee validateEmployeeForTermination(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // Check if employee is eligible for termination (unconfirmed employees only)
        if (employee.getEmploymentStatus() == EmploymentStatus.CONFIRMED) {
            throw new ValidationException("Confirmed employees cannot be terminated. Use dismissal process instead.");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.TERMINATED) {
            throw new ValidationException("Employee is already terminated");
        }

        if (employee.getEmploymentStatus() == EmploymentStatus.DISMISSED) {
            throw new ValidationException("Employee is already dismissed");
        }

        // Check if there's already a pending termination request
        Boolean hasPendingTermination = terminationRequestRepository.hasActivePendingTermination(employeeId);
        if (hasPendingTermination) {
            throw new ValidationException("Employee already has a pending termination request");
        }

        return employee;
    }

    private void validateTerminationRequest(TerminationRequestCreateDto createDto, Employee employee) {
        // Validate probation period for unconfirmed employees
        if (createDto.getScenario() == TerminationScenario.UNCONFIRMED_OUT_OF_PROBATION) {
            if (employee.getEmploymentDate() != null) {
                Period probationPeriod = Period.between(employee.getEmploymentDate(), LocalDateTime.now().toLocalDate());
                if (probationPeriod.toTotalMonths() < 12) {
                    throw new ValidationException("Employee must complete at least 12 months of probation before termination consideration");
                }
            }
            
            if (createDto.getProbationEndDate() == null) {
                throw new ValidationException("Probation end date is required for unconfirmed out of probation scenario");
            }
        }

        // Validate incident date for disciplinary scenarios
        if (createDto.getScenario() == TerminationScenario.DISCIPLINARY && createDto.getIncidentDate() == null) {
            throw new ValidationException("Incident date is required for disciplinary termination");
        }

        // Validate that incident date is not in the future
        if (createDto.getIncidentDate() != null && createDto.getIncidentDate().isAfter(LocalDateTime.now().toLocalDate())) {
            throw new ValidationException("Incident date cannot be in the future");
        }
    }

    private String generateRequestNumber() {
        // Generate unique request number with format: TER-YYYY-NNNNNN
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = terminationRequestRepository.count() + 1;
        return String.format("TER-%s-%06d", year, count);
    }

    
    private TerminationRequest getTerminationRequestEntityById(String requestId) {
        return terminationRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Termination request not found with id: " + requestId));
    }

}
