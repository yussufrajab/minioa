package com.zanzibar.csms.dto.complaint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAppealDto {

    @NotNull(message = "Original complaint ID is required")
    private Long originalComplaintId;

    @NotNull(message = "Appellant ID is required")
    private Long appellantId;

    @NotBlank(message = "Appeal reason is required")
    private String appealReason;

    @NotBlank(message = "Grounds for appeal is required")
    private String groundsForAppeal;

    private String newEvidence;
}