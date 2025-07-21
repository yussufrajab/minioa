package com.zanzibar.csms.dto.resignation;

import com.zanzibar.csms.entity.enums.ResignationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
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
public class ResignationRequestUpdateDto {

    private ResignationType resignationType;

    @Future(message = "Resignation date must be in the future")
    private LocalDate resignationDate;

    @Future(message = "Last working date must be in the future")
    private LocalDate lastWorkingDate;

    @Size(max = 2000, message = "Reason cannot exceed 2000 characters")
    private String reason;

    @DecimalMin(value = "0.0", message = "Payment amount must be positive")
    private BigDecimal paymentAmount;

    private Boolean paymentConfirmed;

    private Boolean clearanceCompleted;

    private Boolean handoverCompleted;
}