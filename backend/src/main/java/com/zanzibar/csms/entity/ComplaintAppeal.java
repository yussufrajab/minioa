package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.ComplaintStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaint_appeals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintAppeal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_complaint_id", nullable = false)
    private Complaint originalComplaint;

    @Column(name = "appeal_number", unique = true, nullable = false)
    private String appealNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appellant_id", nullable = false)
    private Employee appellant;

    @Column(name = "appeal_reason", columnDefinition = "TEXT", nullable = false)
    private String appealReason;

    @Column(name = "grounds_for_appeal", columnDefinition = "TEXT", nullable = false)
    private String groundsForAppeal;

    @Column(name = "new_evidence", columnDefinition = "TEXT")
    private String newEvidence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplaintStatus status = ComplaintStatus.SUBMITTED;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_reviewer_id")
    private User assignedReviewer;

    @Column(name = "review_start_date")
    private LocalDateTime reviewStartDate;

    @Column(name = "target_decision_date")
    private LocalDateTime targetDecisionDate;

    @Column(name = "actual_decision_date")
    private LocalDateTime actualDecisionDate;

    @Column(name = "decision_summary", columnDefinition = "TEXT")
    private String decisionSummary;

    @Column(name = "decision_rationale", columnDefinition = "TEXT")
    private String decisionRationale;

    @Column(name = "is_upheld")
    private Boolean isUpheld;

    @Column(name = "corrective_action", columnDefinition = "TEXT")
    private String correctiveAction;

    @OneToMany(mappedBy = "appeal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AppealEvidence> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "appeal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AppealActivity> activities = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.submissionDate == null) {
            this.submissionDate = LocalDateTime.now();
        }
        if (this.targetDecisionDate == null) {
            this.targetDecisionDate = LocalDateTime.now().plusDays(21);
        }
    }

    public boolean isOverdue() {
        return targetDecisionDate != null && 
               LocalDateTime.now().isAfter(targetDecisionDate) && 
               !status.isTerminal();
    }
}