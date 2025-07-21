package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.RequestType;
import com.zanzibar.csms.entity.enums.UserRole;
import com.zanzibar.csms.exception.BusinessRuleViolationException;
import com.zanzibar.csms.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessRuleEngine {

    private final RequestRepository requestRepository;

    // Business rule configurations
    private static final Map<RequestType, Integer> REQUEST_LIMITS_PER_YEAR = Map.of(
        RequestType.LWOP, 2,
        RequestType.TRANSFER, 1,
        RequestType.PROMOTION, 1
    );

    private static final Map<RequestType, Integer> PROCESSING_DAYS_SLA = Map.of(
        RequestType.CONFIRMATION, 30,
        RequestType.PROMOTION, 45,
        RequestType.LWOP, 14,
        RequestType.RETIREMENT, 60,
        RequestType.TRANSFER, 21
    );

    private static final Map<RequestType, List<String>> REQUIRED_DOCUMENTS = Map.of(
        RequestType.PROMOTION, List.of("PERFORMANCE_APPRAISAL", "ACADEMIC_CERTIFICATES"),
        RequestType.CONFIRMATION, List.of("EMPLOYMENT_LETTER", "PROBATION_REPORT"),
        RequestType.RETIREMENT, List.of("EMPLOYMENT_HISTORY", "PENSION_STATEMENT")
    );

    private static final Map<RequestType, BigDecimal> APPROVAL_LIMITS = Map.of(
        RequestType.PROMOTION, BigDecimal.valueOf(50000), // Maximum salary increase
        RequestType.LWOP, BigDecimal.valueOf(0) // No financial limit
    );

    /**
     * Validates a request against all applicable business rules
     */
    public void validateRequest(Request request) {
        log.info("Validating business rules for request: {}", request.getRequestNumber());

        // Rule 1: Check annual request limits
        validateAnnualRequestLimits(request);

        // Rule 2: Check required documents
        validateRequiredDocuments(request);

        // Rule 3: Check employee eligibility
        validateEmployeeEligibility(request);

        // Rule 4: Check conflicting requests
        validateConflictingRequests(request);

        // Rule 5: Check approval authority
        validateApprovalAuthority(request);

        // Rule 6: Check time-based constraints
        validateTimeConstraints(request);

        log.info("Business rule validation passed for request: {}", request.getRequestNumber());
    }

    /**
     * Validates if a user can approve a request based on business rules
     */
    public void validateApprovalAuthority(Request request, User approver) {
        log.info("Validating approval authority for user: {} on request: {}", 
            approver.getUsername(), request.getRequestNumber());

        // Check role-based approval authority
        if (!hasApprovalAuthority(approver.getRole(), request.getRequestType())) {
            throw new BusinessRuleViolationException(
                "User does not have authority to approve " + request.getRequestType() + " requests"
            );
        }

        // Check financial approval limits
        BigDecimal requestAmount = getRequestAmount(request);
        if (requestAmount != null && !hasFinancialAuthority(approver, requestAmount)) {
            throw new BusinessRuleViolationException(
                "User does not have financial authority to approve requests of this amount"
            );
        }
    }

    /**
     * Calculates SLA due date for a request
     */
    public LocalDateTime calculateSLADueDate(Request request) {
        int processingDays = PROCESSING_DAYS_SLA.getOrDefault(request.getRequestType(), 30);
        return request.getSubmissionDate().plusDays(processingDays);
    }

    /**
     * Checks if a request is approaching SLA deadline
     */
    public boolean isApproachingSLADeadline(Request request) {
        LocalDateTime dueDate = calculateSLADueDate(request);
        LocalDateTime warningDate = dueDate.minusDays(5); // 5 days before deadline
        return LocalDateTime.now().isAfter(warningDate);
    }

    /**
     * Checks if a request has breached SLA
     */
    public boolean hasBreachedSLA(Request request) {
        LocalDateTime dueDate = calculateSLADueDate(request);
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Gets escalation path for overdue requests
     */
    public List<UserRole> getEscalationPath(RequestType requestType) {
        return switch (requestType) {
            case CONFIRMATION, PROMOTION -> List.of(UserRole.HRMO, UserRole.HHRMD);
            case LWOP, TRANSFER -> List.of(UserRole.HRMO);
            case TERMINATION, DISMISSAL -> List.of(UserRole.DO, UserRole.HHRMD);
            default -> List.of(UserRole.HHRMD);
        };
    }

    private void validateAnnualRequestLimits(Request request) {
        Integer annualLimit = REQUEST_LIMITS_PER_YEAR.get(request.getRequestType());
        if (annualLimit != null) {
            LocalDate currentYearStart = LocalDate.now().withDayOfYear(1);
            LocalDate currentYearEnd = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
            
            long requestsThisYear = requestRepository.countByEmployeeAndRequestTypeAndSubmissionDateBetween(
                request.getEmployee(),
                request.getRequestType(),
                currentYearStart.atStartOfDay(),
                currentYearEnd.atTime(23, 59, 59)
            );

            if (requestsThisYear >= annualLimit) {
                throw new BusinessRuleViolationException(
                    "Employee has exceeded annual limit of " + annualLimit + " requests for " + request.getRequestType()
                );
            }
        }
    }

    private void validateRequiredDocuments(Request request) {
        List<String> requiredDocs = REQUIRED_DOCUMENTS.get(request.getRequestType());
        if (requiredDocs != null) {
            List<String> providedDocs = request.getDocuments().stream()
                .map(doc -> doc.getDocumentType())
                .toList();

            List<String> missingDocs = requiredDocs.stream()
                .filter(doc -> !providedDocs.contains(doc))
                .toList();

            if (!missingDocs.isEmpty()) {
                throw new BusinessRuleViolationException(
                    "Missing required documents: " + String.join(", ", missingDocs)
                );
            }
        }
    }

    private void validateEmployeeEligibility(Request request) {
        Employee employee = request.getEmployee();
        
        // Check if employee is active
        if (employee.getEmploymentStatus() != com.zanzibar.csms.entity.enums.EmploymentStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Employee is not active");
        }

        // Check probation period for confirmation requests
        if (request.getRequestType() == RequestType.CONFIRMATION) {
            if (employee.getEmploymentDate().isAfter(LocalDate.now().minusMonths(11))) {
                throw new BusinessRuleViolationException(
                    "Employee must complete at least 11 months of probation before confirmation"
                );
            }
        }

        // Check minimum service for promotion
        if (request.getRequestType() == RequestType.PROMOTION) {
            if (employee.getEmploymentDate().isAfter(LocalDate.now().minusYears(2))) {
                throw new BusinessRuleViolationException(
                    "Employee must have minimum 2 years of service for promotion"
                );
            }
        }

        // Check retirement age
        if (request.getRequestType() == RequestType.RETIREMENT) {
            int age = LocalDate.now().getYear() - employee.getDateOfBirth().getYear();
            if (age < 55) {
                throw new BusinessRuleViolationException(
                    "Employee must be at least 55 years old for retirement"
                );
            }
        }
    }

    private void validateConflictingRequests(Request request) {
        // Check for pending requests of the same type
        List<Request> pendingRequests = requestRepository.findPendingRequestsByEmployee(request.getEmployee());
        
        boolean hasConflictingRequest = pendingRequests.stream()
            .anyMatch(r -> r.getRequestType() == request.getRequestType());

        if (hasConflictingRequest) {
            throw new BusinessRuleViolationException(
                "Employee has a pending " + request.getRequestType() + " request"
            );
        }

        // Check for mutually exclusive requests
        if (request.getRequestType() == RequestType.PROMOTION) {
            boolean hasTransferRequest = pendingRequests.stream()
                .anyMatch(r -> r.getRequestType() == RequestType.TRANSFER);
            
            if (hasTransferRequest) {
                throw new BusinessRuleViolationException(
                    "Employee cannot have both promotion and transfer requests pending"
                );
            }
        }
    }

    private void validateApprovalAuthority(Request request) {
        // This is checked during actual approval, but we can do basic validation here
        BigDecimal requestAmount = getRequestAmount(request);
        if (requestAmount != null) {
            BigDecimal limit = APPROVAL_LIMITS.get(request.getRequestType());
            if (limit != null && requestAmount.compareTo(limit) > 0) {
                throw new BusinessRuleViolationException(
                    "Request amount exceeds approval limit of " + limit
                );
            }
        }
    }

    private void validateTimeConstraints(Request request) {
        // Check blackout periods (e.g., no promotions during budget freeze)
        if (request.getRequestType() == RequestType.PROMOTION) {
            LocalDate budgetFreezeStart = LocalDate.of(LocalDate.now().getYear(), 12, 1);
            LocalDate budgetFreezeEnd = LocalDate.of(LocalDate.now().getYear() + 1, 1, 31);
            
            if (LocalDate.now().isAfter(budgetFreezeStart) && LocalDate.now().isBefore(budgetFreezeEnd)) {
                throw new BusinessRuleViolationException(
                    "Promotion requests are not allowed during budget freeze period"
                );
            }
        }

        // Check working hours for urgent requests
        if (request.getPriority() != null && request.getPriority().name().equals("URGENT")) {
            int hour = LocalDateTime.now().getHour();
            if (hour < 8 || hour > 17) {
                throw new BusinessRuleViolationException(
                    "Urgent requests can only be submitted during working hours (8 AM - 5 PM)"
                );
            }
        }
    }

    private boolean hasApprovalAuthority(UserRole role, RequestType requestType) {
        return switch (requestType) {
            case CONFIRMATION, PROMOTION, LWOP, TRANSFER, RETIREMENT -> 
                role == UserRole.HRMO || role == UserRole.HHRMD;
            case TERMINATION, DISMISSAL -> 
                role == UserRole.DO || role == UserRole.HHRMD;
            default -> role == UserRole.HHRMD;
        };
    }

    private boolean hasFinancialAuthority(User user, BigDecimal amount) {
        // Define financial approval limits based on user role
        BigDecimal userLimit = switch (user.getRole()) {
            case HRMO -> BigDecimal.valueOf(25000);
            case HHRMD -> BigDecimal.valueOf(100000);
            case DO -> BigDecimal.valueOf(50000);
            default -> BigDecimal.ZERO;
        };

        return amount.compareTo(userLimit) <= 0;
    }

    private BigDecimal getRequestAmount(Request request) {
        // This method should be overridden for specific request types
        // For now, return null for requests without financial impact
        return null;
    }
}