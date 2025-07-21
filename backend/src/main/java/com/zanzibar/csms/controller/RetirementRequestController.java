package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.retirement.RetirementRequestCreateDto;
import com.zanzibar.csms.dto.retirement.RetirementRequestResponseDto;
import com.zanzibar.csms.dto.retirement.RetirementRequestUpdateDto;
import com.zanzibar.csms.entity.RetirementRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RetirementType;
import com.zanzibar.csms.service.RetirementRequestService;
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
@RequestMapping("/api/retirement-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Retirement Request Management", description = "API for managing retirement requests")
public class RetirementRequestController {

    private final RetirementRequestService retirementRequestService;

    @PostMapping
    @Operation(summary = "Create a new retirement request", description = "Creates a new retirement request for an employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Retirement request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<RetirementRequestResponseDto> createRetirementRequest(
            @Valid @RequestBody RetirementRequestCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating retirement request for employee: {}", createDto.getEmployeeId());
        
        RetirementRequestResponseDto response = retirementRequestService.createRetirementRequest(
            createDto, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a retirement request", description = "Approves a pending retirement request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement request approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Retirement request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<RetirementRequestResponseDto> approveRetirementRequest(
            @PathVariable String id,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        log.info("Approving retirement request: {}", id);
        
        RetirementRequestResponseDto response = retirementRequestService.approveRetirementRequest(
            id, authentication.getName(), comments
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a retirement request", description = "Rejects a pending retirement request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement request rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Retirement request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<RetirementRequestResponseDto> rejectRetirementRequest(
            @PathVariable String id,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        
        log.info("Rejecting retirement request: {}", id);
        
        RetirementRequestResponseDto response = retirementRequestService.rejectRetirementRequest(
            id, authentication.getName(), rejectionReason
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a retirement request", description = "Updates a pending retirement request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement request updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Retirement request not found")
    })
    @PreAuthorize("hasRole('HRO')")
    public ResponseEntity<RetirementRequestResponseDto> updateRetirementRequest(
            @PathVariable String id,
            @Valid @RequestBody RetirementRequestUpdateDto updateDto) {
        
        log.info("Updating retirement request: {}", id);
        
        RetirementRequestResponseDto response = retirementRequestService.updateRetirementRequest(id, updateDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get retirement request by ID", description = "Retrieves a specific retirement request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement request retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Retirement request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<RetirementRequestResponseDto> getRetirementRequest(@PathVariable String id) {
        log.info("Retrieving retirement request: {}", id);
        
        RetirementRequestResponseDto response = retirementRequestService.getRetirementRequestById(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all retirement requests", description = "Retrieves all retirement requests with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> getAllRetirementRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving all retirement requests");
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.getAllRetirementRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get retirement requests by status", description = "Retrieves retirement requests filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> getRetirementRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving retirement requests by status: {}", status);
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.getRetirementRequestsByStatus(status, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{retirementType}")
    @Operation(summary = "Get retirement requests by type", description = "Retrieves retirement requests filtered by retirement type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid retirement type")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> getRetirementRequestsByType(
            @PathVariable RetirementType retirementType,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving retirement requests by type: {}", retirementType);
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.getRetirementRequestsByType(retirementType, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get retirement requests by employee", description = "Retrieves retirement requests for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> getRetirementRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving retirement requests for employee: {}", employeeId);
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.getRetirementRequestsByEmployee(employeeId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get retirement requests by institution", description = "Retrieves retirement requests for a specific institution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Retirement requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> getRetirementRequestsByInstitution(
            @PathVariable String institutionId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving retirement requests for institution: {}", institutionId);
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.getRetirementRequestsByInstitution(institutionId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search retirement requests", description = "Searches retirement requests by various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> searchRetirementRequests(
            @Parameter(description = "Search term to match against employee name, request number, or retirement information")
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Searching retirement requests with term: {}", searchTerm);
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.searchRetirementRequests(searchTerm, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue retirement requests", description = "Retrieves retirement requests that are overdue based on SLA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue retirement requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<RetirementRequestResponseDto>> getOverdueRetirementRequests(
            @Parameter(description = "Number of days for SLA deadline")
            @RequestParam(defaultValue = "30") int slaDays) {
        
        log.info("Retrieving overdue retirement requests with SLA: {} days", slaDays);
        
        List<RetirementRequest> overdueRequests = retirementRequestService.findOverdueRetirementRequests(slaDays);
        List<RetirementRequestResponseDto> response = overdueRequests.stream()
                .map(request -> retirementRequestService.getRetirementRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming retirements", description = "Retrieves upcoming retirements within specified days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming retirements retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<RetirementRequestResponseDto>> getUpcomingRetirements(
            @Parameter(description = "Number of days to look ahead")
            @RequestParam(defaultValue = "90") int days) {
        
        log.info("Retrieving upcoming retirements within {} days", days);
        
        List<RetirementRequest> upcomingRequests = retirementRequestService.findUpcomingRetirements(days);
        List<RetirementRequestResponseDto> response = upcomingRequests.stream()
                .map(request -> retirementRequestService.getRetirementRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending retirement requests", description = "Retrieves all pending retirement requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending retirement requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<RetirementRequestResponseDto>> getPendingRetirementRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving pending retirement requests");
        
        Page<RetirementRequestResponseDto> response = retirementRequestService.getRetirementRequestsByStatus(RequestStatus.SUBMITTED, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get retirement request statistics", description = "Retrieves statistics about retirement requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Map<String, Object>> getRetirementRequestStatistics() {
        log.info("Retrieving retirement request statistics");
        
        // Get basic statistics
        long totalRequests = retirementRequestService.getAllRetirementRequests(Pageable.unpaged()).getTotalElements();
        long pendingRequests = retirementRequestService.getRetirementRequestsByStatus(RequestStatus.SUBMITTED, Pageable.unpaged()).getTotalElements();
        long approvedRequests = retirementRequestService.getRetirementRequestsByStatus(RequestStatus.APPROVED, Pageable.unpaged()).getTotalElements();
        long rejectedRequests = retirementRequestService.getRetirementRequestsByStatus(RequestStatus.REJECTED, Pageable.unpaged()).getTotalElements();
        
        long compulsoryRetirements = retirementRequestService.getRetirementRequestsByType(RetirementType.COMPULSORY, Pageable.unpaged()).getTotalElements();
        long voluntaryRetirements = retirementRequestService.getRetirementRequestsByType(RetirementType.VOLUNTARY, Pageable.unpaged()).getTotalElements();
        long medicalRetirements = retirementRequestService.getRetirementRequestsByType(RetirementType.ILLNESS, Pageable.unpaged()).getTotalElements();
        
        int overdueCount = retirementRequestService.findOverdueRetirementRequests(30).size();
        int upcomingCount = retirementRequestService.findUpcomingRetirements(90).size();
        
        Map<String, Object> statistics = Map.of(
            "totalRequests", totalRequests,
            "pendingRequests", pendingRequests,
            "approvedRequests", approvedRequests,
            "rejectedRequests", rejectedRequests,
            "compulsoryRetirements", compulsoryRetirements,
            "voluntaryRetirements", voluntaryRetirements,
            "medicalRetirements", medicalRetirements,
            "overdueRequests", overdueCount,
            "upcomingRetirements", upcomingCount
        );
        
        return ResponseEntity.ok(statistics);
    }
}