package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appeal_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppealEvidence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_id", nullable = false)
    private ComplaintAppeal appeal;

    @Column(name = "evidence_type", nullable = false)
    private String evidenceType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(name = "is_confidential")
    private Boolean isConfidential = true;

    @Column(name = "evidence_hash")
    private String evidenceHash;
}