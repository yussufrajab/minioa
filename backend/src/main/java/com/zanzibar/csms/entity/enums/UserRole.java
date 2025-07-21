package com.zanzibar.csms.entity.enums;

/**
 * UserRole Enum - Compatibility enum for services expecting role enumeration
 * Maps to String roles used in Prisma database schema
 */
public enum UserRole {
    ADMIN("Administrator - Full system access"),
    HRO("Human Resource Officer - Submit requests for employees"),
    HHRMD("Head of HR Management Department - Approve/reject all requests"),
    HRMO("Human Resource Management Officer - Approve specific requests"),
    DO("Disciplinary Officer - Handle complaints and disciplinary actions"),
    EMPLOYEE("Employee - Submit complaints, view profile"),
    EMP("Employee - Submit complaints, view profile"), // Alias for EMPLOYEE for backward compatibility
    PO("Planning Officer - View reports and analytics"),
    CSCS("Civil Service Commission Secretary - System oversight"),
    HRRP("HR Responsible Personnel - Supervise institutional HR");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get UserRole from string value (case-insensitive)
     */
    public static UserRole fromString(String role) {
        if (role == null) {
            return EMPLOYEE; // Default role
        }
        
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EMPLOYEE; // Default role if not found
        }
    }

    /**
     * Check if role has administrative privileges
     */
    public boolean isAdministrative() {
        return this == ADMIN || this == HHRMD || this == CSCS;
    }

    /**
     * Check if role can submit HR requests
     */
    public boolean canSubmitRequests() {
        return this == HRO || this == HHRMD || this == HRMO || this == ADMIN;
    }

    /**
     * Check if role can approve HR requests
     */
    public boolean canApproveRequests() {
        return this == HHRMD || this == HRMO || this == ADMIN;
    }

    /**
     * Check if role can handle complaints
     */
    public boolean canHandleComplaints() {
        return this == DO || this == HHRMD || this == ADMIN;
    }

    /**
     * Check if role can view reports
     */
    public boolean canViewReports() {
        return this != EMPLOYEE; // All roles except EMPLOYEE can view some reports
    }

    /**
     * Check if role can manage users
     */
    public boolean canManageUsers() {
        return this == ADMIN;
    }

    /**
     * Get role priority for workflow routing (lower number = higher priority)
     */
    public int getPriority() {
        return switch (this) {
            case ADMIN -> 1;
            case HHRMD -> 2;
            case CSCS -> 3;
            case HRMO -> 4;
            case DO -> 5;
            case HRO -> 6;
            case HRRP -> 7;
            case PO -> 8;
            case EMPLOYEE, EMP -> 9; // Both EMPLOYEE and EMP have same priority
        };
    }
}