package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LWOPDurationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLWOPDuration {
    
    String message() default "Invalid LWOP duration. Duration must be between 1 month and 3 years.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}