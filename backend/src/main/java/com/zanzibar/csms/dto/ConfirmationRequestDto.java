package com.zanzibar.csms.dto;

import com.zanzibar.csms.validation.ValidConfirmationRequest;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ValidConfirmationRequest
public class ConfirmationRequestDto extends RequestDto {

    @NotNull(message = "Probation start date is required")
    @Past(message = "Probation start date must be in the past")
    private LocalDate probationStartDate;

    @NotNull(message = "Probation end date is required")
    @PastOrPresent(message = "Probation end date must be in the past or present")
    private LocalDate probationEndDate;

    @Size(max = 100, message = "Performance rating cannot exceed 100 characters")
    @Pattern(regexp = "^(EXCELLENT|GOOD|SATISFACTORY|NEEDS_IMPROVEMENT|UNSATISFACTORY)$", 
             message = "Performance rating must be one of: EXCELLENT, GOOD, SATISFACTORY, NEEDS_IMPROVEMENT, UNSATISFACTORY")
    private String performanceRating;

    @Size(max = 2000, message = "Supervisor recommendation cannot exceed 2000 characters")
    private String supervisorRecommendation;

    @Size(max = 2000, message = "HR assessment cannot exceed 2000 characters")
    private String hrAssessment;

    @Future(message = "Proposed confirmation date must be in the future")
    private LocalDate proposedConfirmationDate;

    @DecimalMin(value = "0.0", message = "Current salary must be non-negative")
    private Double currentSalary;

    @DecimalMin(value = "0.0", message = "Proposed salary must be non-negative")
    private Double proposedSalary;

    // Helper methods for business logic
    public boolean isProbationPeriodValid() {
        if (probationStartDate == null || probationEndDate == null) {
            return false;
        }
        return probationEndDate.isAfter(probationStartDate);
    }

    public long getProbationDurationInDays() {
        if (probationStartDate == null || probationEndDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(probationStartDate, probationEndDate);
    }

    public boolean isEligibleForConfirmation() {
        // Employee must have completed at least 12 months (365 days) of probation
        return getProbationDurationInDays() >= 365;
    }

    public boolean hasPerformanceRating() {
        return performanceRating != null && !performanceRating.trim().isEmpty();
    }

    public boolean hasSupervisorRecommendation() {
        return supervisorRecommendation != null && !supervisorRecommendation.trim().isEmpty();
    }

    public boolean hasHrAssessment() {
        return hrAssessment != null && !hrAssessment.trim().isEmpty();
    }

    public boolean isSalaryIncreaseProposed() {
        return currentSalary != null && proposedSalary != null && proposedSalary > currentSalary;
    }

    public double getSalaryIncreaseAmount() {
        if (currentSalary == null || proposedSalary == null) {
            return 0.0;
        }
        return proposedSalary - currentSalary;
    }

    public double getSalaryIncreasePercentage() {
        if (currentSalary == null || proposedSalary == null || currentSalary == 0) {
            return 0.0;
        }
        return ((proposedSalary - currentSalary) / currentSalary) * 100;
    }
}