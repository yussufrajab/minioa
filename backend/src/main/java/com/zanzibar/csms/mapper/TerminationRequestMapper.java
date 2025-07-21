package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.termination.TerminationDocumentDto;
import com.zanzibar.csms.dto.termination.TerminationRequestResponseDto;
import com.zanzibar.csms.entity.TerminationDocument;
import com.zanzibar.csms.entity.TerminationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TerminationRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public TerminationRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public TerminationRequestResponseDto toResponseDto(TerminationRequest terminationRequest) {
        if (terminationRequest == null) {
            return null;
        }

        return TerminationRequestResponseDto.builder()
            .id(terminationRequest.getId())
            .requestNumber(terminationRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(terminationRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(terminationRequest.getSubmittedBy()))
            .submissionDate(terminationRequest.getSubmissionDate())
            .status(terminationRequest.getStatus())
            .approver(terminationRequest.getApprover() != null ? userMapper.toBasicDto(terminationRequest.getApprover()) : null)
            .approvalDate(terminationRequest.getApprovalDate())
            .comments(terminationRequest.getComments())
            .scenario(terminationRequest.getScenario())
            .reason(terminationRequest.getReason())
            .incidentDate(terminationRequest.getIncidentDate())
            .probationEndDate(terminationRequest.getProbationEndDate())
            .investigationSummary(terminationRequest.getInvestigationSummary())
            .priorWarningsCount(terminationRequest.getPriorWarningsCount())
            .disciplinaryActions(terminationRequest.getDisciplinaryActions())
            .hrRecommendations(terminationRequest.getHrRecommendations())
            .documents(toDocumentDtos(terminationRequest.getTerminationDocuments()))
            .createdAt(terminationRequest.getCreatedAt())
            .updatedAt(terminationRequest.getUpdatedAt())
            .build();
    }

    public List<TerminationRequestResponseDto> toResponseDtos(List<TerminationRequest> terminationRequests) {
        if (terminationRequests == null) {
            return List.of();
        }

        return terminationRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public TerminationDocumentDto toDocumentDto(TerminationDocument document) {
        if (document == null) {
            return null;
        }

        return TerminationDocumentDto.builder()
            .id(document.getId())
            .documentType(document.getDocumentType())
            .fileName(document.getFileName())
            .filePath(document.getFilePath())
            .fileSize(document.getFileSize())
            .contentType(document.getContentType())
            .description(document.getDescription())
            .isMandatory(document.getIsMandatory())
            .createdAt(document.getCreatedAt())
            .build();
    }

    public List<TerminationDocumentDto> toDocumentDtos(List<TerminationDocument> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
}