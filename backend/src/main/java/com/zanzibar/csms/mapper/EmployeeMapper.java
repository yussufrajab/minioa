package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.employee.EmployeeBasicDto;
import com.zanzibar.csms.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeBasicDto toBasicDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        return EmployeeBasicDto.builder()
            .id(employee.getId())
            .fullName(employee.getFullName())
            .payrollNumber(employee.getPayrollNumber())
            .zanzibarId(employee.getZanzibarId())
            .rank(employee.getRank())
            .institutionName(employee.getInstitution() != null ? employee.getInstitution().getName() : null)
            .department(employee.getDepartment())
            .build();
    }
}