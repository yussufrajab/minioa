package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.RetirementType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "retirement_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetirementRequest extends Request {

    @Enumerated(EnumType.STRING)
    @Column(name = "retirement_type", nullable = false)
    private RetirementType retirementType;

    @Column(name = "retirement_date")
    private LocalDate retirementDate;

    @Column(name = "last_working_date")
    private LocalDate lastWorkingDate;

    @Column(name = "pension_eligibility_confirmed")
    private Boolean pensionEligibilityConfirmed;

    @Column(name = "clearance_completed")
    private Boolean clearanceCompleted;

    @OneToMany(mappedBy = "retirementRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RetirementDocument> retirementDocuments = new ArrayList<>();

    // Helper methods
    public boolean hasRequiredDocuments() {
        if (retirementDocuments == null || retirementDocuments.isEmpty()) {
            return false;
        }
        
        var requiredTypes = com.zanzibar.csms.entity.enums.RetirementDocumentType.getRequiredDocuments(retirementType);
        
        for (var requiredType : requiredTypes) {
            boolean hasDocument = retirementDocuments.stream()
                .anyMatch(doc -> doc.getDocumentType() == requiredType);
            if (!hasDocument) {
                return false;
            }
        }
        
        return true;
    }

    public boolean isCompulsoryRetirement() {
        return retirementType == RetirementType.COMPULSORY;
    }

    public boolean isVoluntaryRetirement() {
        return retirementType == RetirementType.VOLUNTARY;
    }

    public boolean isMedicalRetirement() {
        return retirementType == RetirementType.ILLNESS;
    }

    public String getRetirementTypeDisplay() {
        return retirementType != null ? retirementType.getDisplayName() : "Unknown";
    }

    public List<String> getMissingDocuments() {
        List<String> missing = new ArrayList<>();
        if (retirementType == null) return missing;
        
        var requiredTypes = com.zanzibar.csms.entity.enums.RetirementDocumentType.getRequiredDocuments(retirementType);
        
        for (var requiredType : requiredTypes) {
            boolean hasDocument = retirementDocuments.stream()
                .anyMatch(doc -> doc.getDocumentType() == requiredType);
            if (!hasDocument) {
                missing.add(requiredType.getDisplayName());
            }
        }
        
        return missing;
    }
}