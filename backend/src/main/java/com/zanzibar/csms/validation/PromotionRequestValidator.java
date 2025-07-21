package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.promotion.PromotionRequestCreateDto;
import com.zanzibar.csms.entity.enums.PromotionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PromotionRequestValidator implements ConstraintValidator<ValidPromotionRequest, PromotionRequestCreateDto> {

    @Override
    public void initialize(ValidPromotionRequest constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(PromotionRequestCreateDto request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let @NotNull handle null values
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // Validate salary increase
        if (request.getCurrentSalary() != null && request.getProposedSalary() != null) {
            if (request.getProposedSalary() <= request.getCurrentSalary()) {
                context.buildConstraintViolationWithTemplate("Proposed salary must be higher than current salary")
                        .addPropertyNode("proposedSalary")
                        .addConstraintViolation();
                isValid = false;
            }

            // Check maximum salary increase (50% cap)
            double increasePercentage = ((request.getProposedSalary() - request.getCurrentSalary()) / request.getCurrentSalary()) * 100;
            if (increasePercentage > 50) {
                context.buildConstraintViolationWithTemplate("Salary increase cannot exceed 50% of current salary")
                        .addPropertyNode("proposedSalary")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate promotion type specific requirements
        if (request.getPromotionType() == PromotionType.EDUCATIONAL) {
            if (!request.hasQualificationsMet()) {
                context.buildConstraintViolationWithTemplate("Qualifications met is required for educational promotions")
                        .addPropertyNode("qualificationsMet")
                        .addConstraintViolation();
                isValid = false;
            }
        } else if (request.getPromotionType() == PromotionType.PERFORMANCE) {
            if (!request.hasPerformanceRating()) {
                context.buildConstraintViolationWithTemplate("Performance rating is required for performance promotions")
                        .addPropertyNode("performanceRating")
                        .addConstraintViolation();
                isValid = false;
            }

            if (!request.hasSupervisorRecommendation()) {
                context.buildConstraintViolationWithTemplate("Supervisor recommendation is required for performance promotions")
                        .addPropertyNode("supervisorRecommendation")
                        .addConstraintViolation();
                isValid = false;
            }

            // Performance promotions require good performance ratings
            if (request.getPerformanceRating() != null && 
                (request.getPerformanceRating().equals("NEEDS_IMPROVEMENT") || 
                 request.getPerformanceRating().equals("UNSATISFACTORY"))) {
                context.buildConstraintViolationWithTemplate("Performance promotions require satisfactory or better performance rating")
                        .addPropertyNode("performanceRating")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate minimum years in position (2 years)
        if (request.getYearsInCurrentPosition() == null || request.getYearsInCurrentPosition() < 2) {
            context.buildConstraintViolationWithTemplate("Employee must have served minimum 2 years in current position")
                    .addPropertyNode("yearsInCurrentPosition")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate position progression (proposed position should be different from current)
        if (request.getCurrentPosition() != null && request.getProposedPosition() != null && 
            request.getCurrentPosition().equals(request.getProposedPosition())) {
            context.buildConstraintViolationWithTemplate("Proposed position must be different from current position")
                    .addPropertyNode("proposedPosition")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate grade progression (proposed grade should be different from current)
        if (request.getCurrentGrade() != null && request.getProposedGrade() != null && 
            request.getCurrentGrade().equals(request.getProposedGrade())) {
            context.buildConstraintViolationWithTemplate("Proposed grade must be different from current grade")
                    .addPropertyNode("proposedGrade")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}