package com.zanzibar.csms.entity.enums;

public enum ResignationDocumentType {
    // Common documents for all resignation types
    REQUEST_LETTER("Request Letter", true, true),
    EMPLOYEE_RESIGNATION_LETTER("Employee Resignation Letter", true, true),
    
    // Specific document for 24-hour notice with payment
    PAYMENT_RECEIPT("Payment Receipt", false, true),
    
    // Additional supporting documents
    HANDOVER_NOTES("Handover Notes", false, false),
    CLEARANCE_FORMS("Clearance Forms", false, false);

    private final String displayName;
    private final boolean requiredForStandard;
    private final boolean requiredForImmediate;

    ResignationDocumentType(String displayName, boolean requiredForStandard, boolean requiredForImmediate) {
        this.displayName = displayName;
        this.requiredForStandard = requiredForStandard;
        this.requiredForImmediate = requiredForImmediate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequiredForResignationType(ResignationType resignationType) {
        return switch (resignationType) {
            case THREE_MONTH_NOTICE -> requiredForStandard;
            case TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT -> requiredForImmediate;
        };
    }

    public static ResignationDocumentType[] getRequiredDocuments(ResignationType resignationType) {
        return switch (resignationType) {
            case THREE_MONTH_NOTICE -> new ResignationDocumentType[] {
                REQUEST_LETTER, EMPLOYEE_RESIGNATION_LETTER
            };
            case TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT -> new ResignationDocumentType[] {
                REQUEST_LETTER, EMPLOYEE_RESIGNATION_LETTER, PAYMENT_RECEIPT
            };
        };
    }

    public static ResignationDocumentType[] getCommonDocuments() {
        return new ResignationDocumentType[] {
            REQUEST_LETTER, EMPLOYEE_RESIGNATION_LETTER
        };
    }

    public static ResignationDocumentType[] getOptionalDocuments() {
        return new ResignationDocumentType[] {
            HANDOVER_NOTES, CLEARANCE_FORMS
        };
    }
}