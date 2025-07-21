package com.zanzibar.csms.dto.serviceextension;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceExtensionRequestCreateDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Extension duration in years is required")
    @Min(value = 1, message = "Extension duration must be at least 1 year")
    private Integer extensionDurationYears;
}