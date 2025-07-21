package com.zanzibar.csms.dto.complaint;

import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import com.zanzibar.csms.entity.enums.ComplaintType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintResponseDto {

    private String id;
    private String complaintNumber;
    private String complainantId;
    private String complainantName;
    private String respondentId;
    private String respondentName;
    private String submittedById;
    private String submittedByName;
    private ComplaintType complaintType;
    private ComplaintStatus status;
    private ComplaintSeverity severity;
    private String title;
    private String description;
    private LocalDateTime incidentDate;
    private String incidentLocation;
    private LocalDateTime submissionDate;
    private LocalDateTime acknowledgmentDate;
    private String assignedInvestigatorId;
    private String assignedInvestigatorName;
    private LocalDateTime investigationStartDate;
    private LocalDateTime targetResolutionDate;
    private LocalDateTime actualResolutionDate;
    private String resolutionSummary;
    private String disciplinaryAction;
    private Boolean isAnonymous;
    private Boolean isConfidential;
    private String witnessNames;
    private String evidenceDescription;
    private Integer complainantSatisfactionRating;
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private Boolean isOverdue;
    private Long daysToResolve;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}