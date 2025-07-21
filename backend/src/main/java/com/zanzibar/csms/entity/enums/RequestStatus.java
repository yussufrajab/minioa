package com.zanzibar.csms.entity.enums;

public enum RequestStatus {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    HRO_REVIEW("HRO Review"),
    HRMO_REVIEW("HRMO Review"),
    HHRMD_REVIEW("HHRMD Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    RETURNED("Returned for Revision");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    public boolean isPending() {
        return this == SUBMITTED || this == HRO_REVIEW || this == HRMO_REVIEW || this == HHRMD_REVIEW;
    }
}