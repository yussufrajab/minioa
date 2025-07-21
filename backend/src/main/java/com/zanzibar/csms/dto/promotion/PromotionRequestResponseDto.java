package com.zanzibar.csms.dto.promotion;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.PromotionType;
import com.zanzibar.csms.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;

    // Promotion specific fields
    private PromotionType promotionType;
    private String promotionTypeDisplay;
    private String currentPosition;
    private String currentGrade;
    private String proposedPosition;
    private String proposedGrade;
    private Double currentSalary;
    private Double proposedSalary;
    private LocalDate effectiveDate;
    private String justification;
    private String performanceRating;
    private Integer yearsInCurrentPosition;
    private String qualificationsMet;
    private String supervisorRecommendation;

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
        return yearsInCurrentPosition != null && yearsInCurrentPosition >= 2;
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

    public String getStatusDisplay() {
        return status != null ? status.getDescription() : "Unknown";
    }

    public boolean isEffectiveDateApproaching() {
        return effectiveDate != null && 
               effectiveDate.isAfter(LocalDate.now()) && 
               effectiveDate.isBefore(LocalDate.now().plusDays(30));
    }
}