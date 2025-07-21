package com.zanzibar.csms.dto;

import com.zanzibar.csms.entity.enums.EmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    
    private String id;
    private String fullName;
    private String profileImage;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String region;
    private String countryOfBirth;
    private String payrollNumber;
    private String zanzibarId;
    private String zssfNumber;
    private String rank;
    private String ministry;
    private String institutionId;
    private String institutionName;
    private String department;
    private String appointmentType;
    private String contractType;
    private LocalDate recentTitleDate;
    private String currentReportingOffice;
    private String currentWorkplace;
    private LocalDate employmentDate;
    private LocalDate confirmationDate;
    private Boolean loanGuaranteeStatus;
    private EmploymentStatus employmentStatus;
    private String phoneNumber;
    private String gender;
    private String contactAddress;
}