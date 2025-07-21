package com.zanzibar.csms.dto.cadre;

import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.CadreChangeDocumentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CadreChangeDocumentDto {

    private String id;
    private CadreChangeDocumentType documentType;
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
    public String getDocumentTypeName() {
        return documentType != null ? documentType.getDisplayName() : "Unknown";
    }

    public String getDocumentTypeDescription() {
        return documentType != null ? documentType.getDescription() : "";
    }

    public boolean isMandatoryDocument() {
        return isMandatory != null && isMandatory;
    }

    public boolean isVerifiedDocument() {
        return isVerified != null && isVerified;
    }

    public boolean isPdfDocument() {
        return "application/pdf".equals(contentType);
    }

    public String getFileSizeFormatted() {
        if (fileSize == null) {
            return "Unknown";
        }
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }

    public boolean isValidSize() {
        return fileSize != null && fileSize > 0 && fileSize <= 2 * 1024 * 1024; // 2MB limit
    }

    public String getUploadedByName() {
        return uploadedBy != null ? uploadedBy.getFullName() : "Unknown";
    }

    public String getVerifiedByName() {
        return verifiedBy != null ? verifiedBy.getFullName() : "Not verified";
    }
}