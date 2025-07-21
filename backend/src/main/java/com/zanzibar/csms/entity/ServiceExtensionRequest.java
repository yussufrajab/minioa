package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_extension_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceExtensionRequest extends Request {

    // Required fields as per requirements document
    @Column(name = "extension_duration_years", nullable = false)
    private Integer extensionDurationYears;

    @OneToMany(mappedBy = "serviceExtensionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceExtensionDocument> serviceExtensionDocuments = new ArrayList<>();

    // Optional fields for business logic (not in requirements but useful)
    @Column(name = "retirement_eligibility_date")
    private LocalDate retirementEligibilityDate;

    @Column(name = "notification_sent_date")
    private LocalDateTime notificationSentDate;

    @Column(name = "expiration_warning_sent")
    private Boolean expirationWarningSent;

    // Helper methods
    public boolean hasRequiredDocuments() {
        if (serviceExtensionDocuments == null || serviceExtensionDocuments.isEmpty()) {
            return false;
        }
        
        boolean hasRequestLetter = serviceExtensionDocuments.stream()
            .anyMatch(doc -> doc.getDocumentType() == ServiceExtensionDocumentType.SERVICE_EXTENSION_REQUEST_LETTER);
        
        boolean hasEmployeeConsent = serviceExtensionDocuments.stream()
            .anyMatch(doc -> doc.getDocumentType() == ServiceExtensionDocumentType.EMPLOYEE_CONSENT_LETTER);
        
        return hasRequestLetter && hasEmployeeConsent;
    }

    public String getExtensionDurationDisplay() {
        if (extensionDurationYears != null && extensionDurationYears > 0) {
            String yearText = extensionDurationYears == 1 ? " year" : " years";
            return extensionDurationYears + yearText;
        }
        return "";
    }
}