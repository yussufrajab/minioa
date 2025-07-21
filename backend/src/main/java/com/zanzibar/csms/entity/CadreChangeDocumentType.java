package com.zanzibar.csms.entity;

public enum CadreChangeDocumentType {
    SIGNED_LETTER("Signed Letter of Cadre Change", true),
    CADRE_CHANGE_FORM("Completed Cadre Change Form", true),
    EDUCATION_CERTIFICATE("Educational Certificate", true),
    TCU_VERIFICATION("TCU Verification Document", false),
    PERFORMANCE_APPRAISAL("Performance Appraisal Report", false),
    TRAINING_CERTIFICATE("Training Certificate", false),
    EXPERIENCE_LETTER("Experience Letter", false),
    SUPERVISOR_RECOMMENDATION("Supervisor Recommendation Letter", false),
    QUALIFICATION_TRANSCRIPT("Academic Transcript", false),
    PROFESSIONAL_CERTIFICATE("Professional Certificate", false),
    SKILLS_ASSESSMENT("Skills Assessment Report", false),
    BUDGET_APPROVAL("Budget Approval Letter", false),
    ADDITIONAL_SUPPORTING_DOCUMENT("Additional Supporting Document", false);

    private final String displayName;
    private final boolean mandatory;

    CadreChangeDocumentType(String displayName, boolean mandatory) {
        this.displayName = displayName;
        this.mandatory = mandatory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getDescription() {
        return switch (this) {
            case SIGNED_LETTER -> "Official signed letter requesting cadre change";
            case CADRE_CHANGE_FORM -> "Completed built-in cadre change form";
            case EDUCATION_CERTIFICATE -> "Educational certificate supporting the cadre change";
            case TCU_VERIFICATION -> "TCU verification for foreign qualifications";
            case PERFORMANCE_APPRAISAL -> "Latest performance appraisal report";
            case TRAINING_CERTIFICATE -> "Relevant training certificates";
            case EXPERIENCE_LETTER -> "Work experience letters";
            case SUPERVISOR_RECOMMENDATION -> "Supervisor's recommendation letter";
            case QUALIFICATION_TRANSCRIPT -> "Academic transcript or detailed results";
            case PROFESSIONAL_CERTIFICATE -> "Professional certification documents";
            case SKILLS_ASSESSMENT -> "Skills assessment or competency evaluation";
            case BUDGET_APPROVAL -> "Budget approval for salary increment";
            case ADDITIONAL_SUPPORTING_DOCUMENT -> "Any additional supporting document";
        };
    }

    public static CadreChangeDocumentType[] getMandatoryDocuments() {
        return new CadreChangeDocumentType[]{
            SIGNED_LETTER,
            CADRE_CHANGE_FORM,
            EDUCATION_CERTIFICATE
        };
    }

    public static CadreChangeDocumentType[] getOptionalDocuments() {
        return new CadreChangeDocumentType[]{
            TCU_VERIFICATION,
            PERFORMANCE_APPRAISAL,
            TRAINING_CERTIFICATE,
            EXPERIENCE_LETTER,
            SUPERVISOR_RECOMMENDATION,
            QUALIFICATION_TRANSCRIPT,
            PROFESSIONAL_CERTIFICATE,
            SKILLS_ASSESSMENT,
            BUDGET_APPROVAL,
            ADDITIONAL_SUPPORTING_DOCUMENT
        };
    }
}