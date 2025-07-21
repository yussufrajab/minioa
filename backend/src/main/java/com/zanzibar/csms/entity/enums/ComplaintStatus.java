package com.zanzibar.csms.entity.enums;

public enum ComplaintStatus {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    ACKNOWLEDGED("Acknowledged"),
    UNDER_INVESTIGATION("Under Investigation"),
    EVIDENCE_GATHERING("Evidence Gathering"),
    HEARING_SCHEDULED("Hearing Scheduled"),
    HEARING_COMPLETED("Hearing Completed"),
    RESOLVED("Resolved"),
    CLOSED("Closed"),
    DISMISSED("Dismissed"),
    ESCALATED("Escalated"),
    APPEALED("Appealed"),
    APPEAL_UNDER_REVIEW("Appeal Under Review"),
    APPEAL_RESOLVED("Appeal Resolved"),
    WITHDRAWN("Withdrawn"),
    ASSIGNED("Assigned"),
    REJECTED("Rejected"),
    UNDER_REVIEW("Under Review"),
    RETURNED("Returned"),
    EVIDENCE_COLLECTION("Evidence Collection"),
    PENDING_DECISION("Pending Decision"),
    APPEAL_REVIEW("Appeal Review"),
    APPEAL_UPHELD("Appeal Upheld"),
    APPEAL_DISMISSED("Appeal Dismissed");

    private final String description;

    ComplaintStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != RESOLVED && this != CLOSED && this != DISMISSED && this != APPEAL_RESOLVED && 
               this != WITHDRAWN && this != REJECTED && this != APPEAL_DISMISSED;
    }

    public boolean isTerminal() {
        return this == RESOLVED || this == CLOSED || this == DISMISSED || this == APPEAL_RESOLVED || 
               this == WITHDRAWN || this == REJECTED || this == APPEAL_DISMISSED || this == APPEAL_UPHELD;
    }
}