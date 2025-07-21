package com.zanzibar.csms.dto.user;

import com.zanzibar.csms.entity.enums.UserRole;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicDto {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private UserRole role;
    private String institutionName;
}