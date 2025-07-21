package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.RequestDto;
import com.zanzibar.csms.dto.RequestWorkflowDto;
import com.zanzibar.csms.entity.Employee;
import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.RequestWorkflow;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.repository.EmployeeRepository;
import com.zanzibar.csms.repository.RequestRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkflowService workflowService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public Page<RequestDto> getAllRequests(Pageable pageable) {
        Page<Request> requests = requestRepository.findAll(pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public RequestDto getRequestById(String id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        return convertToDto(request);
    }

    @Transactional(readOnly = true)
    public RequestDto getRequestByNumber(String requestNumber) {
        Request request = requestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with number: " + requestNumber));
        return convertToDto(request);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> getRequestsByStatus(RequestStatus status, Pageable pageable) {
        Page<Request> requests = requestRepository.findByStatus(status, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> getRequestsByType(RequestType type, Pageable pageable) {
        Page<Request> requests = requestRepository.findByRequestType(type, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> getRequestsByEmployee(String employeeId, Pageable pageable) {
        Page<Request> requests = requestRepository.findByEmployeeId(employeeId, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> getRequestsBySubmitter(String submitterId, Pageable pageable) {
        Page<Request> requests = requestRepository.findBySubmittedById(submitterId, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> getPendingRequestsForReviewer(String reviewerId, Pageable pageable) {
        Page<Request> requests = requestRepository.findByCurrentReviewerId(reviewerId, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> getRequestsByInstitution(String institutionId, Pageable pageable) {
        Page<Request> requests = requestRepository.findByInstitutionId(institutionId, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Page<RequestDto> searchRequests(String searchTerm, Pageable pageable) {
        Page<Request> requests = requestRepository.searchRequests(searchTerm, pageable);
        return requests.map(this::convertToDto);
    }

    @Transactional
    public RequestDto createRequest(RequestDto requestDto, String currentUserId) {
        User submitter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + requestDto.getEmployeeId()));

        Request request = Request.builder()
                .requestNumber(generateRequestNumber(requestDto.getRequestType()))
                .employee(employee)
                .submittedBy(submitter)
                .requestType(requestDto.getRequestType())
                .description(requestDto.getDescription())
                .priority(requestDto.getPriority())
                .comments(requestDto.getComments())
                .build();

        request.setCreatedBy(currentUserId);
        Request savedRequest = requestRepository.save(request);

        auditService.logAction(
                currentUserId,
                submitter.getUsername(),
                "CREATE_REQUEST",
                "Request",
                savedRequest.getId(),
                null,
                requestDto.getRequestType().name() + " request created",
                true,
                null
        );

        return convertToDto(savedRequest);
    }

    @Transactional
    public RequestDto submitRequest(String requestId, String currentUserId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));

        User submitter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        if (!request.getSubmittedBy().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Only the submitter can submit this request");
        }

        if (request.getStatus() != RequestStatus.DRAFT) {
            throw new IllegalStateException("Request is not in draft status");
        }

        request.setStatus(RequestStatus.SUBMITTED);
        request.setSubmissionDate(LocalDateTime.now());

        // Initialize workflow
        workflowService.initializeWorkflow(request);

        Request savedRequest = requestRepository.save(request);

        auditService.logAction(
                currentUserId,
                submitter.getUsername(),
                "SUBMIT_REQUEST",
                "Request",
                requestId,
                RequestStatus.DRAFT.name(),
                RequestStatus.SUBMITTED.name(),
                true,
                null
        );

        return convertToDto(savedRequest);
    }

    @Transactional
    public RequestDto processRequest(String requestId, RequestStatus decision, String comments, String currentUserId) {
        User reviewer = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        boolean processed = workflowService.processWorkflowStep(requestId, currentUserId, decision, comments);

        if (!processed) {
            throw new IllegalStateException("Failed to process workflow step");
        }

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));

        return convertToDto(request);
    }

    @Transactional
    public void cancelRequest(String requestId, String reason, String currentUserId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found: " + requestId));

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserId));

        if (!request.getSubmittedBy().getId().equals(currentUserId) && !user.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("Only the submitter or admin can cancel this request");
        }

        if (request.getStatus().isTerminal()) {
            throw new IllegalStateException("Cannot cancel a request that is already completed");
        }

        request.setStatus(RequestStatus.CANCELLED);
        request.setRejectionReason(reason);
        requestRepository.save(request);

        auditService.logAction(
                currentUserId,
                user.getUsername(),
                "CANCEL_REQUEST",
                "Request",
                requestId,
                null,
                "Request cancelled: " + reason,
                true,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<RequestWorkflowDto> getRequestWorkflowHistory(String requestId) {
        List<RequestWorkflow> workflowSteps = workflowService.getWorkflowHistory(requestId);
        return workflowSteps.stream()
                .map(this::convertWorkflowToDto)
                .collect(Collectors.toList());
    }

    private String generateRequestNumber(RequestType requestType) {
        String prefix = requestType.name().substring(0, 3).toUpperCase();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp;
    }

    private RequestDto convertToDto(Request request) {
        RequestDto.RequestDtoBuilder builder = RequestDto.builder()
                .id(request.getId())
                .requestNumber(request.getRequestNumber())
                .employeeId(request.getEmployee().getId())
                .employeeName(request.getEmployee().getFullName())
                .submittedById(request.getSubmittedBy().getId())
                .submittedByName(request.getSubmittedBy().getFullName())
                .submissionDate(request.getSubmissionDate())
                .status(request.getStatus())
                .requestType(request.getRequestType())
                .comments(request.getComments())
                .rejectionReason(request.getRejectionReason())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .currentStage(request.getCurrentStage())
                .institutionId(request.getEmployee().getInstitution() != null ? request.getEmployee().getInstitution().getId() : null)
                .institutionName(request.getEmployee().getInstitution() != null ? request.getEmployee().getInstitution().getName() : null)
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt());

        if (request.getApprover() != null) {
            builder.approverId(request.getApprover().getId())
                    .approverName(request.getApprover().getFullName())
                    .approvalDate(request.getApprovalDate());
        }

        if (request.getCurrentReviewer() != null) {
            builder.currentReviewerId(request.getCurrentReviewer().getId())
                    .currentReviewerName(request.getCurrentReviewer().getFullName());
        }

        return builder.build();
    }

    private RequestWorkflowDto convertWorkflowToDto(RequestWorkflow workflow) {
        RequestWorkflowDto.RequestWorkflowDtoBuilder builder = RequestWorkflowDto.builder()
                .id(workflow.getId())
                .requestId(workflow.getRequest().getId())
                .stepNumber(workflow.getStepNumber())
                .stepName(workflow.getStepName())
                .requiredRole(workflow.getRequiredRole())
                .status(workflow.getStatus())
                .startDate(workflow.getStartDate())
                .completionDate(workflow.getCompletionDate())
                .comments(workflow.getComments())
                .isCurrentStep(workflow.getIsCurrentStep())
                .daysInStep(workflow.getDaysInStep());

        if (workflow.getReviewer() != null) {
            builder.reviewerId(workflow.getReviewer().getId())
                    .reviewerName(workflow.getReviewer().getFullName());
        }

        return builder.build();
    }
}