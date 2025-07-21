package com.zanzibar.csms.entity.enums;

public enum ResignationType {
    THREE_MONTH_NOTICE("3-Month Notice", "Standard 3-month notice period resignation", 90, false),
    TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT("24-Hour Notice with Payment", "Immediate resignation with payment in lieu of notice", 1, true);

    private final String displayName;
    private final String description;
    private final int noticePeriodDays;
    private final boolean requiresPayment;

    ResignationType(String displayName, String description, int noticePeriodDays, boolean requiresPayment) {
        this.displayName = displayName;
        this.description = description;
        this.noticePeriodDays = noticePeriodDays;
        this.requiresPayment = requiresPayment;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getNoticePeriodDays() {
        return noticePeriodDays;
    }

    public boolean requiresPayment() {
        return requiresPayment;
    }

    public boolean isStandardNotice() {
        return this == THREE_MONTH_NOTICE;
    }

    public boolean isImmediateWithPayment() {
        return this == TWENTY_FOUR_HOUR_NOTICE_WITH_PAYMENT;
    }
}