package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "confirmation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "request_id")
public class ConfirmationRequest extends Request {

    @Column(name = "probation_start_date", nullable = false)
    @NotNull(message = "Probation start date is required")
    @Past(message = "Probation start date must be in the past")
    private LocalDate probationStartDate;

    @Column(name = "probation_end_date", nullable = false)
    @NotNull(message = "Probation end date is required")
    @PastOrPresent(message = "Probation end date must be in the past or present")
    private LocalDate probationEndDate;

    @Column(name = "performance_rating")
    @Size(max = 100, message = "Performance rating cannot exceed 100 characters")
    @Pattern(regexp = "^(EXCELLENT|GOOD|SATISFACTORY|NEEDS_IMPROVEMENT|UNSATISFACTORY)$", 
             message = "Performance rating must be one of: EXCELLENT, GOOD, SATISFACTORY, NEEDS_IMPROVEMENT, UNSATISFACTORY")
    private String performanceRating;

    @Column(name = "supervisor_recommendation", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Supervisor recommendation cannot exceed 2000 characters")
    private String supervisorRecommendation;

    @Column(name = "hr_assessment", columnDefinition = "TEXT")
    @Size(max = 2000, message = "HR assessment cannot exceed 2000 characters")
    private String hrAssessment;

    @Column(name = "proposed_confirmation_date")
    @Future(message = "Proposed confirmation date must be in the future")
    private LocalDate proposedConfirmationDate;

    @Column(name = "current_salary")
    @DecimalMin(value = "0.0", message = "Current salary must be non-negative")
    private Double currentSalary;

    @Column(name = "proposed_salary")
    @DecimalMin(value = "0.0", message = "Proposed salary must be non-negative")
    private Double proposedSalary;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        this.setRequestType(com.zanzibar.csms.entity.enums.RequestType.CONFIRMATION);
    }

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