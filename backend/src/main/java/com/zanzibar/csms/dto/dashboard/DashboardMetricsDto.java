package com.zanzibar.csms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDto {

    private EmployeeMetrics employeeMetrics;
    private RequestMetrics requestMetrics;
    private ComplaintMetrics complaintMetrics;
    private WorkflowMetrics workflowMetrics;
    private InstitutionMetrics institutionMetrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeMetrics {
        private Long totalEmployees;
        private Long activeEmployees;
        private Long inactiveEmployees;
        private Long pendingConfirmations;
        private Long newHiresThisMonth;
        private Map<String, Long> employeesByInstitution;
        private Map<String, Long> employeesByGrade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestMetrics {
        private Long totalRequests;
        private Long pendingRequests;
        private Long approvedRequests;
        private Long rejectedRequests;
        private Long overdueRequests;
        private Map<String, Long> requestsByType;
        private Map<String, Long> requestsByStatus;
        private Double averageProcessingTime;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplaintMetrics {
        private Long totalComplaints;
        private Long pendingComplaints;
        private Long underInvestigation;
        private Long resolvedComplaints;
        private Long overdueComplaints;
        private Map<String, Long> complaintsByType;
        private Map<String, Long> complaintsBySeverity;
        private Double averageResolutionTime;
        private Double averageSatisfactionRating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowMetrics {
        private Long pendingHRMOApprovals;
        private Long pendingHHRMDApprovals;
        private Long pendingDOApprovals;
        private Map<String, Long> workflowStepsByUser;
        private Map<String, Long> bottlenecksByStep;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstitutionMetrics {
        private Long totalInstitutions;
        private Long activeInstitutions;
        private Map<String, Long> employeeCountByInstitution;
        private Map<String, Long> requestCountByInstitution;
        private Map<String, Long> complaintCountByInstitution;
    }
}