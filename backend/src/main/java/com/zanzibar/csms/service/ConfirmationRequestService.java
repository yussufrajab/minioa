package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.ConfirmationRequestDto;
import com.zanzibar.csms.entity.ConfirmationRequest;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.entity.enums.Priority;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ConfirmationEligibilityException;
import com.zanzibar.csms.exception.ConfirmationValidationException;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.RequestRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public Page<ConfirmationRequestDto> getAllConfirmationRequests(Pageable pageable) {
        Page<ConfirmationRequest> requests = requestRepository.findByRequestType(RequestType.CONFIRMATION, pageable)
                .map(request -> (ConfirmationRequest) request);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public ConfirmationRequestDto getConfirmationRequestById(String id) {
        ConfirmationRequest request = (ConfirmationRequest) requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation request not found with id: " + id));
        return convertToDto(request);
    }

    @Transactional
    public ConfirmationRequestDto createConfirmationRequest(ConfirmationRequestDto requestDto, String currentUserId) {
        User submitter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + requestDto.getEmployeeId()));

        // Validate employee is eligible for confirmation
        if (!isEmployeeEligibleForConfirmation(employee.getId())) {
            String reason = getConfirmationEligibilityReason(employee.getId());
            throw new ConfirmationEligibilityException(employee.getId(), reason);
        }

        // Validate business rules for confirmation request
        validateConfirmationRequest(requestDto);

        ConfirmationRequest request = new ConfirmationRequest();
        
        // Set base request fields
        request.setRequestNumber(generateRequestNumber());
        request.setEmployee(employee);
        request.setSubmittedBy(submitter);
        request.setRequestType(RequestType.CONFIRMATION);
        request.setDescription(buildDescription(employee));
        request.setPriority(requestDto.getPriority() != null ? requestDto.getPriority() : Priority.NORMAL);
        request.setStatus(RequestStatus.SUBMITTED);
        
        // Set confirmation-specific fields
        request.setProbationStartDate(requestDto.getProbationStartDate());
        request.setProbationEndDate(requestDto.getProbationEndDate());
        request.setPerformanceRating(requestDto.getPerformanceRating());
        request.setSupervisorRecommendation(requestDto.getSupervisorRecommendation());
        request.setHrAssessment(requestDto.getHrAssessment());
        request.setProposedConfirmationDate(requestDto.getProposedConfirmationDate());
        request.setCurrentSalary(requestDto.getCurrentSalary());
        request.setProposedSalary(requestDto.getProposedSalary());

        request.setCreatedBy(currentUserId);
        request.setStatus(RequestStatus.DRAFT);
        
        // Set due date (30 days from probation end date)
        if (requestDto.getProbationEndDate() != null) {
            request.setDueDate(requestDto.getProbationEndDate().plusDays(30).atStartOfDay());
        }

        ConfirmationRequest savedRequest = (ConfirmationRequest) requestRepository.save(request);

        auditService.logAction(
                currentUserId,
                submitter.getUsername(),
                "CREATE_CONFIRMATION_REQUEST",
                "ConfirmationRequest",
                savedRequest.getId(),
                null,
                "Confirmation request created for employee: " + employee.getFullName(),
                true,
                null
        );

        return convertToDto(savedRequest);
    }

    @Transactional
    public ConfirmationRequestDto updateConfirmationRequest(String id, ConfirmationRequestDto requestDto, String currentUserId) {
        ConfirmationRequest request = (ConfirmationRequest) requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Confirmation request not found: " + id));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new IllegalStateException("Can only update requests in draft status");
        }

        if (!request.getSubmittedBy().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Only the submitter can update this request");
        }

        // Update fields
        request.setProbationStartDate(requestDto.getProbationStartDate());
        request.setProbationEndDate(requestDto.getProbationEndDate());
        request.setPerformanceRating(requestDto.getPerformanceRating());
        request.setSupervisorRecommendation(requestDto.getSupervisorRecommendation());
        request.setHrAssessment(requestDto.getHrAssessment());
        request.setProposedConfirmationDate(requestDto.getProposedConfirmationDate());
        request.setCurrentSalary(requestDto.getCurrentSalary());
        request.setProposedSalary(requestDto.getProposedSalary());
        request.setDescription(requestDto.getDescription());
        request.setComments(requestDto.getComments());
        request.setPriority(requestDto.getPriority());
        request.setUpdatedBy(currentUserId);

        ConfirmationRequest savedRequest = (ConfirmationRequest) requestRepository.save(request);

        auditService.logAction(
                currentUserId,
                user.getUsername(),
                "UPDATE_CONFIRMATION_REQUEST",
                "ConfirmationRequest",
                id,
                null,
                "Confirmation request updated",
                true,
                null
        );

        return convertToDto(savedRequest);
    }

    @Transactional(readOnly = true)
    public boolean isEmployeeEligibleForConfirmation(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        // Check if employee status is UNCONFIRMED
        if (employee.getEmploymentStatus() != EmploymentStatus.UNCONFIRMED) {
            log.debug("Employee {} is not eligible for confirmation: status is {}", employeeId, employee.getEmploymentStatus());
            return false;
        }

        // Check if employee has been employed for at least 12 months
        if (employee.getEmploymentDate() == null) {
            log.debug("Employee {} is not eligible for confirmation: employment date is null", employeeId);
            return false;
        }

        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        if (employee.getEmploymentDate().isAfter(twelveMonthsAgo)) {
            log.debug("Employee {} is not eligible for confirmation: employed for less than 12 months", employeeId);
            return false;
        }

        // Check if there's already a pending confirmation request
        boolean hasPendingRequest = requestRepository.findByEmployeeId(employeeId, Pageable.unpaged())
                .getContent()
                .stream()
                .anyMatch(request -> request.getRequestType() == RequestType.CONFIRMATION && 
                                   request.getStatus().isPending());

        if (hasPendingRequest) {
            log.debug("Employee {} is not eligible for confirmation: already has pending request", employeeId);
            return false;
        }

        // Check if employee already has a confirmed status
        if (employee.getConfirmationDate() != null) {
            log.debug("Employee {} is not eligible for confirmation: already confirmed on {}", employeeId, employee.getConfirmationDate());
            return false;
        }

        return true;
    }

    @Transactional(readOnly = true)
    public String getConfirmationEligibilityReason(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));

        if (employee.getEmploymentStatus() != EmploymentStatus.UNCONFIRMED) {
            return "Employee status is " + employee.getEmploymentStatus() + ", not UNCONFIRMED";
        }

        if (employee.getEmploymentDate() == null) {
            return "Employment date is not set";
        }

        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        if (employee.getEmploymentDate().isAfter(twelveMonthsAgo)) {
            long monthsEmployed = java.time.temporal.ChronoUnit.MONTHS.between(employee.getEmploymentDate(), LocalDate.now());
            return "Employee has been employed for only " + monthsEmployed + " months, minimum 12 months required";
        }

        boolean hasPendingRequest = requestRepository.findByEmployeeId(employeeId, Pageable.unpaged())
                .getContent()
                .stream()
                .anyMatch(request -> request.getRequestType() == RequestType.CONFIRMATION && 
                                   request.getStatus().isPending());

        if (hasPendingRequest) {
            return "Employee already has a pending confirmation request";
        }

        if (employee.getConfirmationDate() != null) {
            return "Employee is already confirmed on " + employee.getConfirmationDate();
        }

        return "Employee is eligible for confirmation";
    }

    /**
     * Validates a confirmation request for business rule compliance
     */
    public void validateConfirmationRequest(ConfirmationRequestDto requestDto) {
        validateProbationPeriod(requestDto);
        validateSalaryInformation(requestDto);
        validatePerformanceRating(requestDto);
        validateDocumentationRequirements(requestDto);
    }

    private void validateProbationPeriod(ConfirmationRequestDto requestDto) {
        if (requestDto.getProbationStartDate() == null) {
            throw new ConfirmationValidationException("probationStartDate", "null", "Probation start date is required");
        }
        if (requestDto.getProbationEndDate() == null) {
            throw new ConfirmationValidationException("probationEndDate", "null", "Probation end date is required");
        }
        if (!requestDto.isProbationPeriodValid()) {
            throw new ConfirmationValidationException("probationEndDate", requestDto.getProbationEndDate().toString(), "Probation end date must be after probation start date");
        }
        if (!requestDto.isEligibleForConfirmation()) {
            throw new ConfirmationValidationException("probationEndDate", requestDto.getProbationEndDate().toString(), "Employee must complete at least 12 months of probation before confirmation");
        }
    }

    private void validateSalaryInformation(ConfirmationRequestDto requestDto) {
        if (requestDto.getCurrentSalary() != null && requestDto.getProposedSalary() != null) {
            if (requestDto.getProposedSalary() < requestDto.getCurrentSalary()) {
                throw new ConfirmationValidationException("proposedSalary", requestDto.getProposedSalary().toString(), "Proposed salary cannot be less than current salary");
            }
            double increasePercentage = requestDto.getSalaryIncreasePercentage();
            if (increasePercentage > 50) { // Max 50% increase
                throw new ConfirmationValidationException("proposedSalary", requestDto.getProposedSalary().toString(), "Salary increase cannot exceed 50% of current salary");
            }
        }
    }

    private void validatePerformanceRating(ConfirmationRequestDto requestDto) {
        if (requestDto.getPerformanceRating() != null) {
            String[] validRatings = {"EXCELLENT", "GOOD", "SATISFACTORY", "NEEDS_IMPROVEMENT", "UNSATISFACTORY"};
            boolean isValid = false;
            for (String rating : validRatings) {
                if (rating.equals(requestDto.getPerformanceRating())) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                throw new ConfirmationValidationException("performanceRating", requestDto.getPerformanceRating(), "Invalid performance rating");
            }
            
            // Cannot confirm employee with unsatisfactory performance
            if ("UNSATISFACTORY".equals(requestDto.getPerformanceRating())) {
                throw new ConfirmationValidationException("performanceRating", requestDto.getPerformanceRating(), "Cannot confirm employee with unsatisfactory performance rating");
            }
        }
    }

    private void validateDocumentationRequirements(ConfirmationRequestDto requestDto) {
        // As per requirements, confirmation requires: Letter from Accounting Officer, IPA Certificate, Performance Appraisal
        if (!requestDto.hasSupervisorRecommendation()) {
            throw new ConfirmationValidationException("supervisorRecommendation", "null", "Supervisor recommendation is required for confirmation");
        }
        if (!requestDto.hasHrAssessment()) {
            throw new ConfirmationValidationException("hrAssessment", "null", "HR assessment is required for confirmation");
        }
        if (!requestDto.hasPerformanceRating()) {
            throw new ConfirmationValidationException("performanceRating", "null", "Performance rating is required for confirmation");
        }
    }

    /**
     * Gets employees who are approaching confirmation eligibility
     */
    @Transactional(readOnly = true)
    public Page<Employee> getEmployeesNearingConfirmationEligibility(Pageable pageable) {
        // Find employees who will be eligible for confirmation within next 30 days
        LocalDate elevenMonthsAgo = LocalDate.now().minusMonths(11);
        LocalDate tenMonthsAgo = LocalDate.now().minusMonths(10);
        
        return employeeRepository.findByEmploymentStatusAndEmploymentDateBetween(
            EmploymentStatus.UNCONFIRMED, 
            elevenMonthsAgo, 
            tenMonthsAgo, 
            pageable
        );
    }

    /**
     * Gets employees who are overdue for confirmation
     */
    @Transactional(readOnly = true)
    public Page<Employee> getEmployeesOverdueForConfirmation(Pageable pageable) {
        LocalDate fourteenMonthsAgo = LocalDate.now().minusMonths(14);
        
        return employeeRepository.findByEmploymentStatusAndEmploymentDateBefore(
            EmploymentStatus.UNCONFIRMED, 
            fourteenMonthsAgo, 
            pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<ConfirmationRequestDto> getPendingConfirmations(Pageable pageable) {
        // This would typically query for employees approaching confirmation eligibility
        // For now, return confirmation requests that are pending
        Page<ConfirmationRequest> requests = requestRepository.findByRequestType(RequestType.CONFIRMATION, pageable)
                .map(request -> (ConfirmationRequest) request);
        
        return requests.map(this::convertToDto);
    }

    private String generateRequestNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "CONF-" + timestamp;
    }

    private String buildDescription(Employee employee) {
        return String.format("Employee confirmation request for %s (ID: %s). " +
                "Position: %s, Department: %s, Institution: %s",
                employee.getFullName(),
                employee.getId(),
                employee.getRank(),
                employee.getDepartment(),
                employee.getInstitution() != null ? employee.getInstitution().getName() : "Unknown");
    }

    private ConfirmationRequestDto convertToDto(ConfirmationRequest request) {
        ConfirmationRequestDto dto = new ConfirmationRequestDto();
        
        // Set confirmation-specific fields
        dto.setProbationStartDate(request.getProbationStartDate());
        dto.setProbationEndDate(request.getProbationEndDate());
        dto.setPerformanceRating(request.getPerformanceRating());
        dto.setSupervisorRecommendation(request.getSupervisorRecommendation());
        dto.setHrAssessment(request.getHrAssessment());
        dto.setProposedConfirmationDate(request.getProposedConfirmationDate());
        dto.setCurrentSalary(request.getCurrentSalary());
        dto.setProposedSalary(request.getProposedSalary());

        // Set base request fields
        dto.setId(request.getId());
        dto.setRequestNumber(request.getRequestNumber());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setEmployeeName(request.getEmployee().getFullName());
        dto.setSubmittedById(request.getSubmittedBy().getId());
        dto.setSubmittedByName(request.getSubmittedBy().getFullName());
        dto.setSubmissionDate(request.getSubmissionDate());
        dto.setStatus(request.getStatus());
        dto.setRequestType(request.getRequestType());
        dto.setComments(request.getComments());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setPriority(request.getPriority());
        dto.setDueDate(request.getDueDate());
        dto.setDescription(request.getDescription());
        dto.setCurrentStage(request.getCurrentStage());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        if (request.getEmployee().getInstitution() != null) {
            dto.setInstitutionId(request.getEmployee().getInstitution().getId());
            dto.setInstitutionName(request.getEmployee().getInstitution().getName());
        }

        if (request.getApprover() != null) {
            dto.setApproverId(request.getApprover().getId());
            dto.setApproverName(request.getApprover().getFullName());
            dto.setApprovalDate(request.getApprovalDate());
        }

        if (request.getCurrentReviewer() != null) {
            dto.setCurrentReviewerId(request.getCurrentReviewer().getId());
            dto.setCurrentReviewerName(request.getCurrentReviewer().getFullName());
        }

        return dto;
    }
}