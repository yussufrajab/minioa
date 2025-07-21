package com.zanzibar.csms.dto.dismissal;

import com.zanzibar.csms.dto.user.UserBasicDto;
import com.zanzibar.csms.entity.enums.DismissalDocumentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DismissalDocumentDto {

    private String id;
    private DismissalDocumentType documentType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String description;
    private Boolean isMandatory;
    private String confidentialityLevel;
    private UserBasicDto uploadedBy;
    private LocalDateTime createdAt;
}