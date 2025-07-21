package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.dismissal.DismissalDocumentDto;
import com.zanzibar.csms.dto.dismissal.DismissalRequestResponseDto;
import com.zanzibar.csms.entity.DismissalDocument;
import com.zanzibar.csms.entity.DismissalRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DismissalRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public DismissalRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public DismissalRequestResponseDto toResponseDto(DismissalRequest dismissalRequest) {
        if (dismissalRequest == null) {
            return null;
        }

        return DismissalRequestResponseDto.builder()
            .id(dismissalRequest.getId())
            .requestNumber(dismissalRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(dismissalRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(dismissalRequest.getSubmittedBy()))
            .submissionDate(dismissalRequest.getSubmissionDate())
            .status(dismissalRequest.getStatus())
            .approver(dismissalRequest.getApprover() != null ? userMapper.toBasicDto(dismissalRequest.getApprover()) : null)
            .approvalDate(dismissalRequest.getApprovalDate())
            .comments(dismissalRequest.getComments())
            .dismissalReason(dismissalRequest.getDismissalReason())
            .detailedCharges(dismissalRequest.getDetailedCharges())
            .incidentDate(dismissalRequest.getIncidentDate())
            .investigationStartDate(dismissalRequest.getInvestigationStartDate())
            .investigationEndDate(dismissalRequest.getInvestigationEndDate())
            .investigationSummary(dismissalRequest.getInvestigationSummary())
            .investigationOfficer(dismissalRequest.getInvestigationOfficer())
            .disciplinaryHistory(dismissalRequest.getDisciplinaryHistory())
            .priorWarningsCount(dismissalRequest.getPriorWarningsCount())
            .showCauseDate(dismissalRequest.getShowCauseDate())
            .employeeResponse(dismissalRequest.getEmployeeResponse())
            .hearingDate(dismissalRequest.getHearingDate())
            .hearingOutcome(dismissalRequest.getHearingOutcome())
            .mitigatingFactors(dismissalRequest.getMitigatingFactors())
            .aggravatingFactors(dismissalRequest.getAggravatingFactors())
            .hrRecommendations(dismissalRequest.getHrRecommendations())
            .legalConsultation(dismissalRequest.getLegalConsultation())
            .legalAdvice(dismissalRequest.getLegalAdvice())
            .unionNotificationDate(dismissalRequest.getUnionNotificationDate())
            .unionResponse(dismissalRequest.getUnionResponse())
            .appealPeriodExpires(dismissalRequest.getAppealPeriodExpires())
            .finalSettlementAmount(dismissalRequest.getFinalSettlementAmount())
            .effectiveDismissalDate(dismissalRequest.getEffectiveDismissalDate())
            .investigationRequired(dismissalRequest.isInvestigationRequired())
            .priorWarningsRequired(dismissalRequest.arePriorWarningsRequired())
            .appealPeriodActive(dismissalRequest.isAppealPeriodActive())
            .daysUntilAppealExpires(dismissalRequest.getDaysUntilAppealExpires())
            .documents(toDocumentDtos(dismissalRequest.getDismissalDocuments()))
            .createdAt(dismissalRequest.getCreatedAt())
            .updatedAt(dismissalRequest.getUpdatedAt())
            .build();
    }

    public List<DismissalRequestResponseDto> toResponseDtos(List<DismissalRequest> dismissalRequests) {
        if (dismissalRequests == null) {
            return List.of();
        }

        return dismissalRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public DismissalDocumentDto toDocumentDto(DismissalDocument document) {
        if (document == null) {
            return null;
        }

        return DismissalDocumentDto.builder()
            .id(document.getId())
            .documentType(document.getDocumentType())
            .fileName(document.getFileName())
            .filePath(document.getFilePath())
            .fileSize(document.getFileSize())
            .contentType(document.getContentType())
            .description(document.getDescription())
            .isMandatory(document.getIsMandatory())
            .confidentialityLevel(document.getConfidentialityLevel())
            .uploadedBy(document.getUploadedBy() != null ? userMapper.toBasicDto(document.getUploadedBy()) : null)
            .createdAt(document.getCreatedAt())
            .build();
    }

    public List<DismissalDocumentDto> toDocumentDtos(List<DismissalDocument> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
}