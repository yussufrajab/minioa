package com.zanzibar.csms.dto.termination;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.TerminationScenario;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminationRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;
    private TerminationScenario scenario;
    private String reason;
    private LocalDate incidentDate;
    private LocalDate probationEndDate;
    private String investigationSummary;
    private Integer priorWarningsCount;
    private String disciplinaryActions;
    private String hrRecommendations;
    private List<TerminationDocumentDto> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}