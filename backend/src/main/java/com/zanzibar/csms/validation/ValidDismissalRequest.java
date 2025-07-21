package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DismissalRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDismissalRequest {
    String message() default "Invalid dismissal request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}