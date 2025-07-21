package com.zanzibar.csms.dto;

import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestWorkflowDto {

    private String id;
    private String requestId;
    private Integer stepNumber;
    private String stepName;
    private UserRole requiredRole;
    private String reviewerId;
    private String reviewerName;
    private RequestStatus status;
    private LocalDateTime startDate;
    private LocalDateTime completionDate;
    private String comments;
    private Boolean isCurrentStep;
    private Integer daysInStep;
}