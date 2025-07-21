package com.zanzibar.csms.exception;

public class PromotionValidationException extends RuntimeException {
    
    public PromotionValidationException(String message) {
        super(message);
    }
    
    public PromotionValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}