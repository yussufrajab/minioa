package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.LeaveWithoutPayRequestDto;
import com.zanzibar.csms.entity.LeaveWithoutPay;
import org.springframework.stereotype.Component;

@Component
public class LeaveWithoutPayMapper {

    public LeaveWithoutPay toEntity(LeaveWithoutPayRequestDto dto) {
        if (dto == null) {
            return null;
        }

        LeaveWithoutPay entity = new LeaveWithoutPay();
        entity.setReason(dto.getReason());
        entity.setLeaveStartDate(dto.getLeaveStartDate());
        entity.setLeaveEndDate(dto.getLeaveEndDate());
        entity.setHasLoanGuarantee(dto.getHasLoanGuarantee());
        
        return entity;
    }

    public LeaveWithoutPayRequestDto toDto(LeaveWithoutPay entity) {
        if (entity == null) {
            return null;
        }

        LeaveWithoutPayRequestDto dto = new LeaveWithoutPayRequestDto();
        dto.setEmployeeId(entity.getEmployee() != null ? entity.getEmployee().getId() : null);
        dto.setReason(entity.getReason());
        dto.setLeaveStartDate(entity.getLeaveStartDate());
        dto.setLeaveEndDate(entity.getLeaveEndDate());
        dto.setHasLoanGuarantee(entity.getHasLoanGuarantee());
        
        return dto;
    }
}