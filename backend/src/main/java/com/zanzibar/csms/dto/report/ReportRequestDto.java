package com.zanzibar.csms.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    
    @NotBlank(message = "Report type is required")
    private String reportType;
    
    @NotBlank(message = "Report name is required")
    private String reportName;
    
    private String description;
    
    @NotNull(message = "Period start date is required")
    private LocalDateTime periodStart;
    
    @NotNull(message = "Period end date is required")
    private LocalDateTime periodEnd;
    
    @NotBlank(message = "Output format is required")
    private String format; // PDF, EXCEL, CSV
    
    private String institutionId;
    private List<String> institutionIds;
    private String departmentId;
    private List<String> departmentIds;
    private String employeeId;
    private List<String> employeeIds;
    private String requestType;
    private List<String> requestTypes;
    private String requestStatus;
    private List<String> requestStatuses;
    private String complaintType;
    private List<String> complaintTypes;
    private String complaintStatus;
    private List<String> complaintStatuses;
    private String priority;
    private List<String> priorities;
    private String userRole;
    private List<String> userRoles;
    private String userId;
    private List<String> userIds;
    
    // Report-specific filters
    private Map<String, Object> filters;
    
    // Grouping and sorting
    private List<String> groupBy;
    private List<SortCriteria> sortBy;
    
    // Pagination
    private Integer page;
    private Integer size;
    private Boolean includeCharts;
    private Boolean includeSummary;
    
    // Scheduling
    private Boolean isScheduled;
    private String cronExpression;
    private List<String> emailRecipients;
    private Boolean autoEmail;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortCriteria {
        private String field;
        private String direction; // ASC, DESC
    }
}