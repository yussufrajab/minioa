package com.zanzibar.csms.dto;

import com.zanzibar.csms.entity.enums.Priority;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private String id;
    private String requestNumber;
    private String employeeId;
    private String employeeName;
    private String submittedById;
    private String submittedByName;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private RequestType requestType;
    private String approverId;
    private String approverName;
    private LocalDateTime approvalDate;
    private String comments;
    private String rejectionReason;
    private Priority priority;
    private LocalDateTime dueDate;
    private String description;
    private String currentStage;
    private String currentReviewerId;
    private String currentReviewerName;
    private String institutionId;
    private String institutionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RequestDocumentDto> documents;
    private List<RequestWorkflowDto> workflowSteps;
}