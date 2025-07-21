package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.termination.TerminationRequestCreateDto;
import com.zanzibar.csms.entity.enums.TerminationScenario;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class TerminationRequestValidator implements ConstraintValidator<ValidTerminationRequest, TerminationRequestCreateDto> {

    @Override
    public void initialize(ValidTerminationRequest constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(TerminationRequestCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // Validate scenario-specific requirements
        if (dto.getScenario() != null) {
            switch (dto.getScenario()) {
                case UNCONFIRMED_OUT_OF_PROBATION:
                    if (dto.getProbationEndDate() == null) {
                        context.buildConstraintViolationWithTemplate(
                                "Probation end date is required for unconfirmed out of probation scenario")
                                .addPropertyNode("probationEndDate")
                                .addConstraintViolation();
                        isValid = false;
                    }
                    break;

                case DISCIPLINARY:
                    if (dto.getIncidentDate() == null) {
                        context.buildConstraintViolationWithTemplate(
                                "Incident date is required for disciplinary termination")
                                .addPropertyNode("incidentDate")
                                .addConstraintViolation();
                        isValid = false;
                    }
                    
                    if (dto.getInvestigationSummary() == null || dto.getInvestigationSummary().trim().isEmpty()) {
                        context.buildConstraintViolationWithTemplate(
                                "Investigation summary is required for disciplinary termination")
                                .addPropertyNode("investigationSummary")
                                .addConstraintViolation();
                        isValid = false;
                    }
                    break;

                case NOT_RETURNING_AFTER_LEAVE:
                    if (dto.getIncidentDate() == null) {
                        context.buildConstraintViolationWithTemplate(
                                "Date when employee was expected to return is required")
                                .addPropertyNode("incidentDate")
                                .addConstraintViolation();
                        isValid = false;
                    }
                    break;
            }
        }

        // Validate incident date is not in the future
        if (dto.getIncidentDate() != null && dto.getIncidentDate().isAfter(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate(
                    "Incident date cannot be in the future")
                    .addPropertyNode("incidentDate")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate probation end date is not in the future
        if (dto.getProbationEndDate() != null && dto.getProbationEndDate().isAfter(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate(
                    "Probation end date cannot be in the future")
                    .addPropertyNode("probationEndDate")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate prior warnings count is reasonable
        if (dto.getPriorWarningsCount() != null && dto.getPriorWarningsCount() < 0) {
            context.buildConstraintViolationWithTemplate(
                    "Prior warnings count cannot be negative")
                    .addPropertyNode("priorWarningsCount")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}