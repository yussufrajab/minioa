package com.zanzibar.csms.entity;

import com.zanzibar.csms.entity.enums.TerminationDocumentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "termination_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminationDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termination_request_id", nullable = false)
    private TerminationRequest terminationRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private TerminationDocumentType documentType;

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
}