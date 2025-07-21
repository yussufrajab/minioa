package com.zanzibar.csms.dto.cadre;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadreChangeRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;

    // Cadre change specific fields
    private String currentCadre;
    private String proposedCadre;
    private String educationLevel;
    private Integer educationCompletionYear;
    private String institutionAttended;
    private String qualificationObtained;
    private String justification;
    private String currentSalaryScale;
    private String proposedSalaryScale;
    private Integer yearsOfExperience;
    private String relevantExperience;
    private String trainingCompleted;
    private String skillsAcquired;
    private String performanceRating;
    private String supervisorRecommendation;
    private LocalDate effectiveDate;
    private Boolean tcuVerificationRequired;
    private String tcuVerificationStatus;
    private LocalDate tcuVerificationDate;
    private String hrAssessment;
    private String budgetaryImplications;

    // Computed fields
    private Boolean educationVerificationRequired;
    private Boolean educationVerified;
    private Boolean hasRequiredDocuments;
    private Boolean eligibleForProcessing;
    private Integer yearsSinceEducationCompletion;

    // Document information
    private List<CadreChangeDocumentDto> documents;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods for computed fields
    public boolean isEducationVerificationRequired() {
        return educationVerificationRequired != null && educationVerificationRequired;
    }

    public boolean isEducationVerified() {
        return educationVerified != null && educationVerified;
    }

    public boolean hasRequiredDocuments() {
        return hasRequiredDocuments != null && hasRequiredDocuments;
    }

    public boolean isEligibleForProcessing() {
        return eligibleForProcessing != null && eligibleForProcessing;
    }

    public boolean requiresTcuVerification() {
        return tcuVerificationRequired != null && tcuVerificationRequired;
    }

    public boolean isPending() {
        return status != null && status.isPending();
    }

    public boolean isApproved() {
        return status != null && status == RequestStatus.APPROVED;
    }

    public boolean isRejected() {
        return status != null && status == RequestStatus.REJECTED;
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    public String getStatusDisplay() {
        return status != null ? status.getDescription() : "Unknown";
    }

    public boolean isEffectiveDateApproaching() {
        if (effectiveDate == null || !isApproved()) {
            return false;
        }
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        return effectiveDate.isBefore(sevenDaysFromNow) || effectiveDate.isEqual(sevenDaysFromNow);
    }

    public long getDaysUntilEffective() {
        if (effectiveDate == null) {
            return 0;
        }
        return LocalDate.now().until(effectiveDate).getDays();
    }

    public boolean isOverdue() {
        if (isTerminal()) {
            return false;
        }
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return submissionDate != null && submissionDate.isBefore(thirtyDaysAgo);
    }

    public long getDaysInReview() {
        if (submissionDate == null) {
            return 0;
        }
        LocalDateTime endDate = approvalDate != null ? approvalDate : LocalDateTime.now();
        return java.time.Duration.between(submissionDate, endDate).toDays();
    }
}