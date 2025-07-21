package com.zanzibar.csms.entity.enums;

public enum ComplaintType {
    HARASSMENT("Harassment"),
    DISCRIMINATION("Discrimination"),
    UNFAIR_TREATMENT("Unfair Treatment"),
    WORKPLACE_SAFETY("Workplace Safety"),
    POLICY_VIOLATION("Policy Violation"),
    MISCONDUCT("Misconduct"),
    ABUSE_OF_POWER("Abuse of Power"),
    CORRUPTION("Corruption"),
    NEPOTISM("Nepotism"),
    FAVORITISM("Favoritism"),
    SALARY_DISPUTE("Salary Dispute"),
    PROMOTION_DISPUTE("Promotion Dispute"),
    WORKING_CONDITIONS("Working Conditions"),
    DISCIPLINARY_ACTION("Disciplinary Action"),
    OTHER("Other");

    private final String description;

    ComplaintType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}