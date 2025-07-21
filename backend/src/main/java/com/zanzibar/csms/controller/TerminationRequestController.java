package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.termination.TerminationRequestCreateDto;
import com.zanzibar.csms.dto.termination.TerminationRequestResponseDto;
import com.zanzibar.csms.dto.termination.TerminationRequestUpdateDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.TerminationScenario;
import com.zanzibar.csms.service.TerminationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/termination-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Termination Requests", description = "Termination request management for unconfirmed employees")
public class TerminationRequestController {

    private final TerminationRequestService terminationRequestService;

    @PostMapping
    @Operation(summary = "Create a new termination request", description = "Create a termination request for an unconfirmed employee")
    @ApiResponse(responseCode = "201", description = "Termination request created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<TerminationRequestResponseDto>> createTerminationRequest(
            @Valid @RequestBody TerminationRequestCreateDto createDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String submittedById = auth.getName(); // Assuming the authentication name is the user ID

        TerminationRequestResponseDto response = terminationRequestService.createTerminationRequest(createDto, submittedById);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            com.zanzibar.csms.dto.ApiResponse.<TerminationRequestResponseDto>builder()
                .status("success")
                .message("Termination request created successfully")
                .data(response)
                .build()
        );
    }

    @PutMapping("/{requestId}/approve")
    @Operation(summary = "Approve a termination request", description = "Approve a pending termination request")
    @ApiResponse(responseCode = "200", description = "Termination request approved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request status")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Termination request not found")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<TerminationRequestResponseDto>> approveTerminationRequest(
            @PathVariable String requestId,
            @RequestBody(required = false) @Parameter(description = "Approval comments") String comments) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String approverId = auth.getName();

        TerminationRequestResponseDto response = terminationRequestService.approveTerminationRequest(requestId, approverId, comments);

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<TerminationRequestResponseDto>builder()
                .status("success")
                .message("Termination request approved successfully")
                .data(response)
                .build()
        );
    }

    @PutMapping("/{requestId}/reject")
    @Operation(summary = "Reject a termination request", description = "Reject a pending termination request")
    @ApiResponse(responseCode = "200", description = "Termination request rejected successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request status")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Termination request not found")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<TerminationRequestResponseDto>> rejectTerminationRequest(
            @PathVariable String requestId,
            @RequestBody @Parameter(description = "Rejection reason", required = true) String rejectionReason) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String approverId = auth.getName();

        TerminationRequestResponseDto response = terminationRequestService.rejectTerminationRequest(requestId, approverId, rejectionReason);

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<TerminationRequestResponseDto>builder()
                .status("success")
                .message("Termination request rejected successfully")
                .data(response)
                .build()
        );
    }

    @PutMapping("/{requestId}")
    @Operation(summary = "Update a termination request", description = "Update a pending termination request")
    @ApiResponse(responseCode = "200", description = "Termination request updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Termination request not found")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<TerminationRequestResponseDto>> updateTerminationRequest(
            @PathVariable String requestId,
            @Valid @RequestBody TerminationRequestUpdateDto updateDto) {

        TerminationRequestResponseDto response = terminationRequestService.updateTerminationRequest(requestId, updateDto);

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<TerminationRequestResponseDto>builder()
                .status("success")
                .message("Termination request updated successfully")
                .data(response)
                .build()
        );
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get termination request by ID", description = "Retrieve a specific termination request")
    @ApiResponse(responseCode = "200", description = "Termination request found")
    @ApiResponse(responseCode = "404", description = "Termination request not found")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<TerminationRequestResponseDto>> getTerminationRequest(
            @PathVariable String requestId) {

        TerminationRequestResponseDto response = terminationRequestService.getTerminationRequestById(requestId);

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<TerminationRequestResponseDto>builder()
                .status("success")
                .message("Termination request retrieved successfully")
                .data(response)
                .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get all termination requests", description = "Retrieve termination requests with pagination and filtering")
    @ApiResponse(responseCode = "200", description = "Termination requests retrieved successfully")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<Page<TerminationRequestResponseDto>>> getAllTerminationRequests(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "submissionDate") @Parameter(description = "Sort by field") String sortBy,
            @RequestParam(defaultValue = "desc") @Parameter(description = "Sort direction") String sortDir,
            @RequestParam(required = false) @Parameter(description = "Filter by status") RequestStatus status,
            @RequestParam(required = false) @Parameter(description = "Filter by scenario") TerminationScenario scenario,
            @RequestParam(required = false) @Parameter(description = "Filter by institution") String institutionId,
            @RequestParam(required = false) @Parameter(description = "Search term") String search) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TerminationRequestResponseDto> response;

        if (search != null && !search.trim().isEmpty()) {
            response = terminationRequestService.searchTerminationRequests(search.trim(), pageable);
        } else if (status != null) {
            response = terminationRequestService.getTerminationRequestsByStatus(status, pageable);
        } else if (institutionId != null) {
            response = terminationRequestService.getTerminationRequestsByInstitution(institutionId, pageable);
        } else {
            response = terminationRequestService.getAllTerminationRequests(pageable);
        }

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<Page<TerminationRequestResponseDto>>builder()
                .status("success")
                .message("Termination requests retrieved successfully")
                .data(response)
                .build()
        );
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get termination requests by employee", description = "Retrieve all termination requests for a specific employee")
    @ApiResponse(responseCode = "200", description = "Termination requests retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<Page<TerminationRequestResponseDto>>> getTerminationRequestsByEmployee(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "submissionDate") @Parameter(description = "Sort by field") String sortBy,
            @RequestParam(defaultValue = "desc") @Parameter(description = "Sort direction") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TerminationRequestResponseDto> response = terminationRequestService.getTerminationRequestsByEmployee(employeeId, pageable);

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<Page<TerminationRequestResponseDto>>builder()
                .status("success")
                .message("Termination requests retrieved successfully")
                .data(response)
                .build()
        );
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending termination requests", description = "Retrieve all pending termination requests")
    @ApiResponse(responseCode = "200", description = "Pending termination requests retrieved successfully")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<Page<TerminationRequestResponseDto>>> getPendingTerminationRequests(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "submissionDate") @Parameter(description = "Sort by field") String sortBy,
            @RequestParam(defaultValue = "asc") @Parameter(description = "Sort direction") String sortDir) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<TerminationRequestResponseDto> response = terminationRequestService.getTerminationRequestsByStatus(RequestStatus.SUBMITTED, pageable);

        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.<Page<TerminationRequestResponseDto>>builder()
                .status("success")
                .message("Pending termination requests retrieved successfully")
                .data(response)
                .build()
        );
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get termination request statistics", description = "Retrieve statistics about termination requests")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public ResponseEntity<com.zanzibar.csms.dto.ApiResponse<Object>> getTerminationRequestStatistics() {
        // This would typically call a statistics service
        // For now, returning a placeholder
        return ResponseEntity.ok(
            com.zanzibar.csms.dto.ApiResponse.builder()
                .status("success")
                .message("Termination request statistics retrieved successfully")
                .data("Statistics endpoint - implementation pending")
                .build()
        );
    }
}