package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_trackers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SLATracker extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_breached", nullable = false)
    private Boolean isBreached = false;

    @Column(name = "completed_within_sla")
    private Boolean completedWithinSLA;

    @Column(name = "escalation_level", nullable = false)
    private Integer escalationLevel = 0;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalated_to")
    private User escalatedTo;

    @Column(name = "extension_days")
    private Integer extensionDays = 0;

    @Column(name = "extension_justification", columnDefinition = "TEXT")
    private String extensionJustification;

    @Column(name = "extended_by")
    private String extendedBy;

    @Column(name = "extended_at")
    private LocalDateTime extendedAt;

    @Column(name = "warning_sent")
    private Boolean warningSent = false;

    @Column(name = "warning_sent_at")
    private LocalDateTime warningSentAt;

    // Helper methods
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate) && completedAt == null;
    }

    public boolean isApproachingDeadline() {
        LocalDateTime warningThreshold = dueDate.minusDays(2);
        return LocalDateTime.now().isAfter(warningThreshold) && completedAt == null;
    }

    public long getHoursRemaining() {
        if (completedAt != null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();
    }

    public long getHoursOverdue() {
        if (completedAt != null || !isOverdue()) {
            return 0;
        }
        return java.time.Duration.between(dueDate, LocalDateTime.now()).toHours();
    }
}