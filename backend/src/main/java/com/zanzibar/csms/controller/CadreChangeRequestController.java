package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.cadre.CadreChangeRequestCreateDto;
import com.zanzibar.csms.dto.cadre.CadreChangeRequestResponseDto;
import com.zanzibar.csms.dto.cadre.CadreChangeRequestUpdateDto;
import com.zanzibar.csms.entity.CadreChangeRequest;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.service.CadreChangeRequestService;
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
@RequestMapping("/api/cadre-change-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cadre Change Request Management", description = "API for managing cadre change requests")
public class CadreChangeRequestController {

    private final CadreChangeRequestService cadreChangeRequestService;

    @PostMapping
    @Operation(summary = "Create a new cadre change request", description = "Creates a new cadre change request for an employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cadre change request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<CadreChangeRequestResponseDto> createCadreChangeRequest(
            @Valid @RequestBody CadreChangeRequestCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating cadre change request for employee: {}", createDto.getEmployeeId());
        
        CadreChangeRequestResponseDto response = cadreChangeRequestService.createCadreChangeRequest(
            createDto, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a cadre change request", description = "Approves a pending cadre change request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change request approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Cadre change request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<CadreChangeRequestResponseDto> approveCadreChangeRequest(
            @PathVariable String id,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        log.info("Approving cadre change request: {}", id);
        
        CadreChangeRequestResponseDto response = cadreChangeRequestService.approveCadreChangeRequest(
            id, authentication.getName(), comments
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a cadre change request", description = "Rejects a pending cadre change request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change request rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Cadre change request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<CadreChangeRequestResponseDto> rejectCadreChangeRequest(
            @PathVariable String id,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        
        log.info("Rejecting cadre change request: {}", id);
        
        CadreChangeRequestResponseDto response = cadreChangeRequestService.rejectCadreChangeRequest(
            id, authentication.getName(), rejectionReason
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a cadre change request", description = "Updates a pending cadre change request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change request updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Cadre change request not found")
    })
    @PreAuthorize("hasRole('HRO')")
    public ResponseEntity<CadreChangeRequestResponseDto> updateCadreChangeRequest(
            @PathVariable String id,
            @Valid @RequestBody CadreChangeRequestUpdateDto updateDto) {
        
        log.info("Updating cadre change request: {}", id);
        
        CadreChangeRequestResponseDto response = cadreChangeRequestService.updateCadreChangeRequest(id, updateDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cadre change request by ID", description = "Retrieves a specific cadre change request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change request retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Cadre change request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<CadreChangeRequestResponseDto> getCadreChangeRequest(@PathVariable String id) {
        log.info("Retrieving cadre change request: {}", id);
        
        CadreChangeRequestResponseDto response = cadreChangeRequestService.getCadreChangeRequestById(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all cadre change requests", description = "Retrieves all cadre change requests with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<CadreChangeRequestResponseDto>> getAllCadreChangeRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving all cadre change requests");
        
        Page<CadreChangeRequestResponseDto> response = cadreChangeRequestService.getAllCadreChangeRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get cadre change requests by status", description = "Retrieves cadre change requests filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change requests retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<CadreChangeRequestResponseDto>> getCadreChangeRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving cadre change requests by status: {}", status);
        
        Page<CadreChangeRequestResponseDto> response = cadreChangeRequestService.getCadreChangeRequestsByStatus(status, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get cadre change requests by employee", description = "Retrieves cadre change requests for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<CadreChangeRequestResponseDto>> getCadreChangeRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving cadre change requests for employee: {}", employeeId);
        
        Page<CadreChangeRequestResponseDto> response = cadreChangeRequestService.getCadreChangeRequestsByEmployee(employeeId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Get cadre change requests by institution", description = "Retrieves cadre change requests for a specific institution")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadre change requests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<CadreChangeRequestResponseDto>> getCadreChangeRequestsByInstitution(
            @PathVariable String institutionId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving cadre change requests for institution: {}", institutionId);
        
        Page<CadreChangeRequestResponseDto> response = cadreChangeRequestService.getCadreChangeRequestsByInstitution(institutionId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search cadre change requests", description = "Searches cadre change requests by various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<CadreChangeRequestResponseDto>> searchCadreChangeRequests(
            @Parameter(description = "Search term to match against employee name, request number, or cadre information")
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Searching cadre change requests with term: {}", searchTerm);
        
        Page<CadreChangeRequestResponseDto> response = cadreChangeRequestService.searchCadreChangeRequests(searchTerm, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue cadre change requests", description = "Retrieves cadre change requests that are overdue based on SLA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue cadre change requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<CadreChangeRequestResponseDto>> getOverdueCadreChangeRequests(
            @Parameter(description = "Number of days for SLA deadline")
            @RequestParam(defaultValue = "30") int slaDays) {
        
        log.info("Retrieving overdue cadre change requests with SLA: {} days", slaDays);
        
        List<CadreChangeRequest> overdueRequests = cadreChangeRequestService.findOverdueCadreChangeRequests(slaDays);
        List<CadreChangeRequestResponseDto> response = overdueRequests.stream()
                .map(request -> cadreChangeRequestService.getCadreChangeRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tcu-verification")
    @Operation(summary = "Get requests requiring TCU verification", description = "Retrieves cadre change requests that require TCU verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requests requiring TCU verification retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<CadreChangeRequestResponseDto>> getRequestsRequiringTcuVerification() {
        log.info("Retrieving requests requiring TCU verification");
        
        List<CadreChangeRequest> tcuRequests = cadreChangeRequestService.findRequestsRequiringTcuVerification();
        List<CadreChangeRequestResponseDto> response = tcuRequests.stream()
                .map(request -> cadreChangeRequestService.getCadreChangeRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/implementation")
    @Operation(summary = "Get requests ready for implementation", description = "Retrieves approved cadre change requests ready for implementation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Requests ready for implementation retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<CadreChangeRequestResponseDto>> getRequestsForImplementation() {
        log.info("Retrieving requests ready for implementation");
        
        List<CadreChangeRequest> implementationRequests = cadreChangeRequestService.findRequestsForImplementation();
        List<CadreChangeRequestResponseDto> response = implementationRequests.stream()
                .map(request -> cadreChangeRequestService.getCadreChangeRequestById(request.getId()))
                .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get cadre change request statistics", description = "Retrieves statistics about cadre change requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Map<String, Object>> getCadreChangeRequestStatistics() {
        log.info("Retrieving cadre change request statistics");
        
        // Get basic statistics
        long totalRequests = cadreChangeRequestService.getAllCadreChangeRequests(Pageable.unpaged()).getTotalElements();
        long pendingRequests = cadreChangeRequestService.getCadreChangeRequestsByStatus(RequestStatus.SUBMITTED, Pageable.unpaged()).getTotalElements();
        long approvedRequests = cadreChangeRequestService.getCadreChangeRequestsByStatus(RequestStatus.APPROVED, Pageable.unpaged()).getTotalElements();
        long rejectedRequests = cadreChangeRequestService.getCadreChangeRequestsByStatus(RequestStatus.REJECTED, Pageable.unpaged()).getTotalElements();
        
        int overdueCount = cadreChangeRequestService.findOverdueCadreChangeRequests(30).size();
        int tcuVerificationCount = cadreChangeRequestService.findRequestsRequiringTcuVerification().size();
        int implementationCount = cadreChangeRequestService.findRequestsForImplementation().size();
        
        Map<String, Object> statistics = Map.of(
            "totalRequests", totalRequests,
            "pendingRequests", pendingRequests,
            "approvedRequests", approvedRequests,
            "rejectedRequests", rejectedRequests,
            "overdueRequests", overdueCount,
            "tcuVerificationRequired", tcuVerificationCount,
            "readyForImplementation", implementationCount
        );
        
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending cadre change requests", description = "Retrieves all pending cadre change requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending cadre change requests retrieved successfully")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<CadreChangeRequestResponseDto>> getPendingCadreChangeRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Retrieving pending cadre change requests");
        
        Page<CadreChangeRequestResponseDto> response = cadreChangeRequestService.getCadreChangeRequestsByStatus(RequestStatus.SUBMITTED, pageable);
        
        return ResponseEntity.ok(response);
    }
}