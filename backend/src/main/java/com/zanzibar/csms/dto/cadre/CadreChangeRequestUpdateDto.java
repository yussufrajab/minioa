package com.zanzibar.csms.dto.cadre;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadreChangeRequestUpdateDto {

    @Size(max = 100, message = "Current cadre cannot exceed 100 characters")
    private String currentCadre;

    @Size(max = 100, message = "Proposed cadre cannot exceed 100 characters")
    private String proposedCadre;

    @Size(max = 100, message = "Education level cannot exceed 100 characters")
    private String educationLevel;

    @Min(value = 1900, message = "Education completion year must be after 1900")
    @Max(value = 2030, message = "Education completion year cannot be in the future")
    private Integer educationCompletionYear;

    @Size(max = 200, message = "Institution attended cannot exceed 200 characters")
    private String institutionAttended;

    @Size(max = 200, message = "Qualification obtained cannot exceed 200 characters")
    private String qualificationObtained;

    @Size(min = 50, max = 2000, message = "Justification must be between 50 and 2000 characters")
    private String justification;

    @Size(max = 50, message = "Current salary scale cannot exceed 50 characters")
    private String currentSalaryScale;

    @Size(max = 50, message = "Proposed salary scale cannot exceed 50 characters")
    private String proposedSalaryScale;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperience;

    @Size(max = 1500, message = "Relevant experience cannot exceed 1500 characters")
    private String relevantExperience;

    @Size(max = 1500, message = "Training completed cannot exceed 1500 characters")
    private String trainingCompleted;

    @Size(max = 1500, message = "Skills acquired cannot exceed 1500 characters")
    private String skillsAcquired;

    @Size(max = 50, message = "Performance rating cannot exceed 50 characters")
    private String performanceRating;

    @Size(max = 1000, message = "Supervisor recommendation cannot exceed 1000 characters")
    private String supervisorRecommendation;

    @Future(message = "Effective date must be in the future")
    private LocalDate effectiveDate;

    private Boolean tcuVerificationRequired;

    @Size(max = 50, message = "TCU verification status cannot exceed 50 characters")
    private String tcuVerificationStatus;

    private LocalDate tcuVerificationDate;

    @Size(max = 1000, message = "HR assessment cannot exceed 1000 characters")
    private String hrAssessment;

    @Size(max = 1000, message = "Budgetary implications cannot exceed 1000 characters")
    private String budgetaryImplications;

    private List<String> documentIds;
}