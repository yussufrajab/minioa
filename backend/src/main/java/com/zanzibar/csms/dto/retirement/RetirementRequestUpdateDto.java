package com.zanzibar.csms.dto.retirement;

import com.zanzibar.csms.entity.enums.RetirementType;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetirementRequestUpdateDto {

    private RetirementType retirementType;

    @Future(message = "Retirement date must be in the future")
    private LocalDate retirementDate;

    @Future(message = "Last working date must be in the future")
    private LocalDate lastWorkingDate;

    private Boolean pensionEligibilityConfirmed;

    private Boolean clearanceCompleted;
}