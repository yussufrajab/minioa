package com.zanzibar.csms.dto.serviceextension;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceExtensionRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;

    // Service Extension specific fields
    private Integer extensionDurationYears;
    private String extensionDurationDisplay;

    // Document information
    private List<ServiceExtensionDocumentDto> documents;
    private boolean hasRequiredDocuments;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isPending() {
        return status != null && status.isPending();
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    public String getStatusDisplay() {
        return status != null ? status.getDescription() : "Unknown";
    }
}