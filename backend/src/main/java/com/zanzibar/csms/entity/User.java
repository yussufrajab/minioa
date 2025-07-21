package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import com.zanzibar.csms.entity.enums.UserRole;

/**
 * User Entity - Modified to match Prisma database schema
 * Table name and column names updated to work with existing data
 */
@Entity
@Table(name = "\"User\"") // Case-sensitive table name to match Prisma
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role; // Using String to match Prisma, not enum

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "\"employeeId\"") // Quoted for case sensitivity
    private String employeeId;

    @Column(name = "\"institutionId\"", nullable = false) // Quoted for case sensitivity
    private String institutionId;

    @Column(name = "\"createdAt\"", nullable = false) // Quoted for case sensitivity
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"", nullable = false) // Quoted for case sensitivity
    private LocalDateTime updatedAt;

    // Additional fields for compatibility with existing services (stored in-memory only)
    // These fields don't exist in the Prisma database schema, so they are @Transient
    @Transient
    private Integer failedLoginAttempts = 0;

    @Transient
    private Boolean accountNonLocked = true;

    @Transient
    private LocalDateTime lastLoginDate;

    // Relationships - Lazy loading for performance
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"institutionId\"", insertable = false, updatable = false)
    private Institution institution;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"employeeId\"", insertable = false, updatable = false)
    private Employee employee;

    // Set timestamps before persist/update
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Spring Security UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return active;
    }

    // Removed duplicate method - implemented below in compatibility section

    @Override
    public boolean isCredentialsNonExpired() {
        return active;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // Helper methods for role checking
    public boolean hasRole(String roleName) {
        return this.role.equals(roleName);
    }

    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    public boolean isHRO() {
        return "HRO".equals(this.role);
    }

    public boolean isHHRMD() {
        return "HHRMD".equals(this.role);
    }

    public boolean isHRMO() {
        return "HRMO".equals(this.role);
    }

    public boolean isDO() {
        return "DO".equals(this.role);
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equals(this.role);
    }

    public boolean isPO() {
        return "PO".equals(this.role);
    }

    public boolean isCSCS() {
        return "CSCS".equals(this.role);
    }

    public boolean isHRRP() {
        return "HRRP".equals(this.role);
    }

    // ====================================
    // COMPATIBILITY METHODS
    // ====================================
    // These methods provide backward compatibility with services expecting old field names
    
    /**
     * Compatibility method for services expecting getFullName()
     * Maps to the Prisma 'name' field
     */
    public String getFullName() {
        return this.name;
    }
    
    /**
     * Compatibility method for services expecting setFullName()
     * Maps to the Prisma 'name' field
     */
    public void setFullName(String fullName) {
        this.name = fullName;
    }
    
    /**
     * Compatibility method for services expecting getEmail()
     * Since Prisma schema doesn't have email field, we return null or derive from username
     */
    public String getEmail() {
        // Option 1: Return null (services should handle this)
        return null;
        
        // Option 2: Derive from username (uncomment if needed)
        // return this.username.contains("@") ? this.username : this.username + "@csms.gov.tz";
    }
    
    /**
     * Compatibility method for services expecting setEmail()
     */
    public void setEmail(String email) {
        // Since Prisma schema doesn't have email field, we can either:
        // Option 1: Do nothing (ignore email setting)
        // Option 2: Store in username if it's an email format
        if (email != null && email.contains("@")) {
            this.username = email;
        }
    }
    
    /**
     * Compatibility method for services expecting getPhoneNumber()
     * Since Prisma schema doesn't have phone number in User table, return null
     */
    public String getPhoneNumber() {
        return null;
    }
    
    /**
     * Compatibility method for services expecting setPhoneNumber()
     */
    public void setPhoneNumber(String phoneNumber) {
        // Prisma schema doesn't have phone number in User table
        // This is likely stored in Employee table instead
    }
    
    /**
     * Compatibility method for services expecting getLastLoginDate()
     */
    public LocalDateTime getLastLoginDate() {
        return this.lastLoginDate;
    }
    
    /**
     * Compatibility method for services expecting setLastLoginDate()
     */
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * Compatibility method for services expecting getFailedLoginAttempts()
     */
    public Integer getFailedLoginAttempts() {
        return this.failedLoginAttempts != null ? this.failedLoginAttempts : 0;
    }
    
    /**
     * Compatibility method for services expecting setFailedLoginAttempts()
     */
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    /**
     * Compatibility method for services expecting isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked != null ? this.accountNonLocked : true;
    }
    
    /**
     * Compatibility method for services expecting setAccountNonLocked()
     */
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    
    /**
     * Get role as UserRole enum - primary method for services
     * This overrides the default getRole() to return enum instead of String
     */
    public UserRole getRole() {
        try {
            // Handle the EMPLOYEE vs EMP mapping
            if ("EMPLOYEE".equals(this.role)) {
                return UserRole.EMP; // Use EMP alias for compatibility
            }
            return UserRole.valueOf(this.role);
        } catch (IllegalArgumentException e) {
            // If role string doesn't match enum, return default or handle gracefully
            return UserRole.EMP; // Default to EMP/EMPLOYEE
        }
    }

    /**
     * Get role as String - for database and Prisma compatibility
     */
    public String getRoleString() {
        return this.role;
    }

    /**
     * Compatibility method for services expecting UserRole enum
     * Converts String role to UserRole enum
     */
    public UserRole getRoleEnum() {
        return getRole(); // Delegate to main getRole() method
    }
    
    /**
     * Set role using UserRole enum - primary method for services
     */
    public void setRole(UserRole roleEnum) {
        if (roleEnum != null) {
            // Map EMP back to EMPLOYEE for database storage
            if (roleEnum == UserRole.EMP) {
                this.role = "EMPLOYEE";
            } else {
                this.role = roleEnum.name();
            }
        } else {
            this.role = null;
        }
    }

    /**
     * Set role using String - for database and Prisma compatibility
     */
    public void setRoleString(String role) {
        this.role = role;
    }
    
    // Role constants for backward compatibility
    public static final String ADMIN = "ADMIN";
    public static final String HRO = "HRO";
    public static final String HHRMD = "HHRMD";
    public static final String HRMO = "HRMO";
    public static final String DO = "DO";
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String PO = "PO";
    public static final String CSCS = "CSCS";
    public static final String HRRP = "HRRP";
}