package com.zanzibar.csms.entity.enums;

public enum PromotionType {
    EDUCATIONAL("Educational", "Promotion based on educational advancement"),
    PERFORMANCE("Performance", "Promotion based on performance appraisals");

    private final String displayName;
    private final String description;

    PromotionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}