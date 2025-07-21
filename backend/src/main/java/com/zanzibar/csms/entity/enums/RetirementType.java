package com.zanzibar.csms.entity.enums;

public enum RetirementType {
    COMPULSORY("Compulsory", "Mandatory retirement at retirement age"),
    VOLUNTARY("Voluntary", "Employee voluntary retirement before mandatory age"),
    ILLNESS("Illness", "Medical retirement due to health conditions");

    private final String displayName;
    private final String description;

    RetirementType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresHealthDocuments() {
        return this == ILLNESS;
    }

    public boolean isCompulsory() {
        return this == COMPULSORY;
    }

    public boolean isVoluntary() {
        return this == VOLUNTARY;
    }

    public boolean isMedical() {
        return this == ILLNESS;
    }
}