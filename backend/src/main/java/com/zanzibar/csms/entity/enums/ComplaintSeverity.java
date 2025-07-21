package com.zanzibar.csms.entity.enums;

public enum ComplaintSeverity {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical");

    private final String description;

    ComplaintSeverity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getDaysToResolve() {
        return switch (this) {
            case LOW -> 30;
            case MEDIUM -> 21;
            case HIGH -> 14;
            case CRITICAL -> 7;
        };
    }
}