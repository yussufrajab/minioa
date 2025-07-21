package com.zanzibar.csms.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PromotionRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPromotionRequest {
    String message() default "Invalid promotion request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}