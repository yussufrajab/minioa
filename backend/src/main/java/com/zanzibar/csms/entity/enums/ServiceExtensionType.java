package com.zanzibar.csms.entity.enums;

public enum ServiceExtensionType {
    RETIREMENT_EXTENSION("Retirement Extension"),
    CONTRACT_EXTENSION("Contract Extension"),
    SPECIAL_SKILLS_EXTENSION("Special Skills Extension"),
    INSTITUTIONAL_NEED_EXTENSION("Institutional Need Extension"),
    EMERGENCY_EXTENSION("Emergency Extension");

    private final String description;

    ServiceExtensionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRetirementRelated() {
        return this == RETIREMENT_EXTENSION || this == SPECIAL_SKILLS_EXTENSION;
    }

    public boolean isContractRelated() {
        return this == CONTRACT_EXTENSION || this == INSTITUTIONAL_NEED_EXTENSION;
    }
}