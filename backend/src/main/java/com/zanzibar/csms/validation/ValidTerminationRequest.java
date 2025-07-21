package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TerminationRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTerminationRequest {
    String message() default "Invalid termination request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}