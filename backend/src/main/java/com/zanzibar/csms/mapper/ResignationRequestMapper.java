package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.resignation.ResignationDocumentDto;
import com.zanzibar.csms.dto.resignation.ResignationRequestResponseDto;
import com.zanzibar.csms.entity.ResignationDocument;
import com.zanzibar.csms.entity.ResignationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResignationRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public ResignationRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public ResignationRequestResponseDto toResponseDto(ResignationRequest resignationRequest) {
        if (resignationRequest == null) {
            return null;
        }

        return ResignationRequestResponseDto.builder()
            .id(resignationRequest.getId())
            .requestNumber(resignationRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(resignationRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(resignationRequest.getSubmittedBy()))
            .submissionDate(resignationRequest.getSubmissionDate())
            .status(resignationRequest.getStatus())
            .approver(resignationRequest.getApprover() != null ? userMapper.toBasicDto(resignationRequest.getApprover()) : null)
            .approvalDate(resignationRequest.getApprovalDate())
            .comments(resignationRequest.getComments())
            .resignationType(resignationRequest.getResignationType())
            .resignationTypeDisplay(resignationRequest.getResignationTypeDisplay())
            .resignationDate(resignationRequest.getResignationDate())
            .lastWorkingDate(resignationRequest.getLastWorkingDate())
            .reason(resignationRequest.getReason())
            .paymentAmount(resignationRequest.getPaymentAmount())
            .paymentConfirmed(resignationRequest.getPaymentConfirmed())
            .clearanceCompleted(resignationRequest.getClearanceCompleted())
            .handoverCompleted(resignationRequest.getHandoverCompleted())
            .noticePeriodDays(resignationRequest.getNoticePeriodDays())
            .documents(toDocumentDtos(resignationRequest.getResignationDocuments()))
            .hasRequiredDocuments(resignationRequest.hasRequiredDocuments())
            .missingDocuments(resignationRequest.getMissingDocuments())
            .eligibleForProcessing(resignationRequest.isEligibleForProcessing())
            .createdAt(resignationRequest.getCreatedAt())
            .updatedAt(resignationRequest.getUpdatedAt())
            .build();
    }

    public List<ResignationRequestResponseDto> toResponseDtos(List<ResignationRequest> resignationRequests) {
        if (resignationRequests == null) {
            return List.of();
        }

        return resignationRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public ResignationDocumentDto toDocumentDto(ResignationDocument document) {
        if (document == null) {
            return null;
        }

        return ResignationDocumentDto.builder()
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

    public List<ResignationDocumentDto> toDocumentDtos(List<ResignationDocument> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
}