package com.zanzibar.csms.exception;

public class ConfirmationEligibilityException extends RuntimeException {
    
    private final String employeeId;
    private final String reason;
    
    public ConfirmationEligibilityException(String employeeId, String reason) {
        super("Employee " + employeeId + " is not eligible for confirmation: " + reason);
        this.employeeId = employeeId;
        this.reason = reason;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public String getReason() {
        return reason;
    }
}