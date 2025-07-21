package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.promotion.PromotionRequestCreateDto;
import com.zanzibar.csms.dto.promotion.PromotionRequestResponseDto;
import com.zanzibar.csms.dto.promotion.PromotionRequestUpdateDto;
import com.zanzibar.csms.entity.enums.PromotionType;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.service.PromotionRequestService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promotion-requests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promotion Request Management", description = "API for managing promotion requests")
public class PromotionRequestController {

    private final PromotionRequestService promotionRequestService;

    @PostMapping
    @Operation(summary = "Create a new promotion request", description = "Creates a new promotion request for an employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Promotion request created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<PromotionRequestResponseDto> createPromotionRequest(
            @Valid @RequestBody PromotionRequestCreateDto createDto,
            Authentication authentication) {
        
        log.info("Creating promotion request for employee: {}", createDto.getEmployeeId());
        
        PromotionRequestResponseDto response = promotionRequestService.createPromotionRequest(
            createDto, authentication.getName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a promotion request", description = "Approves a pending promotion request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion request approved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Promotion request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<PromotionRequestResponseDto> approvePromotionRequest(
            @PathVariable String id,
            @RequestParam(required = false) String comments,
            Authentication authentication) {
        
        log.info("Approving promotion request: {}", id);
        
        PromotionRequestResponseDto response = promotionRequestService.approvePromotionRequest(
            id, authentication.getName(), comments
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a promotion request", description = "Rejects a pending promotion request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion request rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Promotion request not found")
    })
    @PreAuthorize("hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<PromotionRequestResponseDto> rejectPromotionRequest(
            @PathVariable String id,
            @RequestParam String rejectionReason,
            Authentication authentication) {
        
        log.info("Rejecting promotion request: {}", id);
        
        PromotionRequestResponseDto response = promotionRequestService.rejectPromotionRequest(
            id, authentication.getName(), rejectionReason
        );
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a promotion request", description = "Updates a pending promotion request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion request updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request state or data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Promotion request not found")
    })
    @PreAuthorize("hasRole('HRO')")
    public ResponseEntity<PromotionRequestResponseDto> updatePromotionRequest(
            @PathVariable String id,
            @Valid @RequestBody PromotionRequestUpdateDto updateDto) {
        
        log.info("Updating promotion request: {}", id);
        
        PromotionRequestResponseDto response = promotionRequestService.updatePromotionRequest(id, updateDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get promotion request by ID", description = "Retrieves a specific promotion request by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion request retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Promotion request not found")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<PromotionRequestResponseDto> getPromotionRequestById(@PathVariable String id) {
        
        log.info("Getting promotion request by ID: {}", id);
        
        PromotionRequestResponseDto response = promotionRequestService.getPromotionRequestById(id);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all promotion requests", description = "Retrieves all promotion requests with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<PromotionRequestResponseDto>> getAllPromotionRequests(
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting all promotion requests");
        
        Page<PromotionRequestResponseDto> response = promotionRequestService.getAllPromotionRequests(pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get promotion requests by status", description = "Retrieves promotion requests filtered by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<PromotionRequestResponseDto>> getPromotionRequestsByStatus(
            @PathVariable RequestStatus status,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting promotion requests by status: {}", status);
        
        Page<PromotionRequestResponseDto> response = promotionRequestService.getPromotionRequestsByStatus(status, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Get promotion requests by employee", description = "Retrieves promotion requests for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<PromotionRequestResponseDto>> getPromotionRequestsByEmployee(
            @PathVariable String employeeId,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting promotion requests by employee: {}", employeeId);
        
        Page<PromotionRequestResponseDto> response = promotionRequestService.getPromotionRequestsByEmployee(employeeId, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{promotionType}")
    @Operation(summary = "Get promotion requests by type", description = "Retrieves promotion requests filtered by promotion type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion requests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<PromotionRequestResponseDto>> getPromotionRequestsByType(
            @PathVariable PromotionType promotionType,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Getting promotion requests by type: {}", promotionType);
        
        Page<PromotionRequestResponseDto> response = promotionRequestService.getPromotionRequestsByType(promotionType, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search promotion requests", description = "Searches promotion requests by employee name, payroll number, or position")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Page<PromotionRequestResponseDto>> searchPromotionRequests(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20, sort = "submissionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.info("Searching promotion requests with term: {}", searchTerm);
        
        Page<PromotionRequestResponseDto> response = promotionRequestService.searchPromotionRequests(searchTerm, pageable);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming promotions", description = "Retrieves promotions with effective date approaching")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming promotions retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<PromotionRequestResponseDto>> getUpcomingPromotions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        
        log.info("Getting upcoming promotions for date: {}", targetDate);
        
        List<PromotionRequestResponseDto> response = promotionRequestService.getUpcomingPromotions(targetDate);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/history")
    @Operation(summary = "Get employee promotion history", description = "Retrieves promotion history for a specific employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promotion history retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<List<PromotionRequestResponseDto>> getEmployeePromotionHistory(
            @PathVariable String employeeId) {
        
        log.info("Getting promotion history for employee: {}", employeeId);
        
        List<PromotionRequestResponseDto> response = promotionRequestService.getEmployeePromotionHistory(employeeId);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get promotion request statistics", description = "Retrieves statistics about promotion requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasRole('HRO') or hasRole('HHRMD') or hasRole('HRMO')")
    public ResponseEntity<Map<String, Object>> getPromotionStatistics() {
        
        log.info("Getting promotion request statistics");
        
        Map<String, Object> stats = Map.of(
            "totalSubmitted", promotionRequestService.countPromotionRequestsByStatus(RequestStatus.SUBMITTED),
            "totalApproved", promotionRequestService.countPromotionRequestsByStatus(RequestStatus.APPROVED),
            "totalRejected", promotionRequestService.countPromotionRequestsByStatus(RequestStatus.REJECTED),
            "educationalPromotions", promotionRequestService.countPromotionRequestsByType(PromotionType.EDUCATIONAL),
            "performancePromotions", promotionRequestService.countPromotionRequestsByType(PromotionType.PERFORMANCE),
            "averageSalaryIncrease", promotionRequestService.getAverageSalaryIncrease(),
            "totalSalaryIncrease", promotionRequestService.getTotalSalaryIncrease()
        );
        
        return ResponseEntity.ok(stats);
    }
}