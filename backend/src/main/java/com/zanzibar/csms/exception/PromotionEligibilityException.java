package com.zanzibar.csms.exception;

public class PromotionEligibilityException extends RuntimeException {
    
    public PromotionEligibilityException(String message) {
        super(message);
    }
    
    public PromotionEligibilityException(String message, Throwable cause) {
        super(message, cause);
    }
}