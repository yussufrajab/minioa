package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.AuthRequest;
import com.zanzibar.csms.dto.AuthResponse;
import com.zanzibar.csms.dto.UserDto;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.exception.AuthenticationException;
import com.zanzibar.csms.repository.UserRepository;
import com.zanzibar.csms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    @Transactional
    public AuthResponse authenticateUser(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        try {
            if (!user.isEnabled()) {
                throw new AuthenticationException("User account is disabled");
            }

            if (!user.isAccountNonLocked()) {
                throw new AuthenticationException("User account is locked");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(user.getUsername());

            // Update user login info
            user.setLastLoginDate(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            userRepository.save(user);

            auditService.logAction(user.getId(), user.getUsername(), "LOGIN", "User", user.getId(), 
                    null, null, true, null);

            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .institutionId(user.getInstitution() != null ? user.getInstitution().getId() : null)
                    .institutionName(user.getInstitution() != null ? user.getInstitution().getName() : null)
                    .isEnabled(user.isEnabled())
                    .lastLoginDate(user.getLastLoginDate())
                    .createdAt(user.getCreatedAt())
                    .build();

            return AuthResponse.builder()
                    .token(jwt)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getExpirationTime(jwt))
                    .user(userDto)
                    .build();

        } catch (Exception e) {
            // Update failed login attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setAccountNonLocked(false);
            }
            userRepository.save(user);
            
            auditService.logAction(user.getId(), user.getUsername(), "LOGIN_FAILED", "User", user.getId(), 
                    null, null, false, e.getMessage());
            
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @Transactional
    public void logoutUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        auditService.logAction(user.getId(), user.getUsername(), "LOGOUT", "User", user.getId(), 
                null, null, true, null);

        SecurityContextHolder.clearContext();
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.getUsernameFromJWT(refreshToken);
            String newToken = tokenProvider.generateTokenFromUsername(username);
            String newRefreshToken = tokenProvider.generateRefreshToken(username);

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .institutionId(user.getInstitution() != null ? user.getInstitution().getId() : null)
                    .institutionName(user.getInstitution() != null ? user.getInstitution().getName() : null)
                    .isEnabled(user.isEnabled())
                    .lastLoginDate(user.getLastLoginDate())
                    .createdAt(user.getCreatedAt())
                    .build();

            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getExpirationTime(newToken))
                    .user(userDto)
                    .build();
        } else {
            throw new AuthenticationException("Invalid refresh token");
        }
    }
}