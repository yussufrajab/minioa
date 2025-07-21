package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.serviceextension.ServiceExtensionDocumentDto;
import com.zanzibar.csms.dto.serviceextension.ServiceExtensionRequestResponseDto;
import com.zanzibar.csms.entity.ServiceExtensionDocument;
import com.zanzibar.csms.entity.ServiceExtensionRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ServiceExtensionRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public ServiceExtensionRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public ServiceExtensionRequestResponseDto toResponseDto(ServiceExtensionRequest serviceExtensionRequest) {
        if (serviceExtensionRequest == null) {
            return null;
        }

        return ServiceExtensionRequestResponseDto.builder()
            .id(serviceExtensionRequest.getId())
            .requestNumber(serviceExtensionRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(serviceExtensionRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(serviceExtensionRequest.getSubmittedBy()))
            .submissionDate(serviceExtensionRequest.getSubmissionDate())
            .status(serviceExtensionRequest.getStatus())
            .approver(serviceExtensionRequest.getApprover() != null ? userMapper.toBasicDto(serviceExtensionRequest.getApprover()) : null)
            .approvalDate(serviceExtensionRequest.getApprovalDate())
            .comments(serviceExtensionRequest.getComments())
            .extensionDurationYears(serviceExtensionRequest.getExtensionDurationYears())
            .extensionDurationDisplay(serviceExtensionRequest.getExtensionDurationDisplay())
            .documents(toDocumentDtos(serviceExtensionRequest.getServiceExtensionDocuments()))
            .hasRequiredDocuments(serviceExtensionRequest.hasRequiredDocuments())
            .createdAt(serviceExtensionRequest.getCreatedAt())
            .updatedAt(serviceExtensionRequest.getUpdatedAt())
            .build();
    }

    public List<ServiceExtensionRequestResponseDto> toResponseDtos(List<ServiceExtensionRequest> serviceExtensionRequests) {
        if (serviceExtensionRequests == null) {
            return List.of();
        }

        return serviceExtensionRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public ServiceExtensionDocumentDto toDocumentDto(ServiceExtensionDocument document) {
        if (document == null) {
            return null;
        }

        return ServiceExtensionDocumentDto.builder()
            .id(document.getId())
            .documentType(document.getDocumentType())
            .fileName(document.getFileName())
            .filePath(document.getFilePath())
            .fileSize(document.getFileSize())
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

    public List<ServiceExtensionDocumentDto> toDocumentDtos(List<ServiceExtensionDocument> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
}