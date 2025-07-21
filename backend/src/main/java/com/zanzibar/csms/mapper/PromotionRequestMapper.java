package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.promotion.PromotionRequestResponseDto;
import com.zanzibar.csms.entity.PromotionRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PromotionRequestMapper {

    private final EmployeeMapper employeeMapper;
    private final UserMapper userMapper;

    public PromotionRequestMapper(EmployeeMapper employeeMapper, UserMapper userMapper) {
        this.employeeMapper = employeeMapper;
        this.userMapper = userMapper;
    }

    public PromotionRequestResponseDto toResponseDto(PromotionRequest promotionRequest) {
        if (promotionRequest == null) {
            return null;
        }

        return PromotionRequestResponseDto.builder()
            .id(promotionRequest.getId())
            .requestNumber(promotionRequest.getRequestNumber())
            .employee(employeeMapper.toBasicDto(promotionRequest.getEmployee()))
            .submittedBy(userMapper.toBasicDto(promotionRequest.getSubmittedBy()))
            .submissionDate(promotionRequest.getSubmissionDate())
            .status(promotionRequest.getStatus())
            .approver(promotionRequest.getApprover() != null ? userMapper.toBasicDto(promotionRequest.getApprover()) : null)
            .approvalDate(promotionRequest.getApprovalDate())
            .comments(promotionRequest.getComments())
            .promotionType(promotionRequest.getPromotionType())
            .promotionTypeDisplay(promotionRequest.getPromotionTypeDisplay())
            .currentPosition(promotionRequest.getCurrentPosition())
            .currentGrade(promotionRequest.getCurrentGrade())
            .proposedPosition(promotionRequest.getProposedPosition())
            .proposedGrade(promotionRequest.getProposedGrade())
            .currentSalary(promotionRequest.getCurrentSalary())
            .proposedSalary(promotionRequest.getProposedSalary())
            .effectiveDate(promotionRequest.getEffectiveDate())
            .justification(promotionRequest.getJustification())
            .performanceRating(promotionRequest.getPerformanceRating())
            .yearsInCurrentPosition(promotionRequest.getYearsInCurrentPosition())
            .qualificationsMet(promotionRequest.getQualificationsMet())
            .supervisorRecommendation(promotionRequest.getSupervisorRecommendation())
            .createdAt(promotionRequest.getCreatedAt())
            .updatedAt(promotionRequest.getUpdatedAt())
            .build();
    }

    public List<PromotionRequestResponseDto> toResponseDtos(List<PromotionRequest> promotionRequests) {
        if (promotionRequests == null) {
            return List.of();
        }

        return promotionRequests.stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }
}