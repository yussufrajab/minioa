package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ServiceExtensionRequestValidator implements ConstraintValidator<ValidServiceExtensionRequest, ServiceExtensionRequestCreateDto> {

    @Override
    public void initialize(ValidServiceExtensionRequest constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(ServiceExtensionRequestCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null validation
        }

        // Basic validation is handled by Bean Validation annotations
        // No additional complex business rules per simplified requirements
        return true;
    }
}