package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.PromotionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "promotion_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "request_id")
public class PromotionRequest extends Request {

    @Enumerated(EnumType.STRING)
    @Column(name = "promotion_type", nullable = false)
    @NotNull(message = "Promotion type is required")
    private PromotionType promotionType;

    @Column(name = "current_position", nullable = false)
    @NotBlank(message = "Current position is required")
    @Size(max = 255, message = "Current position cannot exceed 255 characters")
    private String currentPosition;

    @Column(name = "current_grade", nullable = false)
    @NotBlank(message = "Current grade is required")
    @Size(max = 100, message = "Current grade cannot exceed 100 characters")
    private String currentGrade;

    @Column(name = "proposed_position", nullable = false)
    @NotBlank(message = "Proposed position is required")
    @Size(max = 255, message = "Proposed position cannot exceed 255 characters")
    private String proposedPosition;

    @Column(name = "proposed_grade", nullable = false)
    @NotBlank(message = "Proposed grade is required")
    @Size(max = 100, message = "Proposed grade cannot exceed 100 characters")
    private String proposedGrade;

    @Column(name = "current_salary")
    @DecimalMin(value = "0.0", message = "Current salary must be non-negative")
    private Double currentSalary;

    @Column(name = "proposed_salary")
    @DecimalMin(value = "0.0", message = "Proposed salary must be non-negative")
    private Double proposedSalary;

    @Column(name = "effective_date")
    @Future(message = "Effective date must be in the future")
    private LocalDate effectiveDate;

    @Column(name = "justification", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Justification cannot exceed 2000 characters")
    private String justification;

    @Column(name = "performance_rating")
    @Pattern(regexp = "^(EXCELLENT|GOOD|SATISFACTORY|NEEDS_IMPROVEMENT|UNSATISFACTORY)$", 
             message = "Performance rating must be one of: EXCELLENT, GOOD, SATISFACTORY, NEEDS_IMPROVEMENT, UNSATISFACTORY")
    private String performanceRating;

    @Column(name = "years_in_current_position")
    @Min(value = 0, message = "Years in current position must be non-negative")
    private Integer yearsInCurrentPosition;

    @Column(name = "qualifications_met", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Qualifications met cannot exceed 2000 characters")
    private String qualificationsMet;

    @Column(name = "supervisor_recommendation", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Supervisor recommendation cannot exceed 2000 characters")
    private String supervisorRecommendation;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.setRequestType(com.zanzibar.csms.entity.enums.RequestType.PROMOTION);
    }

    // Helper methods for business logic
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

    public boolean meetsMinimumYearsRequirement() {
        // As per FR8.3: minimum 2 years in current position
        return yearsInCurrentPosition != null && yearsInCurrentPosition >= 2;
    }

    public boolean hasPerformanceRating() {
        return performanceRating != null && !performanceRating.trim().isEmpty();
    }

    public boolean hasSupervisorRecommendation() {
        return supervisorRecommendation != null && !supervisorRecommendation.trim().isEmpty();
    }

    public boolean hasQualificationsMet() {
        return qualificationsMet != null && !qualificationsMet.trim().isEmpty();
    }

    public String getPromotionTypeDisplay() {
        return promotionType != null ? promotionType.getDisplayName() : "Unknown";
    }
}