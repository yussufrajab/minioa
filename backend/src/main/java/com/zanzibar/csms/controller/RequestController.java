package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.RequestDto;
import com.zanzibar.csms.dto.RequestWorkflowDto;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import com.zanzibar.csms.security.CurrentUser;
import com.zanzibar.csms.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Tag(name = "Request Management", description = "HR Request Management API")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    @Operation(summary = "Get all requests", description = "Get paginated list of all HR requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<RequestDto>> getAllRequests(
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getAllRequests(pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get request by ID", description = "Get HR request details by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO') or @requestService.isRequestAccessible(#id, authentication.name)")
    public ResponseEntity<RequestDto> getRequestById(@PathVariable String id) {
        RequestDto request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/number/{requestNumber}")
    @Operation(summary = "Get request by number", description = "Get HR request details by request number")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<RequestDto> getRequestByNumber(@PathVariable String requestNumber) {
        RequestDto request = requestService.getRequestByNumber(requestNumber);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get requests by status", description = "Get requests filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<RequestDto>> getRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getRequestsByStatus(status, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get requests by type", description = "Get requests filtered by request type")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<RequestDto>> getRequestsByType(
            @PathVariable RequestType type,
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getRequestsByType(type, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get requests for employee", description = "Get all requests for a specific employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<RequestDto>> getRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getRequestsByEmployee(employeeId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/my-submissions")
    @Operation(summary = "Get my submitted requests", description = "Get requests submitted by current user")
    public ResponseEntity<Page<RequestDto>> getMySubmissions(
            @CurrentUser User currentUser,
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getRequestsBySubmitter(currentUser.getId(), pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending-review")
    @Operation(summary = "Get pending requests for review", description = "Get requests pending review by current user")
    @PreAuthorize("hasAnyRole('HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<RequestDto>> getPendingRequestsForReview(
            @CurrentUser User currentUser,
            @PageableDefault(sort = "priority", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getPendingRequestsForReviewer(currentUser.getId(), pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get requests by institution", description = "Get requests for a specific institution")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO') or @requestService.hasInstitutionAccess(#institutionId, authentication.name)")
    public ResponseEntity<Page<RequestDto>> getRequestsByInstitution(
            @PathVariable String institutionId,
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.getRequestsByInstitution(institutionId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/search")
    @Operation(summary = "Search requests", description = "Search requests by employee name, request number, or description")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<Page<RequestDto>> searchRequests(
            @RequestParam String searchTerm,
            @PageableDefault(sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<RequestDto> requests = requestService.searchRequests(searchTerm, pageable);
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    @Operation(summary = "Create new request", description = "Create a new HR request in draft status")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO')")
    public ResponseEntity<RequestDto> createRequest(
            @Valid @RequestBody RequestDto requestDto,
            @CurrentUser User currentUser) {
        RequestDto createdRequest = requestService.createRequest(requestDto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit request", description = "Submit a draft request for processing")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO') or @requestService.isRequestOwner(#id, authentication.name)")
    public ResponseEntity<RequestDto> submitRequest(
            @PathVariable String id,
            @CurrentUser User currentUser) {
        RequestDto submittedRequest = requestService.submitRequest(id, currentUser.getId());
        return ResponseEntity.ok(submittedRequest);
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process request", description = "Approve or reject a request")
    @PreAuthorize("hasAnyRole('HHRMD', 'HRMO', 'HRO')")
    public ResponseEntity<RequestDto> processRequest(
            @PathVariable String id,
            @RequestBody @Parameter(description = "Processing decision") Map<String, Object> decision,
            @CurrentUser User currentUser) {
        
        RequestStatus status = RequestStatus.valueOf((String) decision.get("decision"));
        String comments = (String) decision.get("comments");
        
        RequestDto processedRequest = requestService.processRequest(id, status, comments, currentUser.getId());
        return ResponseEntity.ok(processedRequest);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel request", description = "Cancel a pending request")
    @PreAuthorize("hasRole('ADMIN') or @requestService.isRequestOwner(#id, authentication.name)")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable String id,
            @RequestBody @Parameter(description = "Cancellation reason") Map<String, String> request,
            @CurrentUser User currentUser) {
        
        String reason = request.get("reason");
        requestService.cancelRequest(id, reason, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/workflow")
    @Operation(summary = "Get request workflow history", description = "Get the complete workflow history for a request")
    @PreAuthorize("hasAnyRole('ADMIN', 'HHRMD', 'HRMO', 'HRO') or @requestService.isRequestAccessible(#id, authentication.name)")
    public ResponseEntity<List<RequestWorkflowDto>> getRequestWorkflowHistory(@PathVariable String id) {
        List<RequestWorkflowDto> workflowHistory = requestService.getRequestWorkflowHistory(id);
        return ResponseEntity.ok(workflowHistory);
    }
}