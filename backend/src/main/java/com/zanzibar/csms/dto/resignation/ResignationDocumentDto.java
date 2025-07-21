package com.zanzibar.csms.dto.resignation;

import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.ResignationDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResignationDocumentDto {

    private String id;
    private ResignationDocumentType documentType;
    private String documentTypeDisplay;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileSizeDisplay;
    private String contentType;
    private String description;
    private Boolean isMandatory;
    private Boolean isVerified;
    private String verificationNotes;
    private UserBasicDto uploadedBy;
    private UserBasicDto verifiedBy;
    private LocalDateTime createdAt;

    // Helper methods
    public boolean isRequired() {
        return Boolean.TRUE.equals(isMandatory);
    }

    public boolean isVerified() {
        return Boolean.TRUE.equals(isVerified);
    }

    public String getStatusDisplay() {
        if (Boolean.TRUE.equals(isVerified)) {
            return "Verified";
        } else if (Boolean.TRUE.equals(isMandatory)) {
            return "Required";
        } else {
            return "Optional";
        }
    }
}