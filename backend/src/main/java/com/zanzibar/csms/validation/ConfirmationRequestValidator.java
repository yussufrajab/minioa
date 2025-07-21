package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.ConfirmationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConfirmationRequestValidator implements ConstraintValidator<ValidConfirmationRequest, ConfirmationRequestDto> {

    @Override
    public void initialize(ValidConfirmationRequest constraintAnnotation) {
        // Initialization logic if needed
    }

    @Override
    public boolean isValid(ConfirmationRequestDto request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // Validate probation period
        if (request.getProbationStartDate() != null && request.getProbationEndDate() != null) {
            if (!request.isProbationPeriodValid()) {
                context.buildConstraintViolationWithTemplate("Probation end date must be after probation start date")
                        .addPropertyNode("probationEndDate")
                        .addConstraintViolation();
                isValid = false;
            }
            
            if (!request.isEligibleForConfirmation()) {
                context.buildConstraintViolationWithTemplate("Employee must complete at least 12 months of probation before confirmation")
                        .addPropertyNode("probationEndDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate salary information
        if (request.getCurrentSalary() != null && request.getProposedSalary() != null) {
            if (request.getProposedSalary() < request.getCurrentSalary()) {
                context.buildConstraintViolationWithTemplate("Proposed salary cannot be less than current salary")
                        .addPropertyNode("proposedSalary")
                        .addConstraintViolation();
                isValid = false;
            }
            
            double increasePercentage = request.getSalaryIncreasePercentage();
            if (increasePercentage > 50) { // Max 50% increase
                context.buildConstraintViolationWithTemplate("Salary increase cannot exceed 50% of current salary")
                        .addPropertyNode("proposedSalary")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate performance rating
        if (request.getPerformanceRating() != null) {
            if ("UNSATISFACTORY".equals(request.getPerformanceRating())) {
                context.buildConstraintViolationWithTemplate("Cannot confirm employee with unsatisfactory performance rating")
                        .addPropertyNode("performanceRating")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate documentation requirements
        if (request.getPerformanceRating() != null && !request.hasSupervisorRecommendation()) {
            context.buildConstraintViolationWithTemplate("Supervisor recommendation is required when performance rating is provided")
                    .addPropertyNode("supervisorRecommendation")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}