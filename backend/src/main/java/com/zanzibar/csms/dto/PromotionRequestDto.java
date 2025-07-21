package com.zanzibar.csms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PromotionRequestDto extends RequestDto {

    private String currentPosition;
    private String currentGrade;
    private String proposedPosition;
    private String proposedGrade;
    private Double currentSalary;
    private Double proposedSalary;
    private LocalDate effectiveDate;
    private String justification;
    private String performanceRating;
    private Integer yearsInCurrentPosition;
    private String qualificationsMet;
    private String supervisorRecommendation;
}