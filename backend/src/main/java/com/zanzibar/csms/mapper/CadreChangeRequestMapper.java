package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.cadre.CadreChangeDocumentDto;
import com.zanzibar.csms.dto.cadre.CadreChangeRequestResponseDto;
import com.zanzibar.csms.entity.CadreChangeDocument;
import com.zanzibar.csms.entity.CadreChangeRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CadreChangeRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public CadreChangeRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public CadreChangeRequestResponseDto toResponseDto(CadreChangeRequest cadreChangeRequest) {
        if (cadreChangeRequest == null) {
            return null;
        }

        return CadreChangeRequestResponseDto.builder()
            .id(cadreChangeRequest.getId())
            .requestNumber(cadreChangeRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(cadreChangeRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(cadreChangeRequest.getSubmittedBy()))
            .submissionDate(cadreChangeRequest.getSubmissionDate())
            .status(cadreChangeRequest.getStatus())
            .approver(cadreChangeRequest.getApprover() != null ? userMapper.toBasicDto(cadreChangeRequest.getApprover()) : null)
            .approvalDate(cadreChangeRequest.getApprovalDate())
            .comments(cadreChangeRequest.getComments())
            .currentCadre(cadreChangeRequest.getCurrentCadre())
            .proposedCadre(cadreChangeRequest.getProposedCadre())
            .educationLevel(cadreChangeRequest.getEducationLevel())
            .educationCompletionYear(cadreChangeRequest.getEducationCompletionYear())
            .institutionAttended(cadreChangeRequest.getInstitutionAttended())
            .qualificationObtained(cadreChangeRequest.getQualificationObtained())
            .justification(cadreChangeRequest.getJustification())
            .currentSalaryScale(cadreChangeRequest.getCurrentSalaryScale())
            .proposedSalaryScale(cadreChangeRequest.getProposedSalaryScale())
            .yearsOfExperience(cadreChangeRequest.getYearsOfExperience())
            .relevantExperience(cadreChangeRequest.getRelevantExperience())
            .trainingCompleted(cadreChangeRequest.getTrainingCompleted())
            .skillsAcquired(cadreChangeRequest.getSkillsAcquired())
            .performanceRating(cadreChangeRequest.getPerformanceRating())
            .supervisorRecommendation(cadreChangeRequest.getSupervisorRecommendation())
            .effectiveDate(cadreChangeRequest.getEffectiveDate())
            .tcuVerificationRequired(cadreChangeRequest.getTcuVerificationRequired())
            .tcuVerificationStatus(cadreChangeRequest.getTcuVerificationStatus())
            .tcuVerificationDate(cadreChangeRequest.getTcuVerificationDate())
            .hrAssessment(cadreChangeRequest.getHrAssessment())
            .budgetaryImplications(cadreChangeRequest.getBudgetaryImplications())
            .educationVerificationRequired(cadreChangeRequest.isEducationVerificationRequired())
            .educationVerified(cadreChangeRequest.isEducationVerified())
            .hasRequiredDocuments(cadreChangeRequest.hasRequiredDocuments())
            .eligibleForProcessing(cadreChangeRequest.isEligibleForProcessing())
            .yearsSinceEducationCompletion(cadreChangeRequest.getYearsSinceEducationCompletion())
            .documents(toDocumentDtos(cadreChangeRequest.getCadreChangeDocuments()))
            .createdAt(cadreChangeRequest.getCreatedAt())
            .updatedAt(cadreChangeRequest.getUpdatedAt())
            .build();
    }

    public List<CadreChangeRequestResponseDto> toResponseDtos(List<CadreChangeRequest> cadreChangeRequests) {
        if (cadreChangeRequests == null) {
            return List.of();
        }

        return cadreChangeRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public CadreChangeDocumentDto toDocumentDto(CadreChangeDocument document) {
        if (document == null) {
            return null;
        }

        return CadreChangeDocumentDto.builder()
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

    public List<CadreChangeDocumentDto> toDocumentDtos(List<CadreChangeDocument> documents) {
        if (documents == null) {
            return List.of();
        }

        return documents.stream()
            .map(this::toDocumentDto)
            .collect(Collectors.toList());
    }
}