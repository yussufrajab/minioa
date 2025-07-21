package com.zanzibar.csms.dto.retirement;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RetirementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetirementRequestResponseDto {

    private String id;
    private String requestNumber;
    private EmployeeBasicDto employee;
    private UserBasicDto submittedBy;
    private LocalDateTime submissionDate;
    private RequestStatus status;
    private UserBasicDto approver;
    private LocalDateTime approvalDate;
    private String comments;

    // Retirement specific fields
    private RetirementType retirementType;
    private String retirementTypeDisplay;
    private LocalDate retirementDate;
    private LocalDate lastWorkingDate;
    private Boolean pensionEligibilityConfirmed;
    private Boolean clearanceCompleted;

    // Document information
    private List<RetirementDocumentDto> documents;
    private boolean hasRequiredDocuments;
    private List<String> missingDocuments;

    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isPending() {
        return status != null && status.isPending();
    }

    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }

    public boolean isCompulsoryRetirement() {
        return retirementType == RetirementType.COMPULSORY;
    }

    public boolean isVoluntaryRetirement() {
        return retirementType == RetirementType.VOLUNTARY;
    }

    public boolean isMedicalRetirement() {
        return retirementType == RetirementType.ILLNESS;
    }

    public String getStatusDisplay() {
        return status != null ? status.getDescription() : "Unknown";
    }

    public boolean requiresHealthDocuments() {
        return retirementType != null && retirementType.requiresHealthDocuments();
    }

    public boolean isPensionEligible() {
        return Boolean.TRUE.equals(pensionEligibilityConfirmed);
    }

    public boolean isClearanceComplete() {
        return Boolean.TRUE.equals(clearanceCompleted);
    }
}