package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import com.zanzibar.csms.entity.enums.ComplaintType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint extends BaseEntity {

    @Column(name = "complaint_number", unique = true, nullable = false)
    private String complaintNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complainant_id", nullable = false)
    private Employee complainant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_id")
    private Employee respondent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by")
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_type", nullable = false)
    private ComplaintType complaintType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplaintStatus status = ComplaintStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private ComplaintSeverity severity = ComplaintSeverity.MEDIUM;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "incident_date")
    private LocalDateTime incidentDate;

    @Column(name = "incident_location")
    private String incidentLocation;

    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @Column(name = "acknowledgment_date")
    private LocalDateTime acknowledgmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_investigator_id")
    private User assignedInvestigator;

    @Column(name = "investigation_start_date")
    private LocalDateTime investigationStartDate;

    @Column(name = "target_resolution_date")
    private LocalDateTime targetResolutionDate;

    @Column(name = "actual_resolution_date")
    private LocalDateTime actualResolutionDate;

    @Column(name = "resolution_summary", columnDefinition = "TEXT")
    private String resolutionSummary;

    @Column(name = "disciplinary_action", columnDefinition = "TEXT")
    private String disciplinaryAction;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(name = "is_confidential")
    private Boolean isConfidential = true;

    @Column(name = "witness_names", columnDefinition = "TEXT")
    private String witnessNames;

    @Column(name = "evidence_description", columnDefinition = "TEXT")
    private String evidenceDescription;

    @Column(name = "investigation_notes", columnDefinition = "TEXT")
    private String investigationNotes;

    @Column(name = "complainant_satisfaction_rating")
    private Integer complainantSatisfactionRating;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComplaintEvidence> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComplaintActivity> activities = new ArrayList<>();

    @OneToMany(mappedBy = "originalComplaint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ComplaintAppeal> appeals = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.submissionDate == null) {
            this.submissionDate = LocalDateTime.now();
        }
        if (this.targetResolutionDate == null && this.severity != null) {
            this.targetResolutionDate = LocalDateTime.now().plusDays(this.severity.getDaysToResolve());
        }
    }

    public boolean isOverdue() {
        return targetResolutionDate != null && 
               LocalDateTime.now().isAfter(targetResolutionDate) && 
               !status.isTerminal();
    }

    public long getDaysToResolve() {
        if (targetResolutionDate == null) return 0;
        if (actualResolutionDate != null) {
            return java.time.Duration.between(submissionDate, actualResolutionDate).toDays();
        }
        return java.time.Duration.between(submissionDate, LocalDateTime.now()).toDays();
    }
}