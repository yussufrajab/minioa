package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_delegations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDelegation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegator_id", nullable = false)
    private User delegator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_id", nullable = false)
    private User delegate;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private String revokedBy;

    @Column(name = "revocation_reason", columnDefinition = "TEXT")
    private String revocationReason;

    @Column(name = "extension_reason", columnDefinition = "TEXT")
    private String extensionReason;

    @Column(name = "extended_by")
    private String extendedBy;

    @Column(name = "extended_at")
    private LocalDateTime extendedAt;

    // Helper methods
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && 
               (now.isAfter(startDate) || now.isEqual(startDate)) && 
               (now.isBefore(endDate) || now.isEqual(endDate));
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }

    public boolean isScheduledForFuture() {
        return LocalDateTime.now().isBefore(startDate);
    }

    public long getDurationInHours() {
        return java.time.Duration.between(startDate, endDate).toHours();
    }

    public long getHoursRemaining() {
        if (!isCurrentlyActive()) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), endDate).toHours();
    }

    public boolean isExtended() {
        return extendedAt != null;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isEmergency() {
        return reason != null && reason.startsWith("EMERGENCY:");
    }
}