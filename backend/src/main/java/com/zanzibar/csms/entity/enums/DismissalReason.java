package com.zanzibar.csms.entity.enums;

public enum DismissalReason {
    GROSS_MISCONDUCT("Gross Misconduct"),
    SERIOUS_DISCIPLINARY_OFFENSE("Serious Disciplinary Offense"),
    REPEATED_MISCONDUCT("Repeated Misconduct After Warnings"),
    FRAUD_OR_THEFT("Fraud or Theft"),
    INSUBORDINATION("Serious Insubordination"),
    BREACH_OF_CONFIDENTIALITY("Breach of Confidentiality"),
    CRIMINAL_CONVICTION("Criminal Conviction Related to Employment"),
    FALSIFICATION_OF_RECORDS("Falsification of Official Records"),
    ABUSE_OF_OFFICE("Abuse of Office or Position"),
    CONFLICT_OF_INTEREST("Serious Conflict of Interest"),
    VIOLENCE_OR_HARASSMENT("Violence or Harassment in Workplace"),
    SUBSTANCE_ABUSE("Substance Abuse Affecting Performance"),
    UNAUTHORIZED_ABSENCE("Prolonged Unauthorized Absence"),
    BREACH_OF_CODE_OF_CONDUCT("Serious Breach of Code of Conduct"),
    OTHER("Other Serious Misconduct");

    private final String description;

    DismissalReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresInvestigation() {
        return this == GROSS_MISCONDUCT || 
               this == FRAUD_OR_THEFT || 
               this == CRIMINAL_CONVICTION ||
               this == FALSIFICATION_OF_RECORDS ||
               this == ABUSE_OF_OFFICE ||
               this == VIOLENCE_OR_HARASSMENT;
    }

    public boolean requiresPriorWarnings() {
        return this == REPEATED_MISCONDUCT ||
               this == INSUBORDINATION ||
               this == UNAUTHORIZED_ABSENCE ||
               this == SUBSTANCE_ABUSE;
    }
}