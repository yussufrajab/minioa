package com.zanzibar.csms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_extension_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceExtensionDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_extension_request_id", nullable = false)
    private ServiceExtensionRequest serviceExtensionRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private ServiceExtensionDocumentType documentType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verification_notes", length = 1000)
    private String verificationNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    // Helper methods
    public String getDocumentDisplayName() {
        return documentType.getDisplayName();
    }

    public boolean isMandatoryDocument() {
        return Boolean.TRUE.equals(isMandatory);
    }

    public boolean isVerifiedDocument() {
        return Boolean.TRUE.equals(isVerified);
    }

    public String getFileSizeDisplay() {
        if (fileSize == null) return "Unknown";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
}