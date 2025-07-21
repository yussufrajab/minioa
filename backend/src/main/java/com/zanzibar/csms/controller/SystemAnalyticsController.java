package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.dashboard.AnalyticsReportDto;
import com.zanzibar.csms.service.SystemAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "System Analytics", description = "System-wide analytics and reporting endpoints")
public class SystemAnalyticsController {

    private final SystemAnalyticsService systemAnalyticsService;

    @GetMapping("/system")
    @Operation(summary = "Generate system-wide analytics report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "System analytics generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER', 'PLANNING_OFFICER', 'HEAD_HR')")
    public ResponseEntity<AnalyticsReportDto> getSystemAnalytics(
            @Parameter(description = "Start date for analytics period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for analytics period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        
        AnalyticsReportDto report = systemAnalyticsService.generateSystemAnalytics(
            startDate, endDate, authentication.getName());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Generate institutional analytics report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Institutional analytics generated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER', 'PLANNING_OFFICER', 'HEAD_HR', 'HR_MANAGER', 'HR_RESPONSIBLE')")
    public ResponseEntity<AnalyticsReportDto> getInstitutionalAnalytics(
            @PathVariable String institutionId,
            @Parameter(description = "Start date for analytics period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for analytics period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        
        AnalyticsReportDto report = systemAnalyticsService.generateInstitutionalAnalytics(
            institutionId, startDate, endDate, authentication.getName());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/health")
    @Operation(summary = "Get system health metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "System health metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER')")
    public ResponseEntity<Map<String, Object>> getSystemHealthMetrics() {
        Map<String, Object> health = systemAnalyticsService.getSystemHealthMetrics();
        return ResponseEntity.ok(health);
    }

    @GetMapping("/bottlenecks")
    @Operation(summary = "Get workflow bottleneck analysis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bottleneck analysis retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER', 'PLANNING_OFFICER', 'HEAD_HR')")
    public ResponseEntity<List<Map<String, Object>>> getWorkflowBottlenecks() {
        List<Map<String, Object>> bottlenecks = systemAnalyticsService.getWorkflowBottlenecks();
        return ResponseEntity.ok(bottlenecks);
    }

    @GetMapping("/productivity")
    @Operation(summary = "Get user productivity metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productivity metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER', 'PLANNING_OFFICER', 'HEAD_HR')")
    public ResponseEntity<Map<String, Object>> getUserProductivityMetrics(
            @Parameter(description = "Start date for productivity analysis") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for productivity analysis") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> productivity = systemAnalyticsService.getUserProductivityMetrics(startDate, endDate);
        return ResponseEntity.ok(productivity);
    }

    @GetMapping("/export/system")
    @Operation(summary = "Export system analytics report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report exported successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER', 'PLANNING_OFFICER')")
    public ResponseEntity<AnalyticsReportDto> exportSystemAnalytics(
            @Parameter(description = "Start date for export period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for export period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Export format") 
            @RequestParam(defaultValue = "JSON") String format,
            Authentication authentication) {
        
        AnalyticsReportDto report = systemAnalyticsService.generateSystemAnalytics(
            startDate, endDate, authentication.getName());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/export/institution/{institutionId}")
    @Operation(summary = "Export institutional analytics report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Institutional report exported successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Institution not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'COMMISSIONER', 'PLANNING_OFFICER', 'HR_RESPONSIBLE')")
    public ResponseEntity<AnalyticsReportDto> exportInstitutionalAnalytics(
            @PathVariable String institutionId,
            @Parameter(description = "Start date for export period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for export period") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Export format") 
            @RequestParam(defaultValue = "JSON") String format,
            Authentication authentication) {
        
        AnalyticsReportDto report = systemAnalyticsService.generateInstitutionalAnalytics(
            institutionId, startDate, endDate, authentication.getName());
        return ResponseEntity.ok(report);
    }
}