package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CadreChangeRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCadreChangeRequest {
    String message() default "Invalid cadre change request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}