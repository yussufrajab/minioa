package com.zanzibar.csms.entity.enums;

public enum EmploymentStatus {
    ACTIVE("Active"),
    CONFIRMED("Confirmed"),
    UNCONFIRMED("Unconfirmed"),
    ON_PROBATION("On Probation"),
    ON_LEAVE("On Leave"),
    ON_LWOP("On LWOP"),
    RETIRED("Retired"),
    RESIGNED("Resigned"),
    TERMINATED("Terminated"),
    DISMISSED("Dismissed");

    private final String description;

    EmploymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}