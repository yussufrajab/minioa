package com.zanzibar.csms.dto.retirement;

import com.zanzibar.csms.entity.enums.RetirementType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetirementRequestCreateDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Retirement type is required")
    private RetirementType retirementType;

    @NotNull(message = "Retirement date is required")
    @Future(message = "Retirement date must be in the future")
    private LocalDate retirementDate;

    @Future(message = "Last working date must be in the future")
    private LocalDate lastWorkingDate;

    private Boolean pensionEligibilityConfirmed;

    // Helper methods
    public boolean isCompulsoryRetirement() {
        return retirementType == RetirementType.COMPULSORY;
    }

    public boolean isVoluntaryRetirement() {
        return retirementType == RetirementType.VOLUNTARY;
    }

    public boolean isMedicalRetirement() {
        return retirementType == RetirementType.ILLNESS;
    }

    public boolean requiresHealthDocuments() {
        return retirementType != null && retirementType.requiresHealthDocuments();
    }
}