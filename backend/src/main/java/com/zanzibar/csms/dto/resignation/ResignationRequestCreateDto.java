package com.zanzibar.csms.dto.resignation;

import com.zanzibar.csms.entity.enums.ResignationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResignationRequestCreateDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Resignation type is required")
    private ResignationType resignationType;

    @NotNull(message = "Resignation date is required")
    @Future(message = "Resignation date must be in the future")
    private LocalDate resignationDate;

    @Future(message = "Last working date must be in the future")
    private LocalDate lastWorkingDate;

    @Size(max = 2000, message = "Reason cannot exceed 2000 characters")
    private String reason;

    @DecimalMin(value = "0.0", message = "Payment amount must be positive")
    private BigDecimal paymentAmount;

    private Boolean paymentConfirmed;

    // Helper methods
    public boolean isStandardNotice() {
        return resignationType == ResignationType.THREE_MONTH_NOTICE;
    }

    public boolean isImmediateWithPayment() {
        return resignationType == ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT;
    }

    public boolean requiresPayment() {
        return resignationType != null && resignationType.requiresPayment();
    }

    public int getNoticePeriodDays() {
        return resignationType != null ? resignationType.getNoticePeriodDays() : 0;
    }
}