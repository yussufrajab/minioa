package com.zanzibar.csms.mapper;

import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserBasicDto toBasicDto(User user) {
        if (user == null) {
            return null;
        }

        return UserBasicDto.builder()
            .id(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .role(user.getRole())
            .institutionName(user.getInstitution() != null ? user.getInstitution().getName() : null)
            .build();
    }
}