package com.zanzibar.csms.controller;

import com.zanzibar.csms.dto.dashboard.DashboardMetricsDto;
import com.zanzibar.csms.dto.dashboard.UserActivityDto;
import com.zanzibar.csms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard and analytics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    @Operation(summary = "Get dashboard metrics based on user role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DashboardMetricsDto> getDashboardMetrics(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/activity")
    @Operation(summary = "Get user activity and notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activity retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserActivityDto> getUserActivity(
            @Parameter(description = "Number of items to return") @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        UserActivityDto activity = dashboardService.getUserActivity(authentication.getName(), limit);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/admin/metrics")
    @Operation(summary = "Get comprehensive system metrics (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "System metrics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardMetricsDto> getAdminMetrics() {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics("admin");
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/commissioner/overview")
    @Operation(summary = "Get commissioner overview dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commissioner overview retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('COMMISSIONER')")
    public ResponseEntity<DashboardMetricsDto> getCommissionerOverview(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/hhrmd/pending")
    @Operation(summary = "Get HHRMD pending approvals dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "HHRMD dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('HEAD_HR')")
    public ResponseEntity<DashboardMetricsDto> getHHRMDDashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/hrmo/workload")
    @Operation(summary = "Get HRMO workload dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "HRMO dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('HR_MANAGER')")
    public ResponseEntity<DashboardMetricsDto> getHRMODashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/do/complaints")
    @Operation(summary = "Get Disciplinary Officer complaints dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "DO dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('DISCIPLINARY_OFFICER')")
    public ResponseEntity<DashboardMetricsDto> getDODashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/hro/institution")
    @Operation(summary = "Get HRO institution dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "HRO dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('HR_OFFICER')")
    public ResponseEntity<DashboardMetricsDto> getHRODashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/hrrp/overview")
    @Operation(summary = "Get HRRP institutional overview")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "HRRP dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('HR_RESPONSIBLE')")
    public ResponseEntity<DashboardMetricsDto> getHRRPDashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/planning/analytics")
    @Operation(summary = "Get Planning Officer analytics dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planning Officer dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('PLANNING_OFFICER')")
    public ResponseEntity<DashboardMetricsDto> getPlanningOfficerDashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/employee/personal")
    @Operation(summary = "Get employee personal dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee dashboard retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<DashboardMetricsDto> getEmployeeDashboard(Authentication authentication) {
        DashboardMetricsDto metrics = dashboardService.getDashboardMetrics(authentication.getName());
        return ResponseEntity.ok(metrics);
    }
}