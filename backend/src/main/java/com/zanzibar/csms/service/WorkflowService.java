package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.RequestWorkflow;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import com.zanzibar.csms.entity.enums.UserRole;
import com.zanzibar.csms.entity.enums.EmploymentStatus;
import com.zanzibar.csms.repository.RequestWorkflowRepository;
import com.zanzibar.csms.repository.UserRepository;
import com.zanzibar.csms.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final RequestWorkflowRepository workflowRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    private static final Map<RequestType, List<WorkflowStep>> WORKFLOW_DEFINITIONS = Map.of(
        RequestType.CONFIRMATION, List.of(
            new WorkflowStep(1, "HRO Review", UserRole.HRO),
            new WorkflowStep(2, "HRMO Approval", UserRole.HRMO),
            new WorkflowStep(3, "HHRMD Final Approval", UserRole.HHRMD)
        ),
        RequestType.PROMOTION, List.of(
            new WorkflowStep(1, "HRO Review", UserRole.HRO),
            new WorkflowStep(2, "HRMO Evaluation", UserRole.HRMO),
            new WorkflowStep(3, "HHRMD Final Approval", UserRole.HHRMD)
        ),
        RequestType.LWOP, List.of(
            new WorkflowStep(1, "HRO Review", UserRole.HRO),
            new WorkflowStep(2, "HRMO Approval", UserRole.HRMO)
        ),
        RequestType.RETIREMENT, List.of(
            new WorkflowStep(1, "HRO Verification", UserRole.HRO),
            new WorkflowStep(2, "HRMO Processing", UserRole.HRMO),
            new WorkflowStep(3, "HHRMD Final Approval", UserRole.HHRMD)
        ),
        RequestType.TRANSFER, List.of(
            new WorkflowStep(1, "HRO Review", UserRole.HRO),
            new WorkflowStep(2, "HRMO Coordination", UserRole.HRMO),
            new WorkflowStep(3, "HHRMD Approval", UserRole.HHRMD)
        )
    );

    @Transactional
    public void initializeWorkflow(Request request) {
        log.info("Initializing workflow for request: {}", request.getRequestNumber());
        
        List<WorkflowStep> workflowSteps = WORKFLOW_DEFINITIONS.getOrDefault(
            request.getRequestType(), 
            getDefaultWorkflow()
        );

        List<RequestWorkflow> requestWorkflows = new ArrayList<>();
        
        for (WorkflowStep step : workflowSteps) {
            RequestWorkflow workflow = RequestWorkflow.builder()
                .request(request)
                .stepNumber(step.stepNumber())
                .stepName(step.stepName())
                .requiredRole(step.requiredRole())
                .status(RequestStatus.SUBMITTED)
                .isCurrentStep(step.stepNumber() == 1)
                .build();
            
            if (step.stepNumber() == 1) {
                workflow.activate();
                request.setCurrentStage(step.stepName());
                request.setStatus(RequestStatus.HRO_REVIEW);
                
                // Assign to available reviewer with the required role
                assignToNextAvailableReviewer(workflow, step.requiredRole());
            }
            
            requestWorkflows.add(workflow);
        }
        
        workflowRepository.saveAll(requestWorkflows);
        
        auditService.logAction(
            request.getSubmittedBy().getId(),
            request.getSubmittedBy().getUsername(),
            "WORKFLOW_INITIALIZED",
            "Request",
            request.getId(),
            null,
            request.getRequestType().name() + " workflow initialized",
            true,
            null
        );
    }

    @Transactional
    public boolean processWorkflowStep(String requestId, String reviewerId, RequestStatus decision, String comments) {
        log.info("Processing workflow step for request: {} by reviewer: {}", requestId, reviewerId);
        
        Optional<RequestWorkflow> currentStepOpt = workflowRepository.findByRequestIdAndIsCurrentStepTrue(requestId);
        
        if (currentStepOpt.isEmpty()) {
            log.error("No current workflow step found for request: {}", requestId);
            return false;
        }
        
        RequestWorkflow currentStep = currentStepOpt.get();
        User reviewer = userRepository.findById(reviewerId)
            .orElseThrow(() -> new RuntimeException("Reviewer not found: " + reviewerId));
        
        // Validate reviewer has required role
        if (!reviewer.getRole().equals(currentStep.getRequiredRole())) {
            log.error("Reviewer {} does not have required role {} for workflow step", 
                reviewerId, currentStep.getRequiredRole());
            return false;
        }
        
        // Complete current step
        currentStep.complete(reviewer, comments, decision);
        workflowRepository.save(currentStep);
        
        Request request = currentStep.getRequest();
        
        if (decision == RequestStatus.REJECTED) {
            // Reject the entire request
            request.setStatus(RequestStatus.REJECTED);
            request.setApprover(reviewer);
            request.setApprovalDate(LocalDateTime.now());
            request.setRejectionReason(comments);
            
            auditService.logAction(
                reviewerId,
                reviewer.getUsername(),
                "REQUEST_REJECTED",
                "Request",
                requestId,
                null,
                "Request rejected at step: " + currentStep.getStepName(),
                true,
                null
            );
            
            return true;
        }
        
        // Move to next step or complete workflow
        return moveToNextStep(request, currentStep);
    }

    @Transactional
    protected boolean moveToNextStep(Request request, RequestWorkflow completedStep) {
        List<RequestWorkflow> allSteps = workflowRepository.findByRequestIdOrderByStepNumber(request.getId());
        
        Optional<RequestWorkflow> nextStepOpt = allSteps.stream()
            .filter(step -> step.getStepNumber() > completedStep.getStepNumber())
            .findFirst();
        
        if (nextStepOpt.isEmpty()) {
            // Workflow completed - approve request
            request.setStatus(RequestStatus.APPROVED);
            request.setApprover(completedStep.getReviewer());
            request.setApprovalDate(LocalDateTime.now());
            request.setCurrentStage("Completed");
            
            // Handle post-approval actions based on request type
            handlePostApprovalActions(request);
            
            auditService.logAction(
                completedStep.getReviewer().getId(),
                completedStep.getReviewer().getUsername(),
                "REQUEST_APPROVED",
                "Request",
                request.getId(),
                null,
                "Request fully approved",
                true,
                null
            );
            
            log.info("Workflow completed for request: {}", request.getRequestNumber());
            return true;
        }
        
        // Activate next step
        RequestWorkflow nextStep = nextStepOpt.get();
        nextStep.activate();
        
        request.setCurrentStage(nextStep.getStepName());
        request.setStatus(getStatusForRole(nextStep.getRequiredRole()));
        
        // Assign to next reviewer
        assignToNextAvailableReviewer(nextStep, nextStep.getRequiredRole());
        
        workflowRepository.save(nextStep);
        
        auditService.logAction(
            completedStep.getReviewer().getId(),
            completedStep.getReviewer().getUsername(),
            "WORKFLOW_STEP_COMPLETED",
            "Request",
            request.getId(),
            null,
            "Moved to step: " + nextStep.getStepName(),
            true,
            null
        );
        
        log.info("Moved request {} to next step: {}", request.getRequestNumber(), nextStep.getStepName());
        return true;
    }

    private void assignToNextAvailableReviewer(RequestWorkflow workflow, UserRole requiredRole) {
        List<User> availableReviewers = userRepository.findByRoleAndActiveTrue(requiredRole);
        
        if (!availableReviewers.isEmpty()) {
            // Simple round-robin assignment - can be enhanced with load balancing
            User assignedReviewer = availableReviewers.get(0);
            workflow.getRequest().setCurrentReviewer(assignedReviewer);
            
            log.info("Assigned workflow step to reviewer: {} for request: {}", 
                assignedReviewer.getUsername(), workflow.getRequest().getRequestNumber());
        }
    }

    private RequestStatus getStatusForRole(UserRole role) {
        return switch (role) {
            case HRO -> RequestStatus.HRO_REVIEW;
            case HRMO -> RequestStatus.HRMO_REVIEW;
            case HHRMD -> RequestStatus.HHRMD_REVIEW;
            default -> RequestStatus.SUBMITTED;
        };
    }

    private List<WorkflowStep> getDefaultWorkflow() {
        return List.of(
            new WorkflowStep(1, "HRO Review", UserRole.HRO),
            new WorkflowStep(2, "HRMO Approval", UserRole.HRMO),
            new WorkflowStep(3, "HHRMD Final Approval", UserRole.HHRMD)
        );
    }

    public List<RequestWorkflow> getWorkflowHistory(String requestId) {
        return workflowRepository.findByRequestIdOrderByStepNumber(requestId);
    }

    public Optional<RequestWorkflow> getCurrentWorkflowStep(String requestId) {
        return workflowRepository.findByRequestIdAndIsCurrentStepTrue(requestId);
    }

    public List<RequestWorkflow> getPendingWorkflowsForUser(String userId, RequestStatus status) {
        return workflowRepository.findByReviewerIdAndStatus(userId, status);
    }

    /**
     * Handle post-approval actions based on request type
     */
    @Transactional
    protected void handlePostApprovalActions(Request request) {
        switch (request.getRequestType()) {
            case CONFIRMATION:
                handleConfirmationApproval(request);
                break;
            case PROMOTION:
                // Future: Handle promotion approval
                log.info("Promotion request {} approved - manual processing required", request.getRequestNumber());
                break;
            case RETIREMENT:
                // Future: Handle retirement approval
                log.info("Retirement request {} approved - manual processing required", request.getRequestNumber());
                break;
            default:
                log.info("Request {} approved - no automatic post-processing", request.getRequestNumber());
        }
    }

    /**
     * Handle employee confirmation approval - update status from "On Probation" to "Confirmed"
     */
    @Transactional
    protected void handleConfirmationApproval(Request request) {
        try {
            Employee employee = request.getEmployee();
            if (employee != null) {
                EmploymentStatus previousStatus = employee.getEmploymentStatus();
                
                // Update employee status from ON_PROBATION to CONFIRMED
                employee.setEmploymentStatus(EmploymentStatus.CONFIRMED);
                employee.setConfirmationDate(LocalDate.now());
                
                employeeRepository.save(employee);
                
                log.info("Employee {} status updated from '{}' to 'CONFIRMED' due to confirmation request approval", 
                    employee.getFullName(), previousStatus.getDescription());
                
                // Log the status change
                auditService.logAction(
                    request.getApprover().getId(),
                    request.getApprover().getUsername(),
                    "EMPLOYEE_STATUS_UPDATED",
                    "Employee",
                    employee.getId(),
                    previousStatus.getDescription(),
                    EmploymentStatus.CONFIRMED.getDescription(),
                    true,
                    "Status updated automatically due to confirmation request approval"
                );
            } else {
                log.warn("No employee found for confirmation request {}", request.getRequestNumber());
            }
        } catch (Exception e) {
            log.error("Error updating employee status for confirmation request {}: {}", 
                request.getRequestNumber(), e.getMessage(), e);
            // Don't throw exception to avoid rolling back the request approval
        }
    }

    private record WorkflowStep(Integer stepNumber, String stepName, UserRole requiredRole) {}
}