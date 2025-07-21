package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.resignation.ResignationRequestCreateDto;
import com.zanzibar.csms.dto.resignation.ResignationRequestResponseDto;
import com.zanzibar.csms.dto.resignation.ResignationRequestUpdateDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.ResignationType;
import com.zanzibar.csms.service.ResignationRequestService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resignation-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Resignation Request Management", description = "API for managing resignation requests")
public class ResignationRequestController {

    private final ResignationRequestService resignationRequestService;

    @PostMapping
    @Operation(summary = "Create a new resignation request", description = "Creates a new resignation request for an employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Resignation request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> createResignationRequest(
            @Valid @RequestBody ResignationRequestCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating resignation request for employee: {}", createDto.getEmployeeId());
        
        ResignationRequestResponseDto response = resignationRequestService.createResignationRequest(
            createDto, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a resignation request", description = "Approves a pending resignation request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation request approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> approveResignationRequest(
            @PathVariable String id,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        log.info("Approving resignation request: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.approveResignationRequest(
            id, authentication.getName(), comments
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a resignation request", description = "Rejects a pending resignation request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation request rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> rejectResignationRequest(
            @PathVariable String id,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        
        log.info("Rejecting resignation request: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.rejectResignationRequest(
            id, authentication.getName(), rejectionReason
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a resignation request", description = "Updates a pending resignation request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation request updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state or data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HRO')")
    public ResponseEntity<ResignationRequestResponseDto> updateResignationRequest(
            @PathVariable String id,
            @Valid @RequestBody ResignationRequestUpdateDto updateDto) {
        
        log.info("Updating resignation request: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.updateResignationRequest(id, updateDto);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm-payment")
    @Operation(summary = "Confirm payment for immediate resignation", description = "Confirms payment for 24-hour notice resignation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment confirmed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or payment data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> confirmPayment(
            @PathVariable String id,
            @RequestParam BigDecimal paymentAmount,
            Authentication authentication) {
        
        log.info("Confirming payment for resignation request: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.confirmPayment(
            id, authentication.getName(), paymentAmount
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete-clearance")
    @Operation(summary = "Complete clearance for resignation", description = "Marks clearance as completed for approved resignation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clearance completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> completeClearance(
            @PathVariable String id,
            Authentication authentication) {
        
        log.info("Completing clearance for resignation request: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.completeClearance(
            id, authentication.getName()
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete-handover")
    @Operation(summary = "Complete handover for resignation", description = "Marks handover as completed for approved resignation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Handover completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> completeHandover(
            @PathVariable String id,
            Authentication authentication) {
        
        log.info("Completing handover for resignation request: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.completeHandover(
            id, authentication.getName()
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resignation request by ID", description = "Retrieves a specific resignation request by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation request retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Resignation request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<ResignationRequestResponseDto> getResignationRequestById(@PathVariable String id) {
        
        log.info("Getting resignation request by ID: {}", id);
        
        ResignationRequestResponseDto response = resignationRequestService.getResignationRequestById(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all resignation requests", description = "Retrieves all resignation requests with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ResignationRequestResponseDto>> getAllResignationRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting all resignation requests");
        
        Page<ResignationRequestResponseDto> response = resignationRequestService.getAllResignationRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get resignation requests by status", description = "Retrieves resignation requests filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ResignationRequestResponseDto>> getResignationRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting resignation requests by status: {}", status);
        
        Page<ResignationRequestResponseDto> response = resignationRequestService.getResignationRequestsByStatus(status, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get resignation requests by employee", description = "Retrieves resignation requests for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ResignationRequestResponseDto>> getResignationRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting resignation requests by employee: {}", employeeId);
        
        Page<ResignationRequestResponseDto> response = resignationRequestService.getResignationRequestsByEmployee(employeeId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{resignationType}")
    @Operation(summary = "Get resignation requests by type", description = "Retrieves resignation requests filtered by resignation type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resignation requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ResignationRequestResponseDto>> getResignationRequestsByType(
            @PathVariable ResignationType resignationType,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting resignation requests by type: {}", resignationType);
        
        Page<ResignationRequestResponseDto> response = resignationRequestService.getResignationRequestsByType(resignationType, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search resignation requests", description = "Searches resignation requests by employee name, payroll number, or request number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ResignationRequestResponseDto>> searchResignationRequests(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Searching resignation requests with term: {}", searchTerm);
        
        Page<ResignationRequestResponseDto> response = resignationRequestService.searchResignationRequests(searchTerm, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming resignations", description = "Retrieves resignations with last working date approaching")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming resignations retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<ResignationRequestResponseDto>> getUpcomingResignations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        
        log.info("Getting upcoming resignations for date: {}", targetDate);
        
        List<ResignationRequestResponseDto> response = resignationRequestService.getUpcomingResignations(targetDate);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-payment")
    @Operation(summary = "Get pending payment requests", description = "Retrieves immediate resignation requests with pending payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending payment requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<ResignationRequestResponseDto>> getPendingPaymentRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting pending payment requests");
        
        Page<ResignationRequestResponseDto> response = resignationRequestService.getPendingPaymentRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-clearance")
    @Operation(summary = "Get pending clearance requests", description = "Retrieves approved resignations with pending clearance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending clearance requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<ResignationRequestResponseDto>> getPendingClearanceRequests() {
        
        log.info("Getting pending clearance requests");
        
        List<ResignationRequestResponseDto> response = resignationRequestService.getPendingClearanceRequests();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-handover")
    @Operation(summary = "Get pending handover requests", description = "Retrieves approved resignations with pending handover")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending handover requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<ResignationRequestResponseDto>> getPendingHandoverRequests() {
        
        log.info("Getting pending handover requests");
        
        List<ResignationRequestResponseDto> response = resignationRequestService.getPendingHandoverRequests();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get resignation request statistics", description = "Retrieves statistics about resignation requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Map<String, Object>> getResignationStatistics() {
        
        log.info("Getting resignation request statistics");
        
        Map<String, Object> stats = Map.of(
            "totalSubmitted", resignationRequestService.countResignationRequestsByStatus(RequestStatus.SUBMITTED),
            "totalApproved", resignationRequestService.countResignationRequestsByStatus(RequestStatus.APPROVED),
            "totalRejected", resignationRequestService.countResignationRequestsByStatus(RequestStatus.REJECTED),
            "standardNoticeCount", resignationRequestService.countResignationRequestsByType(ResignationType.THREE_MONTH_NOTICE),
            "immediateNoticeCount", resignationRequestService.countResignationRequestsByType(ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT),
            "totalPaymentAmount", resignationRequestService.getTotalPaymentAmount(),
            "averagePaymentAmount", resignationRequestService.getAveragePaymentAmount()
        );
        
        return ResponseEntity.ok(stats);
    }
}