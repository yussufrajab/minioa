package com.zanzibar.csms.validation;

import com.zanzibar.csms.dto.LeaveWithoutPayRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class LWOPDurationValidator implements ConstraintValidator<ValidLWOPDuration, LeaveWithoutPayRequestDto> {

    @Override
    public void initialize(ValidLWOPDuration constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(LeaveWithoutPayRequestDto request, ConstraintValidatorContext context) {
        if (request == null || request.getLeaveStartDate() == null || request.getLeaveEndDate() == null) {
            return false;
        }

        LocalDate startDate = request.getLeaveStartDate();
        LocalDate endDate = request.getLeaveEndDate();
        
        // Check if end date is after start date
        if (!endDate.isAfter(startDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Leave end date must be after start date")
                    .addPropertyNode("leaveEndDate")
                    .addConstraintViolation();
            return false;
        }

        // Calculate the duration between start and end dates
        Period period = Period.between(startDate, endDate);
        
        // Convert to total months for easier calculation
        int totalMonths = period.getYears() * 12 + period.getMonths();
        
        // Add days as a fraction of a month (assuming 30 days per month)
        double fractionalMonth = period.getDays() / 30.0;
        double totalDurationInMonths = totalMonths + fractionalMonth;
        
        // Check if duration is between 1 month and 3 years (36 months)
        if (totalDurationInMonths < 1.0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("LWOP duration must be at least 1 month")
                    .addPropertyNode("leaveEndDate")
                    .addConstraintViolation();
            return false;
        }
        
        if (totalDurationInMonths > 36.0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("LWOP duration cannot exceed 3 years (36 months)")
                    .addPropertyNode("leaveEndDate")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}