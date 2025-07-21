package com.zanzibar.csms.dto.promotion;

import com.zanzibar.csms.entity.enums.PromotionType;
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
public class PromotionRequestUpdateDto {

    private PromotionType promotionType;

    @Size(max = 255, message = "Current position cannot exceed 255 characters")
    private String currentPosition;

    @Size(max = 100, message = "Current grade cannot exceed 100 characters")
    private String currentGrade;

    @Size(max = 255, message = "Proposed position cannot exceed 255 characters")
    private String proposedPosition;

    @Size(max = 100, message = "Proposed grade cannot exceed 100 characters")
    private String proposedGrade;

    @DecimalMin(value = "0.0", message = "Current salary must be non-negative")
    private Double currentSalary;

    @DecimalMin(value = "0.0", message = "Proposed salary must be non-negative")
    private Double proposedSalary;

    @Future(message = "Effective date must be in the future")
    private LocalDate effectiveDate;

    @Size(max = 2000, message = "Justification cannot exceed 2000 characters")
    private String justification;

    @Pattern(regexp = "^(EXCELLENT|GOOD|SATISFACTORY|NEEDS_IMPROVEMENT|UNSATISFACTORY)$", 
             message = "Performance rating must be one of: EXCELLENT, GOOD, SATISFACTORY, NEEDS_IMPROVEMENT, UNSATISFACTORY")
    private String performanceRating;

    @Min(value = 2, message = "Employee must have served minimum 2 years in current position")
    private Integer yearsInCurrentPosition;

    @Size(max = 2000, message = "Qualifications met cannot exceed 2000 characters")
    private String qualificationsMet;

    @Size(max = 2000, message = "Supervisor recommendation cannot exceed 2000 characters")
    private String supervisorRecommendation;

    // Helper methods for validation
    public boolean isEducationalPromotion() {
        return promotionType == PromotionType.EDUCATIONAL;
    }

    public boolean isPerformancePromotion() {
        return promotionType == PromotionType.PERFORMANCE;
    }

    public boolean hasSalaryIncrease() {
        return currentSalary != null && proposedSalary != null && proposedSalary > currentSalary;
    }

    public Double getSalaryIncreaseAmount() {
        if (currentSalary == null || proposedSalary == null) {
            return 0.0;
        }
        return proposedSalary - currentSalary;
    }

    public Double getSalaryIncreasePercentage() {
        if (currentSalary == null || proposedSalary == null || currentSalary == 0) {
            return 0.0;
        }
        return ((proposedSalary - currentSalary) / currentSalary) * 100;
    }

    public boolean hasQualificationsMet() {
        return qualificationsMet != null && !qualificationsMet.trim().isEmpty();
    }

    public boolean hasSupervisorRecommendation() {
        return supervisorRecommendation != null && !supervisorRecommendation.trim().isEmpty();
    }

    public boolean hasPerformanceRating() {
        return performanceRating != null && !performanceRating.trim().isEmpty();
    }
}