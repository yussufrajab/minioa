package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.retirement.RetirementDocumentDto;
import com.zanzibar.csms.dto.retirement.RetirementRequestResponseDto;
import com.zanzibar.csms.entity.RetirementDocument;
import com.zanzibar.csms.entity.RetirementRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RetirementRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public RetirementRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public RetirementRequestResponseDto toResponseDto(RetirementRequest retirementRequest) {
        if (retirementRequest == null) {
            return null;
        }

        return RetirementRequestResponseDto.builder()
            .id(retirementRequest.getId())
            .requestNumber(retirementRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(retirementRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(retirementRequest.getSubmittedBy()))
            .submissionDate(retirementRequest.getSubmissionDate())
            .status(retirementRequest.getStatus())
            .approver(retirementRequest.getApprover() != null ? userMapper.toBasicDto(retirementRequest.getApprover()) : null)
            .approvalDate(retirementRequest.getApprovalDate())
            .comments(retirementRequest.getComments())
            .retirementType(retirementRequest.getRetirementType())
            .retirementTypeDisplay(retirementRequest.getRetirementTypeDisplay())
            .retirementDate(retirementRequest.getRetirementDate())
            .lastWorkingDate(retirementRequest.getLastWorkingDate())
            .pensionEligibilityConfirmed(retirementRequest.getPensionEligibilityConfirmed())
            .clearanceCompleted(retirementRequest.getClearanceCompleted())
            .documents(toDocumentDtos(retirementRequest.getRetirementDocuments()))
            .hasRequiredDocuments(retirementRequest.hasRequiredDocuments())
            .missingDocuments(retirementRequest.getMissingDocuments())
            .createdAt(retirementRequest.getCreatedAt())
            .updatedAt(retirementRequest.getUpdatedAt())
            .build();
    }

    public List<RetirementRequestResponseDto> toResponseDtos(List<RetirementRequest> retirementRequests) {
        if (retirementRequests == null) {
            return List.of();
        }

        return retirementRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public RetirementDocumentDto toDocumentDto(RetirementDocument document) {
        if (document == null) {
            return null;
        }

        return RetirementDocumentDto.builder()
            .id(document.getId())
            .documentType(document.getDocumentType())
            .documentTypeDisplay(document.getDisplayName())
            .fileName(document.getFileName())
            .filePath(document.getFilePath())
            .fileSize(document.getFileSize())
            .fileSizeDisplay(document.getFileSizeDisplay())
            .contentType(document.getContentType())
            .description(document.getDescription())
            .isMandatory(document.getIsMandatory())
            .isVerified(document.getIsVerified())
            .verificationNotes(document.getVerificationNotes())
            .uploadedBy(document.getUploadedBy() != null ? userMapper.toBasicDto(document.getUploadedBy()) : null)
            .verifiedBy(document.getVerifiedBy() != null ? userMapper.toBasicDto(document.getVerifiedBy()) : null)
            .createdAt(document.getCreatedAt())
            .build();
    }

    public List<RetirementDocumentDto> toDocumentDtos(List<RetirementDocument> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
}