package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.ResignationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resignation_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResignationRequest extends Request {

    @Enumerated(EnumType.STRING)
    @Column(name = "resignation_type", nullable = false)
    private ResignationType resignationType;

    @Column(name = "resignation_date")
    private LocalDate resignationDate;

    @Column(name = "last_working_date")
    private LocalDate lastWorkingDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "payment_amount", precision = 19, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "payment_confirmed")
    private Boolean paymentConfirmed;

    @Column(name = "clearance_completed")
    private Boolean clearanceCompleted;

    @Column(name = "handover_completed")
    private Boolean handoverCompleted;

    @OneToMany(mappedBy = "resignationRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResignationDocument> resignationDocuments = new ArrayList<>();

    // Helper methods
    public boolean hasRequiredDocuments() {
        if (resignationDocuments == null || resignationDocuments.isEmpty()) {
            return false;
        }
        
        var requiredTypes = com.zanzibar.csms.entity.enums.ResignationDocumentType.getRequiredDocuments(resignationType);
        
        for (var requiredType : requiredTypes) {
            boolean hasDocument = resignationDocuments.stream()
                .anyMatch(doc -> doc.getDocumentType() == requiredType);
            if (!hasDocument) {
                return false;
            }
        }
        
        return true;
    }

    public boolean isStandardNotice() {
        return resignationType == ResignationType.THREE_MONTH_NOTICE;
    }

    public boolean isImmediateWithPayment() {
        return resignationType == ResignationType.TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT;
    }

    public String getResignationTypeDisplay() {
        return resignationType != null ? resignationType.getDisplayName() : "Unknown";
    }

    public List<String> getMissingDocuments() {
        List<String> missing = new ArrayList<>();
        if (resignationType == null) return missing;
        
        var requiredTypes = com.zanzibar.csms.entity.enums.ResignationDocumentType.getRequiredDocuments(resignationType);
        
        for (var requiredType : requiredTypes) {
            boolean hasDocument = resignationDocuments.stream()
                .anyMatch(doc -> doc.getDocumentType() == requiredType);
            if (!hasDocument) {
                missing.add(requiredType.getDisplayName());
            }
        }
        
        return missing;
    }

    public boolean isEligibleForProcessing() {
        if (!hasRequiredDocuments()) {
            return false;
        }

        // For 24-hour notice with payment, payment must be confirmed
        if (isImmediateWithPayment()) {
            return Boolean.TRUE.equals(paymentConfirmed);
        }

        return true;
    }

    public int getNoticePeriodDays() {
        return resignationType != null ? resignationType.getNoticePeriodDays() : 0;
    }

    public boolean requiresPayment() {
        return resignationType != null && resignationType.requiresPayment();
    }

    public boolean isPaymentConfirmed() {
        return Boolean.TRUE.equals(paymentConfirmed);
    }

    public boolean isClearanceCompleted() {
        return Boolean.TRUE.equals(clearanceCompleted);
    }

    public boolean isHandoverCompleted() {
        return Boolean.TRUE.equals(handoverCompleted);
    }
}