package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.*;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.exception.ValidationException;
import com.zanzibar.csms.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestRevisionService {

    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;
    private final NotificationService notificationService;
    private final AuditService auditService;

    /**
     * Creates a revision of a rejected request
     */
    @Transactional
    public Request createRevision(String requestId, String userId) {
        log.info("Creating revision for request: {} by user: {}", requestId, userId);

        Request originalRequest = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Validate that the request can be revised
        validateRevisionEligibility(originalRequest, userId);

        // Find the actual original request (in case this is already a revision)
        Request rootRequest = originalRequest.getOriginalRequest() != null ? 
            originalRequest.getOriginalRequest() : originalRequest;

        // Calculate revision number
        int maxRevisionNumber = rootRequest.getRevisions().stream()
            .mapToInt(Request::getRevisionNumber)
            .max()
            .orElse(0);

        // Create the revision
        Request revision = createRevisionFromRequest(originalRequest);
        revision.setOriginalRequest(rootRequest);
        revision.setRevisionNumber(maxRevisionNumber + 1);
        revision.setRevisionReason(originalRequest.getRejectionReason());
        revision.setRevisedAt(LocalDateTime.now());
        revision.setStatus(RequestStatus.DRAFT);
        revision.setRequestNumber(generateRevisionRequestNumber(originalRequest, maxRevisionNumber + 1));

        // Clear workflow steps for the revision
        revision.setWorkflowSteps(new ArrayList<>());
        revision.setCurrentReviewer(null);
        revision.setCurrentStage(null);
        revision.setApprover(null);
        revision.setApprovalDate(null);
        revision.setRejectionReason(null);

        // Save the revision
        Request savedRevision = requestRepository.save(revision);

        // Mark original request as revised
        originalRequest.setIsRevised(true);
        requestRepository.save(originalRequest);

        // Log audit event
        auditService.logAction(
            userId,
            null,
            "REQUEST_REVISION_CREATED",
            "Request",
            savedRevision.getId(),
            originalRequest.getRequestNumber(),
            savedRevision.getRequestNumber(),
            true,
            null
        );

        log.info("Created revision {} for request {}", savedRevision.getRequestNumber(), originalRequest.getRequestNumber());

        return savedRevision;
    }

    /**
     * Submits a revised request for approval
     */
    @Transactional
    public Request submitRevision(String revisionId, String userId) {
        log.info("Submitting revision: {} by user: {}", revisionId, userId);

        Request revision = requestRepository.findById(revisionId)
            .orElseThrow(() -> new ResourceNotFoundException("Revision not found with id: " + revisionId));

        // Validate that this is actually a revision
        if (!revision.isRevision()) {
            throw new ValidationException("Request is not a revision");
        }

        // Validate that the revision is in DRAFT status
        if (revision.getStatus() != RequestStatus.DRAFT) {
            throw new ValidationException("Only draft revisions can be submitted");
        }

        // Update status to SUBMITTED
        revision.setStatus(RequestStatus.SUBMITTED);
        revision.setSubmissionDate(LocalDateTime.now());

        // Initialize workflow for the revision
        workflowService.initializeWorkflow(revision);

        Request savedRevision = requestRepository.save(revision);

        // Send notification
        notificationService.sendWorkflowNotification(savedRevision, null, "REVISION_SUBMITTED");

        // Log audit event
        auditService.logAction(
            userId,
            null,
            "REQUEST_REVISION_SUBMITTED",
            "Request",
            savedRevision.getId(),
            null,
            savedRevision.getRequestNumber(),
            true,
            null
        );

        return savedRevision;
    }

    /**
     * Returns a request for clarification without rejecting it
     */
    @Transactional
    public Request returnForClarification(String requestId, String clarificationReason, String userId) {
        log.info("Returning request {} for clarification by user: {}", requestId, userId);

        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Validate that the request is in a reviewable state
        if (!request.getStatus().isPending()) {
            throw new ValidationException("Request is not in a reviewable state");
        }

        // Update status to RETURNED
        request.setStatus(RequestStatus.RETURNED);
        request.setComments(clarificationReason);

        // Pause current workflow step
        request.getWorkflowSteps().stream()
            .filter(workflow -> Boolean.TRUE.equals(workflow.getIsCurrentStep()))
            .findFirst()
            .ifPresent(workflow -> {
                workflow.setStatus(RequestStatus.RETURNED);
                workflow.setComments(clarificationReason);
            });

        Request savedRequest = requestRepository.save(request);

        // Send notification
        notificationService.sendWorkflowNotification(savedRequest, null, "REQUEST_RETURNED");

        // Log audit event
        auditService.logAction(
            userId,
            null,
            "REQUEST_RETURNED_FOR_CLARIFICATION",
            "Request",
            savedRequest.getId(),
            null,
            clarificationReason,
            true,
            null
        );

        return savedRequest;
    }

    /**
     * Resubmits a returned request after clarification
     */
    @Transactional
    public Request resubmitAfterClarification(String requestId, String clarificationResponse, String userId) {
        log.info("Resubmitting request {} after clarification by user: {}", requestId, userId);

        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Validate that the request is in RETURNED status
        if (request.getStatus() != RequestStatus.RETURNED) {
            throw new ValidationException("Only returned requests can be resubmitted");
        }

        // Update comments with clarification response
        String updatedComments = request.getComments() + "\n\nClarification Response: " + clarificationResponse;
        request.setComments(updatedComments);

        // Resume workflow at the current step
        request.getWorkflowSteps().stream()
            .filter(workflow -> Boolean.TRUE.equals(workflow.getIsCurrentStep()))
            .findFirst()
            .ifPresent(workflow -> {
                workflow.setStatus(RequestStatus.SUBMITTED);
                workflow.setComments(workflow.getComments() + "\n\nClarified: " + clarificationResponse);
                request.setStatus(RequestStatus.valueOf(workflow.getRequiredRole().name() + "_REVIEW"));
            });

        Request savedRequest = requestRepository.save(request);

        // Send notification
        notificationService.sendWorkflowNotification(savedRequest, null, "REQUEST_CLARIFIED");

        // Log audit event
        auditService.logAction(
            userId,
            null,
            "REQUEST_RESUBMITTED_AFTER_CLARIFICATION",
            "Request",
            savedRequest.getId(),
            null,
            clarificationResponse,
            true,
            null
        );

        return savedRequest;
    }

    /**
     * Gets revision history for a request
     */
    public List<Request> getRevisionHistory(String requestId) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        // Find the root request
        Request rootRequest = request.getOriginalRequest() != null ? 
            request.getOriginalRequest() : request;

        // Get all revisions
        List<Request> history = new ArrayList<>();
        history.add(rootRequest);
        history.addAll(rootRequest.getRevisions());

        // Sort by revision number
        history.sort((r1, r2) -> r1.getRevisionNumber().compareTo(r2.getRevisionNumber()));

        return history;
    }

    private void validateRevisionEligibility(Request request, String userId) {
        // Check if request is rejected
        if (request.getStatus() != RequestStatus.REJECTED) {
            throw new ValidationException("Only rejected requests can be revised");
        }

        // Check if the user has permission to revise (should be the original submitter or HRO)
        if (!request.getSubmittedBy().getId().equals(userId)) {
            // Additional check for HRO role can be added here
            throw new ValidationException("You don't have permission to revise this request");
        }

        // Check if already revised
        if (Boolean.TRUE.equals(request.getIsRevised())) {
            throw new ValidationException("This request has already been revised. Please check the latest revision.");
        }
    }

    private Request createRevisionFromRequest(Request original) {
        // This method should be overridden for specific request types
        // For now, create a basic copy
        Request revision = new Request();
        revision.setEmployee(original.getEmployee());
        revision.setSubmittedBy(original.getSubmittedBy());
        revision.setRequestType(original.getRequestType());
        revision.setPriority(original.getPriority());
        revision.setDescription(original.getDescription());
        revision.setDueDate(original.getDueDate());
        
        // Copy documents
        original.getDocuments().forEach(doc -> {
            RequestDocument newDoc = new RequestDocument();
            newDoc.setRequest(revision);
            newDoc.setDocumentType(doc.getDocumentType());
            newDoc.setFileName(doc.getFileName());
            newDoc.setFilePath(doc.getFilePath());
            newDoc.setFileSize(doc.getFileSize());
            // Document creation time is handled by BaseEntity
            revision.getDocuments().add(newDoc);
        });

        return revision;
    }

    private String generateRevisionRequestNumber(Request original, int revisionNumber) {
        return original.getRequestNumber() + "-R" + revisionNumber;
    }
}