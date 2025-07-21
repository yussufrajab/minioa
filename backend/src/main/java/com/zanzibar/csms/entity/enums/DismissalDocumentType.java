package com.zanzibar.csms.entity.enums;

public enum DismissalDocumentType {
    // Investigation Documents
    INVESTIGATION_REPORT("Investigation Committee Report"),
    INVESTIGATION_EVIDENCE("Investigation Evidence"),
    WITNESS_STATEMENTS("Witness Statements"),
    EXPERT_REPORTS("Expert Reports"),
    
    // Disciplinary History
    DISCIPLINARY_RECORD("Employee Disciplinary Record"),
    PRIOR_WARNING_LETTERS("Prior Warning Letters"),
    PREVIOUS_INCIDENTS("Previous Incident Reports"),
    
    // Legal Documents
    CRIMINAL_CONVICTION_RECORD("Criminal Conviction Record"),
    COURT_JUDGMENT("Court Judgment"),
    POLICE_REPORT("Police Report"),
    
    // Administrative Documents
    DISMISSAL_REQUEST_LETTER("Dismissal Request Letter"),
    SHOW_CAUSE_NOTICE("Show Cause Notice"),
    EMPLOYEE_RESPONSE("Employee Response to Charges"),
    HEARING_MINUTES("Disciplinary Hearing Minutes"),
    HR_RECOMMENDATION("HR Recommendation Letter"),
    
    // Supporting Evidence
    CCTV_FOOTAGE("CCTV Footage"),
    AUDIO_RECORDINGS("Audio Recordings"),
    FINANCIAL_RECORDS("Financial Records"),
    EMAIL_CORRESPONDENCE("Email Correspondence"),
    SYSTEM_LOGS("System Access Logs"),
    
    // Final Decision
    DISMISSAL_ORDER("Dismissal Order"),
    APPEAL_DOCUMENTS("Appeal Documents"),
    FINAL_CLEARANCE("Final Clearance Certificate"),
    
    // Common
    OTHER("Other Supporting Document");

    private final String description;

    DismissalDocumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static DismissalDocumentType[] getRequiredForReason(DismissalReason reason) {
        switch (reason) {
            case GROSS_MISCONDUCT:
                return new DismissalDocumentType[]{
                    INVESTIGATION_REPORT, DISCIPLINARY_RECORD, DISMISSAL_REQUEST_LETTER
                };
            case FRAUD_OR_THEFT:
                return new DismissalDocumentType[]{
                    INVESTIGATION_REPORT, INVESTIGATION_EVIDENCE, FINANCIAL_RECORDS, DISMISSAL_REQUEST_LETTER
                };
            case REPEATED_MISCONDUCT:
                return new DismissalDocumentType[]{
                    DISCIPLINARY_RECORD, PRIOR_WARNING_LETTERS, PREVIOUS_INCIDENTS, DISMISSAL_REQUEST_LETTER
                };
            case CRIMINAL_CONVICTION:
                return new DismissalDocumentType[]{
                    CRIMINAL_CONVICTION_RECORD, COURT_JUDGMENT, DISMISSAL_REQUEST_LETTER
                };
            case VIOLENCE_OR_HARASSMENT:
                return new DismissalDocumentType[]{
                    INVESTIGATION_REPORT, WITNESS_STATEMENTS, INVESTIGATION_EVIDENCE, DISMISSAL_REQUEST_LETTER
                };
            default:
                return new DismissalDocumentType[]{DISMISSAL_REQUEST_LETTER, INVESTIGATION_REPORT};
        }
    }

    public boolean isMandatory() {
        return this == DISMISSAL_REQUEST_LETTER || 
               this == INVESTIGATION_REPORT ||
               this == DISCIPLINARY_RECORD;
    }
}