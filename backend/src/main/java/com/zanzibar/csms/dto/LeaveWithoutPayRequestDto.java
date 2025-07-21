package com.zanzibar.csms.dto;

import com.zanzibar.csms.validation.ValidLWOPDuration;
import com.zanzibar.csms.validation.ValidLWOPReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidLWOPDuration
public class LeaveWithoutPayRequestDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Reason is required")
    @ValidLWOPReason
    private String reason;

    @NotNull(message = "Leave start date is required")
    private LocalDate leaveStartDate;

    @NotNull(message = "Leave end date is required")
    private LocalDate leaveEndDate;

    @NotNull(message = "Loan guarantee status confirmation is required")
    private Boolean hasLoanGuarantee;

    @NotNull(message = "Loan guarantee confirmation is required")
    private Boolean loanGuaranteeConfirmed;

    private String comments;
    
    private Integer priority;
}