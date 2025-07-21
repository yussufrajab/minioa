package com.zanzibar.csms.entity.enums;

public enum TerminationScenario {
    UNCONFIRMED_OUT_OF_PROBATION("Unconfirmed Employee Out of Probation"),
    NOT_RETURNING_AFTER_LEAVE("Employee Not Returning After Leave"),
    DISCIPLINARY("Disciplinary Action"),
    POOR_PERFORMANCE("Poor Performance During Probation"),
    MEDICAL_UNFITNESS("Medical Unfitness for Duty"),
    ABANDONMENT_OF_DUTY("Abandonment of Duty");

    private final String description;

    TerminationScenario(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}