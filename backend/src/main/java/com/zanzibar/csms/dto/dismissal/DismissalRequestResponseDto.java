package com.zanzibar.csms.dto.dismissal;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.DismissalReason;
import com.zanzibar.csms.entity.enums.RequestStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DismissalRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;
    
    // Dismissal-specific fields
    private DismissalReason dismissalReason;
    private String detailedCharges;
    private LocalDate incidentDate;
    private LocalDate investigationStartDate;
    private LocalDate investigationEndDate;
    private String investigationSummary;
    private String investigationOfficer;
    private String disciplinaryHistory;
    private Integer priorWarningsCount;
    private LocalDateTime showCauseDate;
    private String employeeResponse;
    private LocalDateTime hearingDate;
    private String hearingOutcome;
    private String mitigatingFactors;
    private String aggravatingFactors;
    private String hrRecommendations;
    private Boolean legalConsultation;
    private String legalAdvice;
    private LocalDateTime unionNotificationDate;
    private String unionResponse;
    private LocalDateTime appealPeriodExpires;
    private Double finalSettlementAmount;
    private LocalDate effectiveDismissalDate;
    
    // Helper fields
    private boolean investigationRequired;
    private boolean priorWarningsRequired;
    private boolean appealPeriodActive;
    private Long daysUntilAppealExpires;
    
    private List<DismissalDocumentDto> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}