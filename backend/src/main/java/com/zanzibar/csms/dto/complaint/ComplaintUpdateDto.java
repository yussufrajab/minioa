package com.zanzibar.csms.dto.complaint;

import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintUpdateDto {

    private ComplaintStatus status;
    private ComplaintSeverity severity;
    private String title;
    private String description;
    private LocalDateTime incidentDate;
    private String incidentLocation;
    private String assignedInvestigatorId;
    private LocalDateTime investigationStartDate;
    private LocalDateTime targetResolutionDate;
    private String resolutionSummary;
    private String disciplinaryAction;
    private String witnessNames;
    private String evidenceDescription;
    private String investigationNotes;
    private Integer complainantSatisfactionRating;
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
}