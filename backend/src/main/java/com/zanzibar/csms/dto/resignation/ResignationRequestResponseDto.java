package com.zanzibar.csms.dto.resignation;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.ResignationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResignationRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;

    // Resignation specific fields
    private ResignationType resignationType;
    private String resignationTypeDisplay;
    private LocalDate resignationDate;
    private LocalDate lastWorkingDate;
    private String reason;
    private BigDecimal paymentAmount;
    private Boolean paymentConfirmed;
    private Boolean clearanceCompleted;
    private Boolean handoverCompleted;
    private int noticePeriodDays;

    // Document information
    private List<ResignationDocumentDto> documents;
    private boolean hasRequiredDocuments;
    private List<String> missingDocuments;
    private boolean eligibleForProcessing;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isPending() {
        return status != null && status.isPending();
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    public boolean isStandardNotice() {
        return resignationType == ResignationType.THREE_MONTH_NOTICE;
    }

    public boolean isImmediateWithPayment() {
        return resignationType == ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT;
    }

    public String getStatusDisplay() {
        return status != null ? status.getDescription() : "Unknown";
    }

    public boolean requiresPayment() {
        return resignationType != null && resignationType.requiresPayment();
    }

    public boolean isPaymentConfirmed() {
        return Boolean.TRUE.equals(paymentConfirmed);
    }

    public boolean isClearanceCompleted() {
        return Boolean.TRUE.equals(clearanceCompleted);
    }

    public boolean isHandoverCompleted() {
        return Boolean.TRUE.equals(handoverCompleted);
    }
}