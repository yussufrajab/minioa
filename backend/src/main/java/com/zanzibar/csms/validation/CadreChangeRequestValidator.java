package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.cadre.CadreChangeRequestCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class CadreChangeRequestValidator implements ConstraintValidator<ValidCadreChangeRequest, CadreChangeRequestCreateDto> {

    @Override
    public void initialize(ValidCadreChangeRequest constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(CadreChangeRequestCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // Validate education completion year
        if (dto.getEducationCompletionYear() != null) {
            int currentYear = LocalDate.now().getYear();
            if (dto.getEducationCompletionYear() > currentYear) {
                context.buildConstraintViolationWithTemplate(
                        "Education completion year cannot be in the future")
                        .addPropertyNode("educationCompletionYear")
                        .addConstraintViolation();
                isValid = false;
            }
            
            if (dto.getEducationCompletionYear() < 1900) {
                context.buildConstraintViolationWithTemplate(
                        "Education completion year must be after 1900")
                        .addPropertyNode("educationCompletionYear")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate that proposed cadre is different from current cadre
        if (dto.getCurrentCadre() != null && dto.getProposedCadre() != null) {
            if (dto.getCurrentCadre().equals(dto.getProposedCadre())) {
                context.buildConstraintViolationWithTemplate(
                        "Proposed cadre must be different from current cadre")
                        .addPropertyNode("proposedCadre")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate effective date
        if (dto.getEffectiveDate() != null) {
            if (dto.getEffectiveDate().isBefore(LocalDate.now())) {
                context.buildConstraintViolationWithTemplate(
                        "Effective date must be in the future")
                        .addPropertyNode("effectiveDate")
                        .addConstraintViolation();
                isValid = false;
            }
            
            // Effective date should not be more than 1 year in the future
            if (dto.getEffectiveDate().isAfter(LocalDate.now().plusYears(1))) {
                context.buildConstraintViolationWithTemplate(
                        "Effective date cannot be more than 1 year in the future")
                        .addPropertyNode("effectiveDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate TCU verification requirements
        if (dto.requiresTcuVerification()) {
            if (dto.getTcuVerificationStatus() == null || dto.getTcuVerificationStatus().trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate(
                        "TCU verification status is required when TCU verification is required")
                        .addPropertyNode("tcuVerificationStatus")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate years of experience against education completion
        if (dto.getYearsOfExperience() != null && dto.getEducationCompletionYear() != null) {
            int yearsSinceEducation = LocalDate.now().getYear() - dto.getEducationCompletionYear();
            if (dto.getYearsOfExperience() > yearsSinceEducation) {
                context.buildConstraintViolationWithTemplate(
                        "Years of experience cannot exceed years since education completion")
                        .addPropertyNode("yearsOfExperience")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate salary scale progression (if both are provided)
        if (dto.getCurrentSalaryScale() != null && dto.getProposedSalaryScale() != null) {
            if (dto.getCurrentSalaryScale().equals(dto.getProposedSalaryScale())) {
                context.buildConstraintViolationWithTemplate(
                        "Proposed salary scale should be different from current salary scale")
                        .addPropertyNode("proposedSalaryScale")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate performance rating if provided
        if (dto.getPerformanceRating() != null && !dto.getPerformanceRating().trim().isEmpty()) {
            String rating = dto.getPerformanceRating().toUpperCase();
            if (!rating.matches("^(EXCELLENT|VERY_GOOD|GOOD|SATISFACTORY|NEEDS_IMPROVEMENT|POOR)$")) {
                context.buildConstraintViolationWithTemplate(
                        "Performance rating must be one of: EXCELLENT, VERY_GOOD, GOOD, SATISFACTORY, NEEDS_IMPROVEMENT, POOR")
                        .addPropertyNode("performanceRating")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate minimum experience for cadre change (business rule)
        if (dto.getYearsOfExperience() != null && dto.getYearsOfExperience() < 2) {
            context.buildConstraintViolationWithTemplate(
                    "Minimum 2 years of experience required for cadre change")
                    .addPropertyNode("yearsOfExperience")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}