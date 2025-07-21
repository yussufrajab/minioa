package com.zanzibar.csms.dto.termination;

import com.zanzibar.csms.entity.enums.TerminationDocumentType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminationDocumentDto {

    private String id;
    private TerminationDocumentType documentType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String description;
    private Boolean isMandatory;
    private LocalDateTime createdAt;
}