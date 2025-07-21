package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LWOPReasonValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLWOPReason {
    
    String message() default "Invalid LWOP reason. This reason is not permitted.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}