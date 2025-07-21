package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.dismissal.DismissalRequestCreateDto;
import com.zanzibar.csms.dto.dismissal.DismissalRequestResponseDto;
import com.zanzibar.csms.dto.dismissal.DismissalRequestUpdateDto;
import com.zanzibar.csms.entity.DismissalRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.service.DismissalRequestService;
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
@RequestMapping("/api/dismissal-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dismissal Request Management", description = "API for managing dismissal requests for confirmed employees")
public class DismissalRequestController {

    private final DismissalRequestService dismissalRequestService;

    @PostMapping
    @Operation(summary = "Create a new dismissal request", description = "Creates a new dismissal request for a confirmed employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Dismissal request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<DismissalRequestResponseDto> createDismissalRequest(
            @Valid @RequestBody DismissalRequestCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating dismissal request for employee: {}", createDto.getEmployeeId());
        
        DismissalRequestResponseDto response = dismissalRequestService.createDismissalRequest(
            createDto, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a dismissal request", description = "Approves a pending dismissal request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal request approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Dismissal request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<DismissalRequestResponseDto> approveDismissalRequest(
            @PathVariable String id,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        log.info("Approving dismissal request: {}", id);
        
        DismissalRequestResponseDto response = dismissalRequestService.approveDismissalRequest(
            id, authentication.getName(), comments
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a dismissal request", description = "Rejects a pending dismissal request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal request rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Dismissal request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<DismissalRequestResponseDto> rejectDismissalRequest(
            @PathVariable String id,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        
        log.info("Rejecting dismissal request: {}", id);
        
        DismissalRequestResponseDto response = dismissalRequestService.rejectDismissalRequest(
            id, authentication.getName(), rejectionReason
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dismissal request", description = "Updates a pending dismissal request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal request updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Dismissal request not found")
    })
    @PreAuthorize("hasRole('HRO')")
    public ResponseEntity<DismissalRequestResponseDto> updateDismissalRequest(
            @PathVariable String id,
            @Valid @RequestBody DismissalRequestUpdateDto updateDto) {
        
        log.info("Updating dismissal request: {}", id);
        
        DismissalRequestResponseDto response = dismissalRequestService.updateDismissalRequest(id, updateDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dismissal request by ID", description = "Retrieves a specific dismissal request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal request retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Dismissal request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<DismissalRequestResponseDto> getDismissalRequest(@PathVariable String id) {
        log.info("Retrieving dismissal request: {}", id);
        
        DismissalRequestResponseDto response = dismissalRequestService.getDismissalRequestByIdDto(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all dismissal requests", description = "Retrieves all dismissal requests with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<Page<DismissalRequestResponseDto>> getAllDismissalRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving all dismissal requests");
        
        Page<DismissalRequestResponseDto> response = dismissalRequestService.getAllDismissalRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get dismissal requests by status", description = "Retrieves dismissal requests filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<Page<DismissalRequestResponseDto>> getDismissalRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving dismissal requests by status: {}", status);
        
        Page<DismissalRequestResponseDto> response = dismissalRequestService.getDismissalRequestsByStatus(status, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get dismissal requests by employee", description = "Retrieves dismissal requests for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<Page<DismissalRequestResponseDto>> getDismissalRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving dismissal requests for employee: {}", employeeId);
        
        Page<DismissalRequestResponseDto> response = dismissalRequestService.getDismissalRequestsByEmployee(employeeId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get dismissal requests by institution", description = "Retrieves dismissal requests for a specific institution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dismissal requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<Page<DismissalRequestResponseDto>> getDismissalRequestsByInstitution(
            @PathVariable String institutionId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving dismissal requests for institution: {}", institutionId);
        
        Page<DismissalRequestResponseDto> response = dismissalRequestService.getDismissalRequestsByInstitution(institutionId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search dismissal requests", description = "Searches dismissal requests by various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<Page<DismissalRequestResponseDto>> searchDismissalRequests(
            @Parameter(description = "Search term to match against employee name, request number, or dismissal reason")
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Searching dismissal requests with term: {}", searchTerm);
        
        Page<DismissalRequestResponseDto> response = dismissalRequestService.searchDismissalRequests(searchTerm, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue dismissal requests", description = "Retrieves dismissal requests that are overdue based on SLA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue dismissal requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<List<DismissalRequestResponseDto>> getOverdueDismissalRequests(
            @Parameter(description = "Number of days for SLA deadline")
            @RequestParam(defaultValue = "30") int slaDays) {
        
        log.info("Retrieving overdue dismissal requests with SLA: {} days", slaDays);
        
        List<DismissalRequest> overdueRequests = dismissalRequestService.findOverdueDismissalRequests(slaDays);
        List<DismissalRequestResponseDto> response = overdueRequests.stream()
                .map(request -> dismissalRequestService.getDismissalRequestByIdDto(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active-investigations")
    @Operation(summary = "Get active investigations", description = "Retrieves dismissal requests with active investigations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active investigations retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<List<DismissalRequestResponseDto>> getActiveInvestigations() {
        log.info("Retrieving active investigations");
        
        List<DismissalRequest> activeInvestigations = dismissalRequestService.findActiveInvestigations();
        List<DismissalRequestResponseDto> response = activeInvestigations.stream()
                .map(request -> dismissalRequestService.getDismissalRequestByIdDto(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appeal-period-expired")
    @Operation(summary = "Get requests with expired appeal period", description = "Retrieves dismissal requests where appeal period has expired")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requests with expired appeal period retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<List<DismissalRequestResponseDto>> getRequestsWithExpiredAppealPeriod() {
        log.info("Retrieving requests with expired appeal period");
        
        List<DismissalRequest> expiredAppealRequests = dismissalRequestService.findRequestsWithExpiredAppealPeriod();
        List<DismissalRequestResponseDto> response = expiredAppealRequests.stream()
                .map(request -> dismissalRequestService.getDismissalRequestByIdDto(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appeal-period-expiring")
    @Operation(summary = "Get requests with appeal period expiring soon", description = "Retrieves dismissal requests where appeal period is expiring soon")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requests with expiring appeal period retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<List<DismissalRequestResponseDto>> getRequestsWithAppealPeriodExpiringSoon(
            @Parameter(description = "Number of days before appeal period expiration")
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("Retrieving requests with appeal period expiring in {} days", days);
        
        List<DismissalRequest> expiringAppealRequests = dismissalRequestService.findRequestsWithAppealPeriodExpiringSoon(days);
        List<DismissalRequestResponseDto> response = expiringAppealRequests.stream()
                .map(request -> dismissalRequestService.getDismissalRequestByIdDto(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get dismissal request statistics", description = "Retrieves statistics about dismissal requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('DO')")
    public ResponseEntity<Map<String, Object>> getDismissalRequestStatistics() {
        log.info("Retrieving dismissal request statistics");
        
        // Get basic statistics
        long totalRequests = dismissalRequestService.getAllDismissalRequests(Pageable.unpaged()).getTotalElements();
        long pendingRequests = dismissalRequestService.getDismissalRequestsByStatus(RequestStatus.SUBMITTED, Pageable.unpaged()).getTotalElements();
        long approvedRequests = dismissalRequestService.getDismissalRequestsByStatus(RequestStatus.APPROVED, Pageable.unpaged()).getTotalElements();
        long rejectedRequests = dismissalRequestService.getDismissalRequestsByStatus(RequestStatus.REJECTED, Pageable.unpaged()).getTotalElements();
        
        int overdueCount = dismissalRequestService.findOverdueDismissalRequests(30).size();
        int activeInvestigationsCount = dismissalRequestService.findActiveInvestigations().size();
        int expiredAppealPeriodCount = dismissalRequestService.findRequestsWithExpiredAppealPeriod().size();
        int expiringAppealPeriodCount = dismissalRequestService.findRequestsWithAppealPeriodExpiringSoon(7).size();
        
        Map<String, Object> statistics = Map.of(
            "totalRequests", totalRequests,
            "pendingRequests", pendingRequests,
            "approvedRequests", approvedRequests,
            "rejectedRequests", rejectedRequests,
            "overdueRequests", overdueCount,
            "activeInvestigations", activeInvestigationsCount,
            "expiredAppealPeriod", expiredAppealPeriodCount,
            "expiringAppealPeriod", expiringAppealPeriodCount
        );
        
        return ResponseEntity.ok(statistics);
    }
}