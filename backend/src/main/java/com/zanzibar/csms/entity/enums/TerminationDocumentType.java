package com.zanzibar.csms.entity.enums;

public enum TerminationDocumentType {
    // For Unconfirmed Out of Probation
    APPLICATION_LETTER("Application Letter"),
    PROBATION_EXTENSION("Probation Extension Letter"),
    PERFORMANCE_APPRAISAL("Performance Appraisal Report"),
    
    // For Not Returning After Leave
    REMINDER_LETTER("Reminder Letter"),
    WARNING_LETTER("Warning Letter"),
    MEDIA_ANNOUNCEMENT("Media Announcement"),
    
    // For Disciplinary
    INVESTIGATION_REPORT("Investigation Committee Report"),
    EVIDENCE_PRIOR_DISCIPLINARY("Evidence of Prior Disciplinary Actions"),
    VERBAL_WARNING("Verbal Warning Letter"),
    WRITTEN_WARNING("Written Warning Letter"),
    SUMMONS_LETTER("Summons Letter"),
    
    // Common documents
    TERMINATION_REQUEST_LETTER("Termination Request Letter"),
    EMPLOYEE_RESPONSE("Employee Response Letter"),
    WITNESS_STATEMENT("Witness Statement"),
    MEDICAL_REPORT("Medical Report"),
    HR_RECOMMENDATION("HR Recommendation Letter"),
    FINAL_DECISION_LETTER("Final Decision Letter"),
    OTHER("Other Supporting Document");

    private final String description;

    TerminationDocumentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static TerminationDocumentType[] getRequiredForScenario(TerminationScenario scenario) {
        switch (scenario) {
            case UNCONFIRMED_OUT_OF_PROBATION:
                return new TerminationDocumentType[]{
                    APPLICATION_LETTER, PROBATION_EXTENSION, PERFORMANCE_APPRAISAL
                };
            case NOT_RETURNING_AFTER_LEAVE:
                return new TerminationDocumentType[]{
                    APPLICATION_LETTER, REMINDER_LETTER, WARNING_LETTER, MEDIA_ANNOUNCEMENT
                };
            case DISCIPLINARY:
                return new TerminationDocumentType[]{
                    INVESTIGATION_REPORT, EVIDENCE_PRIOR_DISCIPLINARY, SUMMONS_LETTER
                };
            default:
                return new TerminationDocumentType[]{TERMINATION_REQUEST_LETTER};
        }
    }
}