package com.zanzibar.csms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDocumentDto {

    private String id;
    private String requestId;
    private String documentType;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String description;
    private LocalDateTime uploadDate;
}