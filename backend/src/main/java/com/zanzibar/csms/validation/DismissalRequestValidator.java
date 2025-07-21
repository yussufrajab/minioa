package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.dismissal.DismissalRequestCreateDto;
import com.zanzibar.csms.entity.enums.DismissalReason;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DismissalRequestValidator implements ConstraintValidator<ValidDismissalRequest, DismissalRequestCreateDto> {

    @Override
    public void initialize(ValidDismissalRequest constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(DismissalRequestCreateDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true; // Let @NotNull handle null validation
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        // Validate reason-specific requirements
        if (dto.getDismissalReason() != null) {
            DismissalReason reason = dto.getDismissalReason();
            
            // Check if investigation is required
            if (reason.requiresInvestigation()) {
                if (dto.getInvestigationSummary() == null || dto.getInvestigationSummary().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                            "Investigation summary is required for " + reason.getDescription())
                            .addPropertyNode("investigationSummary")
                            .addConstraintViolation();
                    isValid = false;
                }
                
                if (dto.getInvestigationOfficer() == null || dto.getInvestigationOfficer().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                            "Investigation officer is required for " + reason.getDescription())
                            .addPropertyNode("investigationOfficer")
                            .addConstraintViolation();
                    isValid = false;
                }
                
                if (dto.getInvestigationStartDate() == null) {
                    context.buildConstraintViolationWithTemplate(
                            "Investigation start date is required for " + reason.getDescription())
                            .addPropertyNode("investigationStartDate")
                            .addConstraintViolation();
                    isValid = false;
                }
            }
            
            // Check if prior warnings are required
            if (reason.requiresPriorWarnings()) {
                if (dto.getPriorWarningsCount() == null || dto.getPriorWarningsCount() == 0) {
                    context.buildConstraintViolationWithTemplate(
                            "Prior warnings are required for " + reason.getDescription())
                            .addPropertyNode("priorWarningsCount")
                            .addConstraintViolation();
                    isValid = false;
                }
                
                if (dto.getDisciplinaryHistory() == null || dto.getDisciplinaryHistory().trim().isEmpty()) {
                    context.buildConstraintViolationWithTemplate(
                            "Disciplinary history is required for " + reason.getDescription())
                            .addPropertyNode("disciplinaryHistory")
                            .addConstraintViolation();
                    isValid = false;
                }
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

        // Validate investigation dates
        if (dto.getInvestigationStartDate() != null && dto.getInvestigationEndDate() != null) {
            if (dto.getInvestigationStartDate().isAfter(dto.getInvestigationEndDate())) {
                context.buildConstraintViolationWithTemplate(
                        "Investigation start date cannot be after end date")
                        .addPropertyNode("investigationStartDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        // Validate investigation dates are not in the future
        if (dto.getInvestigationStartDate() != null && dto.getInvestigationStartDate().isAfter(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate(
                    "Investigation start date cannot be in the future")
                    .addPropertyNode("investigationStartDate")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate show cause date is not in the past (if set)
        if (dto.getShowCauseDate() != null && dto.getShowCauseDate().isBefore(LocalDateTime.now().minusDays(1))) {
            context.buildConstraintViolationWithTemplate(
                    "Show cause date cannot be in the past")
                    .addPropertyNode("showCauseDate")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate hearing date is not in the past (if set)
        if (dto.getHearingDate() != null && dto.getHearingDate().isBefore(LocalDateTime.now().minusDays(1))) {
            context.buildConstraintViolationWithTemplate(
                    "Hearing date cannot be in the past")
                    .addPropertyNode("hearingDate")
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

        // Validate settlement amount is not negative
        if (dto.getFinalSettlementAmount() != null && dto.getFinalSettlementAmount() < 0) {
            context.buildConstraintViolationWithTemplate(
                    "Final settlement amount cannot be negative")
                    .addPropertyNode("finalSettlementAmount")
                    .addConstraintViolation();
            isValid = false;
        }

        // Validate effective dismissal date
        if (dto.getEffectiveDismissalDate() != null && dto.getEffectiveDismissalDate().isBefore(LocalDate.now())) {
            context.buildConstraintViolationWithTemplate(
                    "Effective dismissal date cannot be in the past")
                    .addPropertyNode("effectiveDismissalDate")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}