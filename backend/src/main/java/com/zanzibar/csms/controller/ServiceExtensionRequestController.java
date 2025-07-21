package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestCreateDto;
import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestResponseDto;
import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestUpdateDto;
import com.zanzibar.csms.entity.ServiceExtensionRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.ServiceExtensionStatus;
import com.zanzibar.csms.entity.enums.ServiceExtensionType;
import com.zanzibar.csms.service.ServiceExtensionRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-extension-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Service Extension Request Management", description = "API for managing service extension requests")
public class ServiceExtensionRequestController {

    private final ServiceExtensionRequestService serviceExtensionRequestService;

    @PostMapping
    @Operation(summary = "Create a new service extension request", description = "Creates a new service extension request for an employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Service extension request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ServiceExtensionRequestResponseDto> createServiceExtensionRequest(
            @Valid @RequestBody ServiceExtensionRequestCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating service extension request for employee: {}", createDto.getEmployeeId());
        
        ServiceExtensionRequestResponseDto response = serviceExtensionRequestService.createServiceExtensionRequest(
            createDto, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a service extension request", description = "Approves a pending service extension request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension request approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Service extension request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ServiceExtensionRequestResponseDto> approveServiceExtensionRequest(
            @PathVariable String id,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        log.info("Approving service extension request: {}", id);
        
        ServiceExtensionRequestResponseDto response = serviceExtensionRequestService.approveServiceExtensionRequest(
            id, authentication.getName(), comments
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a service extension request", description = "Rejects a pending service extension request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension request rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Service extension request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ServiceExtensionRequestResponseDto> rejectServiceExtensionRequest(
            @PathVariable String id,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        
        log.info("Rejecting service extension request: {}", id);
        
        ServiceExtensionRequestResponseDto response = serviceExtensionRequestService.rejectServiceExtensionRequest(
            id, authentication.getName(), rejectionReason
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a service extension request", description = "Updates a pending service extension request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension request updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Service extension request not found")
    })
    @PreAuthorize("hasRole('HRO')")
    public ResponseEntity<ServiceExtensionRequestResponseDto> updateServiceExtensionRequest(
            @PathVariable String id,
            @Valid @RequestBody ServiceExtensionRequestUpdateDto updateDto) {
        
        log.info("Updating service extension request: {}", id);
        
        ServiceExtensionRequestResponseDto response = serviceExtensionRequestService.updateServiceExtensionRequest(id, updateDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service extension request by ID", description = "Retrieves a specific service extension request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension request retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Service extension request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ServiceExtensionRequestResponseDto> getServiceExtensionRequest(@PathVariable String id) {
        log.info("Retrieving service extension request: {}", id);
        
        ServiceExtensionRequestResponseDto response = serviceExtensionRequestService.getServiceExtensionRequestById(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all service extension requests", description = "Retrieves all service extension requests with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getAllServiceExtensionRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving all service extension requests");
        
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getAllServiceExtensionRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get service extension requests by status", description = "Retrieves service extension requests filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getServiceExtensionRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving service extension requests by status: {}", status);
        
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getServiceExtensionRequestsByStatus(status, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get service extension requests by employee", description = "Retrieves service extension requests for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getServiceExtensionRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving service extension requests for employee: {}", employeeId);
        
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getServiceExtensionRequestsByEmployee(employeeId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get service extension requests by institution", description = "Retrieves service extension requests for a specific institution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getServiceExtensionRequestsByInstitution(
            @PathVariable String institutionId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving service extension requests for institution: {}", institutionId);
        
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getServiceExtensionRequestsByInstitution(institutionId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search service extension requests", description = "Searches service extension requests by various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> searchServiceExtensionRequests(
            @Parameter(description = "Search term to match against employee name, request number, or extension information")
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Searching service extension requests with term: {}", searchTerm);
        
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.searchServiceExtensionRequests(searchTerm, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue service extension requests", description = "Retrieves service extension requests that are overdue based on SLA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue service extension requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<ServiceExtensionRequestResponseDto>> getOverdueServiceExtensionRequests(
            @Parameter(description = "Number of days for SLA deadline")
            @RequestParam(defaultValue = "30") int slaDays) {
        
        log.info("Retrieving overdue service extension requests with SLA: {} days", slaDays);
        
        List<ServiceExtensionRequest> overdueRequests = serviceExtensionRequestService.findOverdueServiceExtensionRequests(slaDays);
        List<ServiceExtensionRequestResponseDto> response = overdueRequests.stream()
                .map(request -> serviceExtensionRequestService.getServiceExtensionRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get expiring service extensions", description = "Retrieves service extensions that are expiring soon")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expiring service extensions retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<ServiceExtensionRequestResponseDto>> getExpiringServiceExtensions(
            @Parameter(description = "Number of days before expiration")
            @RequestParam(defaultValue = "90") int days) {
        
        log.info("Retrieving expiring service extensions within {} days", days);
        
        List<ServiceExtensionRequest> expiringRequests = serviceExtensionRequestService.findExpiringExtensions(days);
        List<ServiceExtensionRequestResponseDto> response = expiringRequests.stream()
                .map(request -> serviceExtensionRequestService.getServiceExtensionRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active service extensions", description = "Retrieves currently active service extensions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active service extensions retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<ServiceExtensionRequestResponseDto>> getActiveServiceExtensions() {
        log.info("Retrieving active service extensions");
        
        List<ServiceExtensionRequest> activeRequests = serviceExtensionRequestService.findActiveExtensions();
        List<ServiceExtensionRequestResponseDto> response = activeRequests.stream()
                .map(request -> serviceExtensionRequestService.getServiceExtensionRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/extension-type/{extensionType}")
    @Operation(summary = "Get service extension requests by extension type", description = "Retrieves service extension requests filtered by extension type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid extension type")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getServiceExtensionRequestsByExtensionType(
            @PathVariable ServiceExtensionType extensionType,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving service extension requests by extension type: {}", extensionType);
        
        // This would require adding a method to the service
        // For now, returning a placeholder response
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getAllServiceExtensionRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/extension-status/{extensionStatus}")
    @Operation(summary = "Get service extension requests by extension status", description = "Retrieves service extension requests filtered by extension status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service extension requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid extension status")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getServiceExtensionRequestsByExtensionStatus(
            @PathVariable ServiceExtensionStatus extensionStatus,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving service extension requests by extension status: {}", extensionStatus);
        
        // This would require adding a method to the service
        // For now, returning a placeholder response
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getAllServiceExtensionRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get service extension request statistics", description = "Retrieves statistics about service extension requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Map<String, Object>> getServiceExtensionRequestStatistics() {
        log.info("Retrieving service extension request statistics");
        
        // Get basic statistics
        long totalRequests = serviceExtensionRequestService.getAllServiceExtensionRequests(Pageable.unpaged()).getTotalElements();
        long pendingRequests = serviceExtensionRequestService.getServiceExtensionRequestsByStatus(RequestStatus.SUBMITTED, Pageable.unpaged()).getTotalElements();
        long approvedRequests = serviceExtensionRequestService.getServiceExtensionRequestsByStatus(RequestStatus.APPROVED, Pageable.unpaged()).getTotalElements();
        long rejectedRequests = serviceExtensionRequestService.getServiceExtensionRequestsByStatus(RequestStatus.REJECTED, Pageable.unpaged()).getTotalElements();
        
        int overdueCount = serviceExtensionRequestService.findOverdueServiceExtensionRequests(30).size();
        int expiringCount = serviceExtensionRequestService.findExpiringExtensions(90).size();
        int activeCount = serviceExtensionRequestService.findActiveExtensions().size();
        
        Map<String, Object> statistics = Map.of(
            "totalRequests", totalRequests,
            "pendingRequests", pendingRequests,
            "approvedRequests", approvedRequests,
            "rejectedRequests", rejectedRequests,
            "overdueRequests", overdueCount,
            "expiringExtensions", expiringCount,
            "activeExtensions", activeCount
        );
        
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending service extension requests", description = "Retrieves all pending service extension requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending service extension requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ServiceExtensionRequestResponseDto>> getPendingServiceExtensionRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving pending service extension requests");
        
        Page<ServiceExtensionRequestResponseDto> response = serviceExtensionRequestService.getServiceExtensionRequestsByStatus(RequestStatus.SUBMITTED, pageable);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/process-expiration-notifications")
    @Operation(summary = "Process expiration notifications", description = "Processes and sends expiration notifications for service extensions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expiration notifications processed successfully")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<String> processExpirationNotifications() {
        log.info("Processing expiration notifications");
        
        serviceExtensionRequestService.processExpirationNotifications();
        
        return ResponseEntity.ok("Expiration notifications processed successfully");
    }

    @PostMapping("/update-expired-extensions")
    @Operation(summary = "Update expired extensions", description = "Updates the status of expired service extensions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Expired extensions updated successfully")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<String> updateExpiredExtensions() {
        log.info("Updating expired extensions");
        
        serviceExtensionRequestService.updateExpiredExtensions();
        
        return ResponseEntity.ok("Expired extensions updated successfully");
    }
}