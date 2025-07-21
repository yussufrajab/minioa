package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.DismissalReason;
import com.zanzibar.csms.entity.enums.Priority;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dismissal_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DismissalRequest extends Request {

    @Enumerated(EnumType.STRING)
    @Column(name = "dismissal_reason", nullable = false)
    private DismissalReason dismissalReason;

    @Column(name = "detailed_charges", columnDefinition = "TEXT", nullable = false)
    private String detailedCharges;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "investigation_start_date")
    private LocalDate investigationStartDate;

    @Column(name = "investigation_end_date")
    private LocalDate investigationEndDate;

    @Column(name = "investigation_summary", columnDefinition = "TEXT")
    private String investigationSummary;

    @Column(name = "investigation_officer")
    private String investigationOfficer;

    @Column(name = "disciplinary_history", columnDefinition = "TEXT")
    private String disciplinaryHistory;

    @Column(name = "prior_warnings_count")
    private Integer priorWarningsCount = 0;

    @Column(name = "show_cause_date")
    private LocalDateTime showCauseDate;

    @Column(name = "employee_response", columnDefinition = "TEXT")
    private String employeeResponse;

    @Column(name = "hearing_date")
    private LocalDateTime hearingDate;

    @Column(name = "hearing_outcome", columnDefinition = "TEXT")
    private String hearingOutcome;

    @Column(name = "mitigating_factors", columnDefinition = "TEXT")
    private String mitigatingFactors;

    @Column(name = "aggravating_factors", columnDefinition = "TEXT")
    private String aggravatingFactors;

    @Column(name = "hr_recommendations", columnDefinition = "TEXT")
    private String hrRecommendations;

    @Column(name = "legal_consultation")
    private Boolean legalConsultation = false;

    @Column(name = "legal_advice", columnDefinition = "TEXT")
    private String legalAdvice;

    @Column(name = "union_notification_date")
    private LocalDateTime unionNotificationDate;

    @Column(name = "union_response", columnDefinition = "TEXT")
    private String unionResponse;

    @Column(name = "appeal_period_expires")
    private LocalDateTime appealPeriodExpires;

    @Column(name = "final_settlement_amount")
    private Double finalSettlementAmount;

    @Column(name = "effective_dismissal_date")
    private LocalDate effectiveDismissalDate;

    @OneToMany(mappedBy = "dismissalRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DismissalDocument> dismissalDocuments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.getRequestType() == null) {
            this.setRequestType(RequestType.DISMISSAL);
        }
        
        // Set appeal period to 30 days from submission
        if (this.appealPeriodExpires == null) {
            this.appealPeriodExpires = LocalDateTime.now().plusDays(30);
        }
    }

    public void addDocument(DismissalDocument document) {
        dismissalDocuments.add(document);
        document.setDismissalRequest(this);
    }

    public void removeDocument(DismissalDocument document) {
        dismissalDocuments.remove(document);
        document.setDismissalRequest(null);
    }

    public boolean isInvestigationRequired() {
        return dismissalReason != null && dismissalReason.requiresInvestigation();
    }

    public boolean arePriorWarningsRequired() {
        return dismissalReason != null && dismissalReason.requiresPriorWarnings();
    }

    public boolean isAppealPeriodActive() {
        return appealPeriodExpires != null && LocalDateTime.now().isBefore(appealPeriodExpires);
    }

    public long getDaysUntilAppealExpires() {
        if (appealPeriodExpires == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), appealPeriodExpires).toDays();
    }

    // Custom builder pattern
    public static DismissalRequestBuilder dismissalBuilder() {
        return new DismissalRequestBuilder();
    }

    public static class DismissalRequestBuilder {
        private String requestNumber;
        private Employee employee;
        private User submittedBy;
        private LocalDateTime submissionDate;
        private RequestStatus status;
        private RequestType requestType;
        private User approver;
        private LocalDateTime approvalDate;
        private String comments;
        private String rejectionReason;
        private Priority priority;
        private LocalDateTime dueDate;
        private String description;
        private String currentStage;
        private User currentReviewer;
        private List<RequestDocument> documents;
        private List<RequestWorkflow> workflowSteps;
        
        private DismissalReason dismissalReason;
        private String detailedCharges;
        private LocalDate incidentDate;
        private LocalDate investigationStartDate;
        private LocalDate investigationEndDate;
        private String investigationSummary;
        private String investigationOfficer;
        private String disciplinaryHistory;
        private Integer priorWarningsCount;
        private LocalDateTime showCauseDate;
        private String employeeResponse;
        private LocalDateTime hearingDate;
        private String hearingOutcome;
        private String mitigatingFactors;
        private String aggravatingFactors;
        private String hrRecommendations;
        private Boolean legalConsultation;
        private String legalAdvice;
        private LocalDateTime unionNotificationDate;
        private String unionResponse;
        private LocalDateTime appealPeriodExpires;
        private Double finalSettlementAmount;
        private LocalDate effectiveDismissalDate;
        private List<DismissalDocument> dismissalDocuments;

        public DismissalRequestBuilder requestNumber(String requestNumber) {
            this.requestNumber = requestNumber;
            return this;
        }

        public DismissalRequestBuilder employee(Employee employee) {
            this.employee = employee;
            return this;
        }

        public DismissalRequestBuilder submittedBy(User submittedBy) {
            this.submittedBy = submittedBy;
            return this;
        }

        public DismissalRequestBuilder submissionDate(LocalDateTime submissionDate) {
            this.submissionDate = submissionDate;
            return this;
        }

        public DismissalRequestBuilder status(RequestStatus status) {
            this.status = status;
            return this;
        }

        public DismissalRequestBuilder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public DismissalRequestBuilder approver(User approver) {
            this.approver = approver;
            return this;
        }

        public DismissalRequestBuilder approvalDate(LocalDateTime approvalDate) {
            this.approvalDate = approvalDate;
            return this;
        }

        public DismissalRequestBuilder comments(String comments) {
            this.comments = comments;
            return this;
        }

        public DismissalRequestBuilder rejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
            return this;
        }

        public DismissalRequestBuilder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public DismissalRequestBuilder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public DismissalRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DismissalRequestBuilder currentStage(String currentStage) {
            this.currentStage = currentStage;
            return this;
        }

        public DismissalRequestBuilder currentReviewer(User currentReviewer) {
            this.currentReviewer = currentReviewer;
            return this;
        }

        public DismissalRequestBuilder documents(List<RequestDocument> documents) {
            this.documents = documents;
            return this;
        }

        public DismissalRequestBuilder workflowSteps(List<RequestWorkflow> workflowSteps) {
            this.workflowSteps = workflowSteps;
            return this;
        }

        public DismissalRequestBuilder dismissalReason(DismissalReason dismissalReason) {
            this.dismissalReason = dismissalReason;
            return this;
        }

        public DismissalRequestBuilder detailedCharges(String detailedCharges) {
            this.detailedCharges = detailedCharges;
            return this;
        }

        public DismissalRequestBuilder incidentDate(LocalDate incidentDate) {
            this.incidentDate = incidentDate;
            return this;
        }

        public DismissalRequestBuilder investigationStartDate(LocalDate investigationStartDate) {
            this.investigationStartDate = investigationStartDate;
            return this;
        }

        public DismissalRequestBuilder investigationEndDate(LocalDate investigationEndDate) {
            this.investigationEndDate = investigationEndDate;
            return this;
        }

        public DismissalRequestBuilder investigationSummary(String investigationSummary) {
            this.investigationSummary = investigationSummary;
            return this;
        }

        public DismissalRequestBuilder investigationOfficer(String investigationOfficer) {
            this.investigationOfficer = investigationOfficer;
            return this;
        }

        public DismissalRequestBuilder disciplinaryHistory(String disciplinaryHistory) {
            this.disciplinaryHistory = disciplinaryHistory;
            return this;
        }

        public DismissalRequestBuilder priorWarningsCount(Integer priorWarningsCount) {
            this.priorWarningsCount = priorWarningsCount;
            return this;
        }

        public DismissalRequestBuilder showCauseDate(LocalDateTime showCauseDate) {
            this.showCauseDate = showCauseDate;
            return this;
        }

        public DismissalRequestBuilder employeeResponse(String employeeResponse) {
            this.employeeResponse = employeeResponse;
            return this;
        }

        public DismissalRequestBuilder hearingDate(LocalDateTime hearingDate) {
            this.hearingDate = hearingDate;
            return this;
        }

        public DismissalRequestBuilder hearingOutcome(String hearingOutcome) {
            this.hearingOutcome = hearingOutcome;
            return this;
        }

        public DismissalRequestBuilder mitigatingFactors(String mitigatingFactors) {
            this.mitigatingFactors = mitigatingFactors;
            return this;
        }

        public DismissalRequestBuilder aggravatingFactors(String aggravatingFactors) {
            this.aggravatingFactors = aggravatingFactors;
            return this;
        }

        public DismissalRequestBuilder hrRecommendations(String hrRecommendations) {
            this.hrRecommendations = hrRecommendations;
            return this;
        }

        public DismissalRequestBuilder legalConsultation(Boolean legalConsultation) {
            this.legalConsultation = legalConsultation;
            return this;
        }

        public DismissalRequestBuilder legalAdvice(String legalAdvice) {
            this.legalAdvice = legalAdvice;
            return this;
        }

        public DismissalRequestBuilder unionNotificationDate(LocalDateTime unionNotificationDate) {
            this.unionNotificationDate = unionNotificationDate;
            return this;
        }

        public DismissalRequestBuilder unionResponse(String unionResponse) {
            this.unionResponse = unionResponse;
            return this;
        }

        public DismissalRequestBuilder appealPeriodExpires(LocalDateTime appealPeriodExpires) {
            this.appealPeriodExpires = appealPeriodExpires;
            return this;
        }

        public DismissalRequestBuilder finalSettlementAmount(Double finalSettlementAmount) {
            this.finalSettlementAmount = finalSettlementAmount;
            return this;
        }

        public DismissalRequestBuilder effectiveDismissalDate(LocalDate effectiveDismissalDate) {
            this.effectiveDismissalDate = effectiveDismissalDate;
            return this;
        }

        public DismissalRequestBuilder dismissalDocuments(List<DismissalDocument> dismissalDocuments) {
            this.dismissalDocuments = dismissalDocuments;
            return this;
        }

        public DismissalRequest build() {
            DismissalRequest request = new DismissalRequest();
            
            // Set base class fields
            request.setRequestNumber(requestNumber);
            request.setEmployee(employee);
            request.setSubmittedBy(submittedBy);
            request.setSubmissionDate(submissionDate);
            request.setStatus(status);
            request.setRequestType(requestType != null ? requestType : RequestType.DISMISSAL);
            request.setApprover(approver);
            request.setApprovalDate(approvalDate);
            request.setComments(comments);
            request.setRejectionReason(rejectionReason);
            request.setPriority(priority);
            request.setDueDate(dueDate);
            request.setDescription(description);
            request.setCurrentStage(currentStage);
            request.setCurrentReviewer(currentReviewer);
            request.setDocuments(documents != null ? documents : new ArrayList<>());
            request.setWorkflowSteps(workflowSteps != null ? workflowSteps : new ArrayList<>());
            
            // Set dismissal-specific fields
            request.setDismissalReason(dismissalReason);
            request.setDetailedCharges(detailedCharges);
            request.setIncidentDate(incidentDate);
            request.setInvestigationStartDate(investigationStartDate);
            request.setInvestigationEndDate(investigationEndDate);
            request.setInvestigationSummary(investigationSummary);
            request.setInvestigationOfficer(investigationOfficer);
            request.setDisciplinaryHistory(disciplinaryHistory);
            request.setPriorWarningsCount(priorWarningsCount != null ? priorWarningsCount : 0);
            request.setShowCauseDate(showCauseDate);
            request.setEmployeeResponse(employeeResponse);
            request.setHearingDate(hearingDate);
            request.setHearingOutcome(hearingOutcome);
            request.setMitigatingFactors(mitigatingFactors);
            request.setAggravatingFactors(aggravatingFactors);
            request.setHrRecommendations(hrRecommendations);
            request.setLegalConsultation(legalConsultation != null ? legalConsultation : false);
            request.setLegalAdvice(legalAdvice);
            request.setUnionNotificationDate(unionNotificationDate);
            request.setUnionResponse(unionResponse);
            request.setAppealPeriodExpires(appealPeriodExpires);
            request.setFinalSettlementAmount(finalSettlementAmount);
            request.setEffectiveDismissalDate(effectiveDismissalDate);
            request.setDismissalDocuments(dismissalDocuments != null ? dismissalDocuments : new ArrayList<>());
            
            return request;
        }
    }
}