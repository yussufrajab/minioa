package com.zanzibar.csms.exception;

public class ConfirmationValidationException extends RuntimeException {
    
    private final String field;
    private final String value;
    private final String validationRule;
    
    public ConfirmationValidationException(String field, String value, String validationRule) {
        super("Validation failed for field '" + field + "' with value '" + value + "': " + validationRule);
        this.field = field;
        this.value = value;
        this.validationRule = validationRule;
    }
    
    public ConfirmationValidationException(String message) {
        super(message);
        this.field = null;
        this.value = null;
        this.validationRule = message;
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getValidationRule() {
        return validationRule;
    }
}