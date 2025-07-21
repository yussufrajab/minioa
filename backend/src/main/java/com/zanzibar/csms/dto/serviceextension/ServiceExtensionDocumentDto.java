package com.zanzibar.csms.dto.serviceextension;

import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.ServiceExtensionDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceExtensionDocumentDto {

    private String id;
    private ServiceExtensionDocumentType documentType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String description;
    private Boolean isMandatory;
    private Boolean isVerified;
    private String verificationNotes;
    private UserBasicDto uploadedBy;
    private UserBasicDto verifiedBy;
    private LocalDateTime createdAt;

    // Helper methods
    public String getDocumentDisplayName() {
        return documentType != null ? documentType.getDisplayName() : "Unknown";
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

    public String getDocumentTypeDisplay() {
        return documentType != null ? documentType.getDisplayName() : "Unknown";
    }

    public boolean isDocumentTypeMandatory() {
        return documentType != null && documentType.isMandatory();
    }
}