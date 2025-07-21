package com.zanzibar.csms.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StandardReportDto {
    
    // Employee Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeReportDto {
        private String employeeId;
        private String payrollNumber;
        private String zanzibarId;
        private String zssfNumber;
        private String fullName;
        private String rank;
        private String department;
        private String institution;
        private String employmentStatus;
        private LocalDateTime employmentDate;
        private LocalDateTime confirmationDate;
        private String contractType;
        private String appointmentType;
        private Integer yearsOfService;
        private List<String> certificates;
        private List<String> documents;
    }
    
    // Request Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestReportDto {
        private String requestId;
        private String requestNumber;
        private String requestType;
        private String employeeName;
        private String employeeId;
        private String institution;
        private String status;
        private LocalDateTime submissionDate;
        private LocalDateTime approvalDate;
        private String approvedBy;
        private String submittedBy;
        private Long processingDays;
        private String priority;
        private String comments;
        private List<String> documents;
    }
    
    // Complaint Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplaintReportDto {
        private String complaintId;
        private String complaintNumber;
        private String complaintType;
        private String severity;
        private String complainantName;
        private String complainantId;
        private String respondentName;
        private String institution;
        private String status;
        private LocalDateTime submissionDate;
        private LocalDateTime resolutionDate;
        private String resolvedBy;
        private Long resolutionDays;
        private String resolution;
        private Double satisfactionRating;
        private String category;
    }
    
    // Institutional Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstitutionalReportDto {
        private String institutionId;
        private String institutionName;
        private String voteNumber;
        private String email;
        private String address;
        private String telephone;
        private Boolean isActive;
        private Long totalEmployees;
        private Long activeEmployees;
        private Long confirmedEmployees;
        private Long unconfirmedEmployees;
        private Long pendingRequests;
        private Long approvedRequests;
        private Long rejectedRequests;
        private Long totalComplaints;
        private Long resolvedComplaints;
        private Map<String, Long> requestsByType;
        private Map<String, Long> complaintsByType;
    }
    
    // Workflow Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowReportDto {
        private String workflowId;
        private String requestId;
        private String requestType;
        private String currentStep;
        private String currentReviewer;
        private String status;
        private LocalDateTime stepStartDate;
        private LocalDateTime stepCompletedDate;
        private Long stepDuration;
        private String action;
        private String comments;
        private String nextStep;
        private String nextReviewer;
        private Boolean isOverdue;
        private LocalDateTime dueDate;
    }
    
    // Performance Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceReportDto {
        private String metric;
        private String period;
        private Double value;
        private String unit;
        private String category;
        private String subcategory;
        private Double targetValue;
        private Double variance;
        private String trend;
        private String institution;
        private String department;
        private LocalDateTime measurementDate;
        private Map<String, Object> breakdown;
    }
    
    // Audit Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditReportDto {
        private String auditId;
        private String userId;
        private String username;
        private String action;
        private String entityType;
        private String entityId;
        private String entityName;
        private LocalDateTime timestamp;
        private String ipAddress;
        private Boolean success;
        private String beforeValue;
        private String afterValue;
        private String reason;
        private String sessionId;
        private String userAgent;
        private String location;
    }
    
    // SLA Reports
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SLAReportDto {
        private String requestId;
        private String requestType;
        private String institution;
        private LocalDateTime submissionDate;
        private LocalDateTime dueDate;
        private LocalDateTime completionDate;
        private Long slaHours;
        private Long actualHours;
        private Long variance;
        private String status;
        private Boolean withinSLA;
        private String breachReason;
        private String currentReviewer;
        private String escalationLevel;
        private Double compliancePercentage;
    }
}