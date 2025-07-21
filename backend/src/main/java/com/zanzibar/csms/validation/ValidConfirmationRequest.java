package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConfirmationRequestValidator.class)
@Documented
public @interface ValidConfirmationRequest {
    
    String message() default "Invalid confirmation request";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}