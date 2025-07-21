package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cadre_change_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CadreChangeDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadre_change_request_id", nullable = false)
    private CadreChangeRequest cadreChangeRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private CadreChangeDocumentType documentType;

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

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verification_notes")
    private String verificationNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    public boolean isValid() {
        return fileName != null && !fileName.trim().isEmpty() &&
               filePath != null && !filePath.trim().isEmpty() &&
               fileSize != null && fileSize > 0 &&
               fileSize <= 2 * 1024 * 1024; // 2MB limit
    }

    public boolean isPdfFile() {
        return contentType != null && contentType.equals("application/pdf");
    }

    public String getDisplayName() {
        return documentType != null ? documentType.getDisplayName() : fileName;
    }

    public boolean isMandatoryDocument() {
        return isMandatory != null && isMandatory;
    }

    public boolean isVerifiedDocument() {
        return isVerified != null && isVerified;
    }
}