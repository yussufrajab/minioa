package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.DismissalDocumentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dismissal_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DismissalDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dismissal_request_id", nullable = false)
    private DismissalRequest dismissalRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DismissalDocumentType documentType;

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

    @Column(name = "is_mandatory")
    @Builder.Default
    private Boolean isMandatory = false;

    @Column(name = "confidentiality_level")
    @Builder.Default
    private String confidentialityLevel = "RESTRICTED";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}