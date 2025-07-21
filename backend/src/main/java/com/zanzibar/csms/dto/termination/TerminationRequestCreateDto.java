package com.zanzibar.csms.dto.termination;

import com.zanzibar.csms.entity.enums.TerminationScenario;
import com.zanzibar.csms.validation.ValidTerminationRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidTerminationRequest
public class TerminationRequestCreateDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Termination scenario is required")
    private TerminationScenario scenario;

    @NotBlank(message = "Reason for termination is required")
    @Size(min = 10, max = 2000, message = "Reason must be between 10 and 2000 characters")
    private String reason;

    private LocalDate incidentDate;

    private LocalDate probationEndDate;

    @Size(max = 2000, message = "Investigation summary cannot exceed 2000 characters")
    private String investigationSummary;

    @Builder.Default
    private Integer priorWarningsCount = 0;

    @Size(max = 1000, message = "Disciplinary actions description cannot exceed 1000 characters")
    private String disciplinaryActions;

    @Size(max = 1000, message = "HR recommendations cannot exceed 1000 characters")
    private String hrRecommendations;

    @Builder.Default
    private List<String> documentIds = List.of();
}