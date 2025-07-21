package com.zanzibar.csms.entity;

public enum ServiceExtensionDocumentType {
    SERVICE_EXTENSION_REQUEST_LETTER("Service Extension Request Letter", true),
    EMPLOYEE_CONSENT_LETTER("Employee Consent Letter", true);

    private final String displayName;
    private final boolean mandatory;

    ServiceExtensionDocumentType(String displayName, boolean mandatory) {
        this.displayName = displayName;
        this.mandatory = mandatory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public static ServiceExtensionDocumentType[] getMandatoryDocuments() {
        return new ServiceExtensionDocumentType[] {
            SERVICE_EXTENSION_REQUEST_LETTER,
            EMPLOYEE_CONSENT_LETTER
        };
    }
}