package com.zanzibar.csms.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class LWOPReasonValidator implements ConstraintValidator<ValidLWOPReason, String> {

    // List of forbidden reasons as per FR5.3
    private static final List<String> FORBIDDEN_REASONS = Arrays.asList(
        "employment in internal organizations",
        "spouse relocation for work or transfer",
        "engagement in politics",
        "funeral or mourning ceremonies outside zanzibar",
        "caring for a sick family member",
        "spouse studying abroad",
        "spouse living outside zanzibar",
        "spouse of high-ranking officials (president/vice president)"
    );

    @Override
    public void initialize(ValidLWOPReason constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String reason, ConstraintValidatorContext context) {
        if (reason == null || reason.trim().isEmpty()) {
            return false;
        }

        String normalizedReason = reason.toLowerCase().trim();
        
        // Check if the reason contains any forbidden keywords
        for (String forbiddenReason : FORBIDDEN_REASONS) {
            if (normalizedReason.contains(forbiddenReason)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "The reason '" + forbiddenReason + "' is not permitted for LWOP requests")
                    .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}