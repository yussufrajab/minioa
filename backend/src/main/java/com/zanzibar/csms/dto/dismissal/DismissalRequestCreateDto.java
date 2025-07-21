package com.zanzibar.csms.dto.dismissal;

import com.zanzibar.csms.entity.enums.DismissalReason;
import com.zanzibar.csms.validation.ValidDismissalRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidDismissalRequest
public class DismissalRequestCreateDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Dismissal reason is required")
    private DismissalReason dismissalReason;

    @NotBlank(message = "Detailed charges are required")
    @Size(min = 50, max = 5000, message = "Detailed charges must be between 50 and 5000 characters")
    private String detailedCharges;

    private LocalDate incidentDate;

    private LocalDate investigationStartDate;

    private LocalDate investigationEndDate;

    @Size(max = 3000, message = "Investigation summary cannot exceed 3000 characters")
    private String investigationSummary;

    @Size(max = 200, message = "Investigation officer name cannot exceed 200 characters")
    private String investigationOfficer;

    @Size(max = 2000, message = "Disciplinary history cannot exceed 2000 characters")
    private String disciplinaryHistory;

    @Builder.Default
    private Integer priorWarningsCount = 0;

    private LocalDateTime showCauseDate;

    @Size(max = 2000, message = "Employee response cannot exceed 2000 characters")
    private String employeeResponse;

    private LocalDateTime hearingDate;

    @Size(max = 2000, message = "Hearing outcome cannot exceed 2000 characters")
    private String hearingOutcome;

    @Size(max = 1500, message = "Mitigating factors cannot exceed 1500 characters")
    private String mitigatingFactors;

    @Size(max = 1500, message = "Aggravating factors cannot exceed 1500 characters")
    private String aggravatingFactors;

    @Size(max = 2000, message = "HR recommendations cannot exceed 2000 characters")
    private String hrRecommendations;

    @Builder.Default
    private Boolean legalConsultation = false;

    @Size(max = 2000, message = "Legal advice cannot exceed 2000 characters")
    private String legalAdvice;

    private LocalDateTime unionNotificationDate;

    @Size(max = 1000, message = "Union response cannot exceed 1000 characters")
    private String unionResponse;

    private Double finalSettlementAmount;

    private LocalDate effectiveDismissalDate;

    @Builder.Default
    private List<String> documentIds = List.of();
}