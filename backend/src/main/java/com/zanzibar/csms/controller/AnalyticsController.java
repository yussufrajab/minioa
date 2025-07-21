package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.dashboard.AnalyticsReportDto;
import com.zanzibar.csms.service.AnalyticsService;
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
@RequestMapping("/api/reports/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Real-time analytics and insights endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    @Operation(summary = "Get analytics overview")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics overview retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<AnalyticsReportDto> getAnalyticsOverview(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        AnalyticsReportDto analytics = analyticsService.getAnalyticsOverview(
            periodStart, periodEnd, institutionId, authentication.getName());
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/trends")
    @Operation(summary = "Get analytics trends")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics trends retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<List<AnalyticsReportDto.TrendData>> getAnalyticsTrends(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Trend category") @RequestParam String category,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        List<AnalyticsReportDto.TrendData> trends = analyticsService.getAnalyticsTrends(
            periodStart, periodEnd, category, institutionId, authentication.getName());
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/performance")
    @Operation(summary = "Get system performance metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Performance metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<AnalyticsReportDto.SystemPerformance> getSystemPerformance(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        AnalyticsReportDto.SystemPerformance performance = analyticsService.getSystemPerformance(
            periodStart, periodEnd, institutionId, authentication.getName());
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/comparisons")
    @Operation(summary = "Get comparison analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comparison analytics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<List<AnalyticsReportDto.ComparisonData>> getComparisonAnalytics(
            @Parameter(description = "Current period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime currentPeriodStart,
            @Parameter(description = "Current period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime currentPeriodEnd,
            @Parameter(description = "Previous period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime previousPeriodStart,
            @Parameter(description = "Previous period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime previousPeriodEnd,
            @Parameter(description = "Comparison category") @RequestParam String category,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        List<AnalyticsReportDto.ComparisonData> comparisons = analyticsService.getComparisonAnalytics(
            currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd, 
            category, institutionId, authentication.getName());
        return ResponseEntity.ok(comparisons);
    }

    @GetMapping("/kpis")
    @Operation(summary = "Get key performance indicators")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "KPIs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<Map<String, Object>> getKPIs(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        Map<String, Object> kpis = analyticsService.getKPIs(
            periodStart, periodEnd, institutionId, authentication.getName());
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/bottlenecks")
    @Operation(summary = "Get workflow bottlenecks analysis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bottlenecks analysis retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'HHRMD', 'HRMO', 'DO', 'PO')")
    public ResponseEntity<Map<String, Object>> getBottlenecksAnalysis(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        Map<String, Object> bottlenecks = analyticsService.getBottlenecksAnalysis(
            periodStart, periodEnd, institutionId, authentication.getName());
        return ResponseEntity.ok(bottlenecks);
    }

    @GetMapping("/predictions")
    @Operation(summary = "Get predictive analytics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Predictive analytics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'PO')")
    public ResponseEntity<Map<String, Object>> getPredictiveAnalytics(
            @Parameter(description = "Prediction type") @RequestParam String predictionType,
            @Parameter(description = "Prediction horizon (days)") @RequestParam(defaultValue = "30") Integer horizonDays,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        Map<String, Object> predictions = analyticsService.getPredictiveAnalytics(
            predictionType, horizonDays, institutionId, authentication.getName());
        return ResponseEntity.ok(predictions);
    }

    @GetMapping("/export")
    @Operation(summary = "Export analytics data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics data exported successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CSCS', 'PO')")
    public ResponseEntity<String> exportAnalyticsData(
            @Parameter(description = "Period start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodStart,
            @Parameter(description = "Period end date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime periodEnd,
            @Parameter(description = "Export format") @RequestParam(defaultValue = "CSV") String format,
            @Parameter(description = "Data categories to export") @RequestParam List<String> categories,
            @Parameter(description = "Institution ID filter") @RequestParam(required = false) String institutionId,
            Authentication authentication) {
        
        String exportUrl = analyticsService.exportAnalyticsData(
            periodStart, periodEnd, format, categories, institutionId, authentication.getName());
        return ResponseEntity.ok(exportUrl);
    }
}