package com.zanzibar.csms.entity.enums;

public enum ServiceExtensionStatus {
    ACTIVE("Active"),
    PENDING_RENEWAL("Pending Renewal"),
    EXPIRED("Expired"),
    CANCELLED("Cancelled"),
    EXTENDED("Extended");

    private final String description;

    ServiceExtensionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == EXTENDED;
    }

    public boolean requiresAction() {
        return this == PENDING_RENEWAL || this == EXPIRED;
    }
}