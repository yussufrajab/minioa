package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cadre_change_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CadreChangeRequest extends Request {

    @Column(name = "current_cadre", nullable = false)
    private String currentCadre;

    @Column(name = "proposed_cadre", nullable = false)
    private String proposedCadre;

    @Column(name = "education_level", nullable = false)
    private String educationLevel;

    @Column(name = "education_completion_year", nullable = false)
    private Integer educationCompletionYear;

    @Column(name = "institution_attended")
    private String institutionAttended;

    @Column(name = "qualification_obtained")
    private String qualificationObtained;

    @Column(name = "justification", columnDefinition = "TEXT", nullable = false)
    private String justification;

    @Column(name = "current_salary_scale")
    private String currentSalaryScale;

    @Column(name = "proposed_salary_scale")
    private String proposedSalaryScale;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    @Column(name = "relevant_experience", columnDefinition = "TEXT")
    private String relevantExperience;

    @Column(name = "training_completed", columnDefinition = "TEXT")
    private String trainingCompleted;

    @Column(name = "skills_acquired", columnDefinition = "TEXT")
    private String skillsAcquired;

    @Column(name = "performance_rating")
    private String performanceRating;

    @Column(name = "supervisor_recommendation", columnDefinition = "TEXT")
    private String supervisorRecommendation;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "tcu_verification_required")
    private Boolean tcuVerificationRequired = false;

    @Column(name = "tcu_verification_status")
    private String tcuVerificationStatus;

    @Column(name = "tcu_verification_date")
    private LocalDate tcuVerificationDate;

    @Column(name = "hr_assessment", columnDefinition = "TEXT")
    private String hrAssessment;

    @Column(name = "budgetary_implications", columnDefinition = "TEXT")
    private String budgetaryImplications;

    @OneToMany(mappedBy = "cadreChangeRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CadreChangeDocument> cadreChangeDocuments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.getRequestType() == null) {
            this.setRequestType(RequestType.CADRE_CHANGE);
        }
        
        // Set effective date to 30 days from submission if not provided
        if (this.effectiveDate == null) {
            this.effectiveDate = LocalDate.now().plusDays(30);
        }
    }

    public void addDocument(CadreChangeDocument document) {
        cadreChangeDocuments.add(document);
        document.setCadreChangeRequest(this);
    }

    public void removeDocument(CadreChangeDocument document) {
        cadreChangeDocuments.remove(document);
        document.setCadreChangeRequest(null);
    }

    public boolean isEducationVerificationRequired() {
        return tcuVerificationRequired != null && tcuVerificationRequired;
    }

    public boolean isEducationVerified() {
        return tcuVerificationStatus != null && tcuVerificationStatus.equals("VERIFIED");
    }

    public boolean hasRequiredDocuments() {
        if (cadreChangeDocuments == null || cadreChangeDocuments.isEmpty()) {
            return false;
        }
        
        // Check for mandatory documents
        boolean hasEducationCertificate = cadreChangeDocuments.stream()
            .anyMatch(doc -> doc.getDocumentType() == CadreChangeDocumentType.EDUCATION_CERTIFICATE);
        
        boolean hasCadreChangeForm = cadreChangeDocuments.stream()
            .anyMatch(doc -> doc.getDocumentType() == CadreChangeDocumentType.CADRE_CHANGE_FORM);
        
        boolean hasSignedLetter = cadreChangeDocuments.stream()
            .anyMatch(doc -> doc.getDocumentType() == CadreChangeDocumentType.SIGNED_LETTER);
        
        return hasEducationCertificate && hasCadreChangeForm && hasSignedLetter;
    }

    public boolean isEligibleForProcessing() {
        return hasRequiredDocuments() && 
               (!isEducationVerificationRequired() || isEducationVerified());
    }

    public int getYearsSinceEducationCompletion() {
        if (educationCompletionYear == null) {
            return 0;
        }
        return LocalDate.now().getYear() - educationCompletionYear;
    }

    public List<CadreChangeDocument> getCadreChangeDocuments() {
        return cadreChangeDocuments;
    }

    public void setCadreChangeDocuments(List<CadreChangeDocument> cadreChangeDocuments) {
        this.cadreChangeDocuments = cadreChangeDocuments;
    }
}