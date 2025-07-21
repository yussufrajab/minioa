package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.Priority;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Inheritance(strategy = InheritanceType.JOINED)
public class Request extends BaseEntity {

    @Column(name = "request_number", unique = true, nullable = false)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority = Priority.NORMAL;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "current_stage")
    private String currentStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_reviewer_id")
    private User currentReviewer;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestWorkflow> workflowSteps = new ArrayList<>();

    // Revision tracking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_request_id")
    private Request originalRequest;

    @OneToMany(mappedBy = "originalRequest", fetch = FetchType.LAZY)
    private List<Request> revisions = new ArrayList<>();

    @Column(name = "revision_number")
    private Integer revisionNumber = 0;

    @Column(name = "is_revised")
    private Boolean isRevised = false;

    @Column(name = "revision_reason", columnDefinition = "TEXT")
    private String revisionReason;

    @Column(name = "revised_at")
    private LocalDateTime revisedAt;

    @PrePersist
    protected void onCreate() {
        this.submissionDate = LocalDateTime.now();
        this.status = RequestStatus.DRAFT;
    }

    // Helper methods for revision tracking
    public boolean hasRevisions() {
        return !revisions.isEmpty();
    }

    public boolean isRevision() {
        return originalRequest != null;
    }

    public Request getLatestRevision() {
        if (revisions.isEmpty()) {
            return this;
        }
        return revisions.stream()
            .max((r1, r2) -> r1.getRevisionNumber().compareTo(r2.getRevisionNumber()))
            .orElse(this);
    }
}