package com.zanzibar.csms.dto.employee;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeBasicDto {
    private String id;
    private String fullName;
    private String payrollNumber;
    private String zanzibarId;
    private String rank;
    private String institutionName;
    private String department;
}