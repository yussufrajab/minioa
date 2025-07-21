package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.RequestWorkflow;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final RequestRepository requestRepository;

    @Async
    public void sendWorkflowNotification(Request request, RequestWorkflow workflow, String action) {
        log.info("Sending workflow notification for request {} - action: {}", 
            request.getRequestNumber(), action);
        
        String message = buildWorkflowMessage(request, workflow, action);
        
        // In a real implementation, this would send email/SMS/push notifications
        // For now, we'll just log the notification
        log.info("NOTIFICATION: {}", message);
        
        // You could integrate with email services, SMS providers, or push notification services here
        // Examples:
        // - Spring Boot Mail
        // - Twilio for SMS
        // - Firebase for push notifications
        // - Slack/Teams webhooks
    }

    @Async
    public void sendStatusChangeNotification(Request request, RequestStatus oldStatus, RequestStatus newStatus, User user) {
        log.info("Sending status change notification for request {} from {} to {}", 
            request.getRequestNumber(), oldStatus, newStatus);
        
        String message = buildStatusChangeMessage(request, oldStatus, newStatus, user);
        log.info("NOTIFICATION: {}", message);
    }

    @Async
    public void sendOverdueNotification(Request request) {
        log.warn("Sending overdue notification for request {}", request.getRequestNumber());
        
        String message = buildOverdueMessage(request);
        log.warn("OVERDUE NOTIFICATION: {}", message);
    }

    @Async
    public void sendApprovalNotification(Request request, User approver) {
        log.info("Sending approval notification for request {}", request.getRequestNumber());
        
        String message = buildApprovalMessage(request, approver);
        log.info("APPROVAL NOTIFICATION: {}", message);
    }

    @Async
    public void sendRejectionNotification(Request request, User rejector, String reason) {
        log.info("Sending rejection notification for request {}", request.getRequestNumber());
        
        String message = buildRejectionMessage(request, rejector, reason);
        log.info("REJECTION NOTIFICATION: {}", message);
    }

    public void sendDailyDigest(User user) {
        log.info("Sending daily digest to user {}", user.getUsername());
        
        // Get pending requests for the user
        List<Request> pendingRequests = requestRepository.findPendingRequestsForReviewer(
            user.getId(), 
            List.of(RequestStatus.HRO_REVIEW, RequestStatus.HRMO_REVIEW, RequestStatus.HHRMD_REVIEW)
        );
        
        if (!pendingRequests.isEmpty()) {
            String message = buildDailyDigestMessage(user, pendingRequests);
            log.info("DAILY DIGEST: {}", message);
        }
    }

    public void checkOverdueRequests() {
        log.info("Checking for overdue requests...");
        
        List<Request> overdueRequests = requestRepository.findOverdueRequests(
            LocalDateTime.now(),
            List.of(RequestStatus.HRO_REVIEW, RequestStatus.HRMO_REVIEW, RequestStatus.HHRMD_REVIEW)
        );
        
        for (Request request : overdueRequests) {
            sendOverdueNotification(request);
            
            // Escalate if needed
            if (shouldEscalate(request)) {
                escalateRequest(request);
            }
        }
    }

    private boolean shouldEscalate(Request request) {
        // Check if request has been overdue for more than 3 days
        if (request.getDueDate() != null) {
            long daysOverdue = java.time.Duration.between(
                request.getDueDate(), 
                LocalDateTime.now()
            ).toDays();
            return daysOverdue >= 3;
        }
        return false;
    }

    private void escalateRequest(Request request) {
        log.warn("Escalating overdue request: {}", request.getRequestNumber());
        // Escalation logic - could assign to higher authority or send to admin
    }

    private String buildWorkflowMessage(Request request, RequestWorkflow workflow, String action) {
        return String.format(
            "Workflow Update - Request %s\n" +
            "Employee: %s\n" +
            "Type: %s\n" +
            "Current Step: %s\n" +
            "Action: %s\n" +
            "Reviewer: %s\n" +
            "Date: %s",
            request.getRequestNumber(),
            request.getEmployee().getFullName(),
            request.getRequestType().getDescription(),
            workflow.getStepName(),
            action,
            workflow.getReviewer() != null ? workflow.getReviewer().getFullName() : "Unassigned",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    private String buildStatusChangeMessage(Request request, RequestStatus oldStatus, RequestStatus newStatus, User user) {
        return String.format(
            "Status Change - Request %s\n" +
            "Employee: %s\n" +
            "Type: %s\n" +
            "Status: %s -> %s\n" +
            "Changed by: %s\n" +
            "Date: %s",
            request.getRequestNumber(),
            request.getEmployee().getFullName(),
            request.getRequestType().getDescription(),
            oldStatus.getDescription(),
            newStatus.getDescription(),
            user.getFullName(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    private String buildOverdueMessage(Request request) {
        return String.format(
            "� OVERDUE REQUEST - %s\n" +
            "Employee: %s\n" +
            "Type: %s\n" +
            "Current Stage: %s\n" +
            "Due Date: %s\n" +
            "Days Overdue: %d\n" +
            "Current Reviewer: %s",
            request.getRequestNumber(),
            request.getEmployee().getFullName(),
            request.getRequestType().getDescription(),
            request.getCurrentStage(),
            request.getDueDate() != null ? request.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Not set",
            request.getDueDate() != null ? java.time.Duration.between(request.getDueDate(), LocalDateTime.now()).toDays() : 0,
            request.getCurrentReviewer() != null ? request.getCurrentReviewer().getFullName() : "Unassigned"
        );
    }

    private String buildApprovalMessage(Request request, User approver) {
        return String.format(
            " REQUEST APPROVED - %s\n" +
            "Employee: %s\n" +
            "Type: %s\n" +
            "Approved by: %s\n" +
            "Date: %s",
            request.getRequestNumber(),
            request.getEmployee().getFullName(),
            request.getRequestType().getDescription(),
            approver.getFullName(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    private String buildRejectionMessage(Request request, User rejector, String reason) {
        return String.format(
            "L REQUEST REJECTED - %s\n" +
            "Employee: %s\n" +
            "Type: %s\n" +
            "Rejected by: %s\n" +
            "Reason: %s\n" +
            "Date: %s",
            request.getRequestNumber(),
            request.getEmployee().getFullName(),
            request.getRequestType().getDescription(),
            rejector.getFullName(),
            reason,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    private String buildDailyDigestMessage(User user, List<Request> pendingRequests) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("=== Daily Digest for %s\n", user.getFullName()));
        message.append(String.format("You have %d pending requests for review:\n\n", pendingRequests.size()));
        
        for (Request request : pendingRequests) {
            message.append(String.format(
                "• %s - %s (%s)\n  Employee: %s | Due: %s\n",
                request.getRequestNumber(),
                request.getRequestType().getDescription(),
                request.getStatus().getDescription(),
                request.getEmployee().getFullName(),
                request.getDueDate() != null ? 
                    request.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : 
                    "No due date"
            ));
        }
        
        return message.toString();
    }

    @Async
    public void sendComplaintStatusNotification(com.zanzibar.csms.entity.Complaint complaint, com.zanzibar.csms.entity.enums.ComplaintStatus newStatus) {
        log.info("Sending complaint status notification for complaint {} - status: {}", 
            complaint.getComplaintNumber(), newStatus);
        
        String message = String.format(
            "Complaint Status Update:\n" +
            "Complaint Number: %s\n" +
            "New Status: %s\n" +
            "Title: %s\n" +
            "Date: %s",
            complaint.getComplaintNumber(),
            newStatus.getDescription(),
            complaint.getTitle(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
        
        log.info("COMPLAINT NOTIFICATION: {}", message);
    }

    @Async
    public void sendInvestigatorAssignmentNotification(com.zanzibar.csms.entity.Complaint complaint, User investigator) {
        log.info("Sending investigator assignment notification for complaint {} to {}", 
            complaint.getComplaintNumber(), investigator.getUsername());
        
        String message = String.format(
            "Investigator Assignment:\n" +
            "Complaint Number: %s\n" +
            "Assigned to: %s\n" +
            "Title: %s\n" +
            "Priority: %s\n" +
            "Date Assigned: %s",
            complaint.getComplaintNumber(),
            investigator.getFullName(),
            complaint.getTitle(),
            complaint.getSeverity().name(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
        
        log.info("INVESTIGATOR ASSIGNMENT NOTIFICATION: {}", message);
    }

    // Termination Request Notifications
    @Async
    public void notifyTerminationRequestSubmitted(com.zanzibar.csms.entity.TerminationRequest terminationRequest) {
        log.info("Sending termination request submission notification for request {}", 
            terminationRequest.getRequestNumber());
        
        String message = String.format(
            "🚨 NEW TERMINATION REQUEST SUBMITTED\n" +
            "Request Number: %s\n" +
            "Employee: %s (ID: %s)\n" +
            "Scenario: %s\n" +
            "Submitted by: %s\n" +
            "Institution: %s\n" +
            "Reason: %s\n" +
            "Date Submitted: %s\n" +
            "Status: PENDING REVIEW",
            terminationRequest.getRequestNumber(),
            terminationRequest.getEmployee().getFullName(),
            terminationRequest.getEmployee().getPayrollNumber(),
            terminationRequest.getScenario().getDescription(),
            terminationRequest.getSubmittedBy().getFullName(),
            terminationRequest.getEmployee().getInstitution().getName(),
            terminationRequest.getReason(),
            terminationRequest.getSubmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
        
        log.info("TERMINATION REQUEST SUBMISSION NOTIFICATION: {}", message);
        
        // In a real implementation, this would send notifications to HHRMD and DO
        // who have authority to approve termination requests
    }

    @Async
    public void notifyTerminationRequestApproved(com.zanzibar.csms.entity.TerminationRequest terminationRequest) {
        log.info("Sending termination request approval notification for request {}", 
            terminationRequest.getRequestNumber());
        
        String message = String.format(
            "✅ TERMINATION REQUEST APPROVED\n" +
            "Request Number: %s\n" +
            "Employee: %s (ID: %s)\n" +
            "Scenario: %s\n" +
            "Approved by: %s\n" +
            "Institution: %s\n" +
            "Employee Status: TERMINATED\n" +
            "Approval Date: %s\n" +
            "Comments: %s",
            terminationRequest.getRequestNumber(),
            terminationRequest.getEmployee().getFullName(),
            terminationRequest.getEmployee().getPayrollNumber(),
            terminationRequest.getScenario().getDescription(),
            terminationRequest.getApprover().getFullName(),
            terminationRequest.getEmployee().getInstitution().getName(),
            terminationRequest.getApprovalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            terminationRequest.getComments() != null ? terminationRequest.getComments() : "No additional comments"
        );
        
        log.info("TERMINATION REQUEST APPROVAL NOTIFICATION: {}", message);
        
        // Notify the HRO who submitted the request and relevant stakeholders
    }

    @Async
    public void notifyTerminationRequestRejected(com.zanzibar.csms.entity.TerminationRequest terminationRequest) {
        log.info("Sending termination request rejection notification for request {}", 
            terminationRequest.getRequestNumber());
        
        String message = String.format(
            "❌ TERMINATION REQUEST REJECTED\n" +
            "Request Number: %s\n" +
            "Employee: %s (ID: %s)\n" +
            "Scenario: %s\n" +
            "Rejected by: %s\n" +
            "Institution: %s\n" +
            "Rejection Date: %s\n" +
            "Rejection Reason: %s\n" +
            "Next Steps: Please review the rejection reason and submit a corrected request if applicable.",
            terminationRequest.getRequestNumber(),
            terminationRequest.getEmployee().getFullName(),
            terminationRequest.getEmployee().getPayrollNumber(),
            terminationRequest.getScenario().getDescription(),
            terminationRequest.getApprover().getFullName(),
            terminationRequest.getEmployee().getInstitution().getName(),
            terminationRequest.getApprovalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            terminationRequest.getComments() != null ? terminationRequest.getComments() : "No reason provided"
        );
        
        log.info("TERMINATION REQUEST REJECTION NOTIFICATION: {}", message);
        
        // Notify the HRO who submitted the request so they can make corrections
    }

    // Additional notification methods for business rules
    
    @Async
    public void sendDelegationNotification(com.zanzibar.csms.entity.UserDelegation delegation) {
        log.info("Sending delegation notification for delegation: {}", delegation.getId());
        
        String message = buildDelegationMessage(delegation);
        log.info("DELEGATION NOTIFICATION: {}", message);
    }

    @Async
    public void sendDelegationRevocationNotification(com.zanzibar.csms.entity.UserDelegation delegation) {
        log.info("Sending delegation revocation notification for delegation: {}", delegation.getId());
        
        String message = buildDelegationRevocationMessage(delegation);
        log.info("DELEGATION REVOCATION NOTIFICATION: {}", message);
    }

    @Async
    public void sendEscalationNotification(Request request, User escalatedTo) {
        log.info("Sending escalation notification for request: {} to user: {}", 
            request.getRequestNumber(), escalatedTo.getUsername());
        
        String message = buildEscalationMessage(request, escalatedTo);
        log.info("ESCALATION NOTIFICATION: {}", message);
    }

    @Async
    public void sendSLAWarningNotification(Request request) {
        log.info("Sending SLA warning notification for request: {}", request.getRequestNumber());
        
        String message = buildSLAWarningMessage(request);
        log.info("SLA WARNING NOTIFICATION: {}", message);
    }

    @Async
    public void sendDelegationActivationNotification(com.zanzibar.csms.entity.UserDelegation delegation) {
        log.info("Sending delegation activation notification for delegation: {}", delegation.getId());
        
        String message = buildDelegationActivationMessage(delegation);
        log.info("DELEGATION ACTIVATION NOTIFICATION: {}", message);
    }

    @Async
    public void sendDelegationExpirationNotification(com.zanzibar.csms.entity.UserDelegation delegation) {
        log.info("Sending delegation expiration notification for delegation: {}", delegation.getId());
        
        String message = buildDelegationExpirationMessage(delegation);
        log.info("DELEGATION EXPIRATION NOTIFICATION: {}", message);
    }

    @Async
    public void sendEmergencyDelegationNotification(com.zanzibar.csms.entity.UserDelegation delegation) {
        log.info("Sending emergency delegation notification for delegation: {}", delegation.getId());
        
        String message = buildEmergencyDelegationMessage(delegation);
        log.info("EMERGENCY DELEGATION NOTIFICATION: {}", message);
    }

    @Async
    public void sendDelegationExtensionNotification(com.zanzibar.csms.entity.UserDelegation delegation) {
        log.info("Sending delegation extension notification for delegation: {}", delegation.getId());
        
        String message = buildDelegationExtensionMessage(delegation);
        log.info("DELEGATION EXTENSION NOTIFICATION: {}", message);
    }

    // Message builders for delegation notifications
    private String buildDelegationMessage(com.zanzibar.csms.entity.UserDelegation delegation) {
        return String.format(
            "A delegation has been created:\n" +
            "From: %s\n" +
            "To: %s\n" +
            "Period: %s to %s\n" +
            "Reason: %s\n" +
            "Duration: %d hours",
            delegation.getDelegator().getUsername(),
            delegation.getDelegate().getUsername(),
            delegation.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            delegation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            delegation.getReason(),
            delegation.getDurationInHours()
        );
    }

    private String buildDelegationRevocationMessage(com.zanzibar.csms.entity.UserDelegation delegation) {
        return String.format(
            "A delegation has been revoked:\n" +
            "From: %s\n" +
            "To: %s\n" +
            "Revoked By: %s\n" +
            "Reason: %s",
            delegation.getDelegator().getUsername(),
            delegation.getDelegate().getUsername(),
            delegation.getRevokedBy(),
            delegation.getRevocationReason()
        );
    }

    private String buildEscalationMessage(Request request, User escalatedTo) {
        return String.format(
            "Request has been escalated to you:\n" +
            "Request Number: %s\n" +
            "Type: %s\n" +
            "Employee: %s\n" +
            "Submitted: %s\n" +
            "Current Status: %s\n" +
            "Please review and take action.",
            request.getRequestNumber(),
            request.getRequestType(),
            request.getEmployee().getFullName(),
            request.getSubmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            request.getStatus()
        );
    }

    private String buildSLAWarningMessage(Request request) {
        return String.format(
            "SLA Warning - Request approaching deadline:\n" +
            "Request Number: %s\n" +
            "Type: %s\n" +
            "Employee: %s\n" +
            "Due Date: %s\n" +
            "Current Status: %s\n" +
            "Please review urgently to avoid SLA breach.",
            request.getRequestNumber(),
            request.getRequestType(),
            request.getEmployee().getFullName(),
            request.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            request.getStatus()
        );
    }

    private String buildDelegationActivationMessage(com.zanzibar.csms.entity.UserDelegation delegation) {
        return String.format(
            "Delegation is now active:\n" +
            "You are now acting on behalf of: %s\n" +
            "Valid until: %s\n" +
            "Reason: %s",
            delegation.getDelegator().getUsername(),
            delegation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            delegation.getReason()
        );
    }

    private String buildDelegationExpirationMessage(com.zanzibar.csms.entity.UserDelegation delegation) {
        return String.format(
            "Delegation has expired:\n" +
            "Delegation to: %s\n" +
            "Expired at: %s\n" +
            "You have resumed your normal duties.",
            delegation.getDelegate().getUsername(),
            delegation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

    private String buildEmergencyDelegationMessage(com.zanzibar.csms.entity.UserDelegation delegation) {
        return String.format(
            "URGENT: Emergency delegation activated:\n" +
            "You are now acting on behalf of: %s\n" +
            "Valid until: %s\n" +
            "Reason: %s\n" +
            "Please check for pending urgent requests.",
            delegation.getDelegator().getUsername(),
            delegation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            delegation.getReason()
        );
    }

    private String buildDelegationExtensionMessage(com.zanzibar.csms.entity.UserDelegation delegation) {
        return String.format(
            "Delegation has been extended:\n" +
            "Acting on behalf of: %s\n" +
            "New end date: %s\n" +
            "Extension reason: %s",
            delegation.getDelegator().getUsername(),
            delegation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            delegation.getExtensionReason()
        );
    }
}