package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_workflow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestWorkflow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_role", nullable = false)
    private UserRole requiredRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.SUBMITTED;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "is_current_step")
    private Boolean isCurrentStep = false;

    @Column(name = "days_in_step")
    private Integer daysInStep;

    @PrePersist
    protected void onCreate() {
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
    }

    public void complete(User reviewer, String comments, RequestStatus status) {
        this.reviewer = reviewer;
        this.comments = comments;
        this.status = status;
        this.completionDate = LocalDateTime.now();
        this.isCurrentStep = false;
        this.daysInStep = (int) java.time.Duration.between(startDate, completionDate).toDays();
    }

    public void activate() {
        this.isCurrentStep = true;
        this.startDate = LocalDateTime.now();
        this.status = RequestStatus.SUBMITTED;
    }
}