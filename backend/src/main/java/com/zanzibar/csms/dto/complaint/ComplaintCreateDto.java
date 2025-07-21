package com.zanzibar.csms.dto.complaint;

import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintCreateDto {

    @NotNull(message = "Complainant ID is required")
    private String complainantId;

    private String respondentId;

    @NotNull(message = "Complaint type is required")
    private ComplaintType complaintType;

    @NotNull(message = "Severity is required")
    private ComplaintSeverity severity;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private LocalDateTime incidentDate;

    private String incidentLocation;

    private Boolean isAnonymous = false;

    private Boolean isConfidential = true;

    private String witnessNames;

    private String evidenceDescription;
}