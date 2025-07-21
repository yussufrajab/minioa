package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ServiceExtensionRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidServiceExtensionRequest {
    String message() default "Invalid service extension request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}