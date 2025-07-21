package com.zanzibar.csms.controller;

import com.zanzibar.csms.service.ComplaintAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints/analytics")
@RequiredArgsConstructor
@Tag(name = "Complaint Analytics", description = "Endpoints for complaint analytics and reporting")
public class ComplaintAnalyticsController {

    private final ComplaintAnalyticsService complaintAnalyticsService;

    @GetMapping
    @Operation(summary = "Get comprehensive complaint analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_MANAGER', 'HEAD_HR', 'COMMISSIONER')")
    public ResponseEntity<Map<String, Object>> getComplaintAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> analytics = complaintAnalyticsService.getComplaintAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics for complaints")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('HR_OFFICER', 'HR_MANAGER', 'HEAD_HR', 'COMMISSIONER')")
    public ResponseEntity<Map<String, Long>> getDashboardMetrics() {
        Map<String, Long> metrics = complaintAnalyticsService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }
}