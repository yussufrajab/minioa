package com.zanzibar.csms.dto;

import com.zanzibar.csms.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private String institutionId;
    private String institutionName;
    private boolean isEnabled;
    private LocalDateTime lastLoginDate;
    private LocalDateTime createdAt;
}