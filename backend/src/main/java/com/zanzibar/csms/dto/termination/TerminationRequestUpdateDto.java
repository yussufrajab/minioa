package com.zanzibar.csms.dto.termination;

import com.zanzibar.csms.entity.enums.TerminationScenario;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminationRequestUpdateDto {

    private TerminationScenario scenario;

    @Size(min = 10, max = 2000, message = "Reason must be between 10 and 2000 characters")
    private String reason;

    private LocalDate incidentDate;

    private LocalDate probationEndDate;

    @Size(max = 2000, message = "Investigation summary cannot exceed 2000 characters")
    private String investigationSummary;

    private Integer priorWarningsCount;

    @Size(max = 1000, message = "Disciplinary actions description cannot exceed 1000 characters")
    private String disciplinaryActions;

    @Size(max = 1000, message = "HR recommendations cannot exceed 1000 characters")
    private String hrRecommendations;

    private List<String> documentIds;
}