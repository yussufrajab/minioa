package com.zanzibar.csms.entity.enums;

public enum RetirementDocumentType {
    // Common documents for all retirement types
    REQUEST_LETTER("Request Letter", true, true, true),
    ARDHIL_HALI("Ardhil-Hali", true, true, true),
    BIRTH_CERTIFICATE("Birth Certificate", true, true, true),
    
    // Specific documents for illness retirement
    HEALTH_BOARD_REPORT("Health Board Report", false, false, true),
    SICK_LEAVE_RECORDS("Sick Leave Records", false, false, true);

    private final String displayName;
    private final boolean requiredForCompulsory;
    private final boolean requiredForVoluntary;
    private final boolean requiredForIllness;

    RetirementDocumentType(String displayName, boolean requiredForCompulsory, 
                          boolean requiredForVoluntary, boolean requiredForIllness) {
        this.displayName = displayName;
        this.requiredForCompulsory = requiredForCompulsory;
        this.requiredForVoluntary = requiredForVoluntary;
        this.requiredForIllness = requiredForIllness;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequiredForRetirementType(RetirementType retirementType) {
        return switch (retirementType) {
            case COMPULSORY -> requiredForCompulsory;
            case VOLUNTARY -> requiredForVoluntary;
            case ILLNESS -> requiredForIllness;
        };
    }

    public static RetirementDocumentType[] getRequiredDocuments(RetirementType retirementType) {
        return switch (retirementType) {
            case COMPULSORY, VOLUNTARY -> new RetirementDocumentType[] {
                REQUEST_LETTER, ARDHIL_HALI, BIRTH_CERTIFICATE
            };
            case ILLNESS -> new RetirementDocumentType[] {
                REQUEST_LETTER, HEALTH_BOARD_REPORT, SICK_LEAVE_RECORDS, ARDHIL_HALI, BIRTH_CERTIFICATE
            };
        };
    }

    public static RetirementDocumentType[] getCommonDocuments() {
        return new RetirementDocumentType[] {
            REQUEST_LETTER, ARDHIL_HALI, BIRTH_CERTIFICATE
        };
    }

    public static RetirementDocumentType[] getIllnessSpecificDocuments() {
        return new RetirementDocumentType[] {
            HEALTH_BOARD_REPORT, SICK_LEAVE_RECORDS
        };
    }
}