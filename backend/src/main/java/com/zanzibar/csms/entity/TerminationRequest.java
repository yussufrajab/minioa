package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.TerminationScenario;
import com.zanzibar.csms.entity.enums.RequestType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "termination_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TerminationRequest extends Request {

    @Enumerated(EnumType.STRING)
    @Column(name = "scenario", nullable = false)
    private TerminationScenario scenario;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "incident_date")
    private LocalDate incidentDate;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "investigation_summary", columnDefinition = "TEXT")
    private String investigationSummary;

    @Column(name = "prior_warnings_count")
    private Integer priorWarningsCount = 0;

    @Column(name = "disciplinary_actions", columnDefinition = "TEXT")
    private String disciplinaryActions;

    @Column(name = "hr_recommendations", columnDefinition = "TEXT")
    private String hrRecommendations;

    @OneToMany(mappedBy = "terminationRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TerminationDocument> terminationDocuments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.getRequestType() == null) {
            this.setRequestType(RequestType.TERMINATION);
        }
    }

    public void addDocument(TerminationDocument document) {
        terminationDocuments.add(document);
        document.setTerminationRequest(this);
    }

    public void removeDocument(TerminationDocument document) {
        terminationDocuments.remove(document);
        document.setTerminationRequest(null);
    }
    
    public List<TerminationDocument> getTerminationDocuments() {
        return terminationDocuments;
    }
    
    public void setTerminationDocuments(List<TerminationDocument> terminationDocuments) {
        this.terminationDocuments = terminationDocuments;
    }
}