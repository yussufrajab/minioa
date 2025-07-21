package com.zanzibar.csms.entity.enums;

public enum RequestType {
    CONFIRMATION("Employment Confirmation"),
    LWOP("Leave Without Pay"),
    TERMINATION("Termination"),
    DISMISSAL("Dismissal"),
    COMPLAINT("Complaint"),
    PROMOTION("Promotion"),
    CADRE_CHANGE("Change of Cadre"),
    SERVICE_EXTENSION("Service Extension"),
    RETIREMENT("Retirement"),
    RESIGNATION("Resignation"),
    TRANSFER("Transfer");

    private final String description;

    RequestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}