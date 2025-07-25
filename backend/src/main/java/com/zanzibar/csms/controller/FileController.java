package com.zanzibar.csms.controller;

import com.zanzibar.csms.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "File upload, download, and management operations")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO', 'HHRMD', 'HRMO', 'DO', 'EMPLOYEE')")
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "File to upload") 
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Folder/category for the file") 
            @RequestParam(value = "folder", defaultValue = "documents") String folder) {
        
        try {
            log.info("Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);
            
            String objectKey = fileStorageService.uploadFile(file, folder);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("data", Map.of(
                "objectKey", objectKey,
                "originalName", file.getOriginalFilename(),
                "size", file.getSize(),
                "contentType", file.getContentType()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("File validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to upload file"));
        }
    }

    @GetMapping("/download/**")
    @Operation(summary = "Download a file")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO', 'HHRMD', 'HRMO', 'DO', 'EMPLOYEE', 'CSCS', 'HRRP', 'PO')")
    public ResponseEntity<?> downloadFile(HttpServletRequest request) {
        
        try {
            // Extract object key from request path
            String path = request.getRequestURI();
            String prefix = "/api/files/download/";
            String objectKey = path.substring(path.indexOf(prefix) + prefix.length());
            
            // URL decode the object key
            objectKey = java.net.URLDecoder.decode(objectKey, java.nio.charset.StandardCharsets.UTF_8);
            
            log.info("Downloading file: {}", objectKey);
            
            if (!fileStorageService.fileExists(objectKey)) {
                log.error("File not found: {}", objectKey);
                return ResponseEntity.notFound().build();
            }
            
            InputStream fileStream = fileStorageService.downloadFile(objectKey);
            String contentType = fileStorageService.getFileContentType(objectKey);
            
            // Extract filename from object key
            String filename = objectKey.substring(objectKey.lastIndexOf('/') + 1);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, contentType);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(fileStream));
                    
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to download file"));
        }
    }

    @GetMapping(value = "/preview/**")
    @Operation(summary = "Preview a file (get presigned URL)")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO', 'HHRMD', 'HRMO', 'DO', 'EMPLOYEE', 'CSCS', 'HRRP', 'PO')")
    public ResponseEntity<?> previewFile(
            HttpServletRequest request,
            @Parameter(description = "URL expiry in minutes (default: 60)") 
            @RequestParam(value = "expiry", defaultValue = "60") int expiryMinutes) {
        
        String objectKey = null;
        try {
            // Extract object key from request path
            String requestURI = request.getRequestURI();
            String prefix = "/api/files/preview/";
            objectKey = requestURI.substring(requestURI.indexOf(prefix) + prefix.length());
            
            // URL decode the object key in case it has encoded characters
            objectKey = java.net.URLDecoder.decode(objectKey, java.nio.charset.StandardCharsets.UTF_8);
            
            log.info("=== FILE PREVIEW DEBUG ===");
            log.info("Request URI: '{}'", requestURI);
            log.info("Extracted objectKey: '{}'", objectKey);
            log.info("ObjectKey length: {}, contains slash: {}", objectKey.length(), objectKey.contains("/"));
            
            // Check if file exists first
            if (!fileStorageService.fileExists(objectKey)) {
                log.error("File does not exist in MinIO: {}", objectKey);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "File not found: " + objectKey));
            }
            
            log.info("File exists in MinIO, generating presigned URL...");
            String preSignedUrl = fileStorageService.getPreSignedUrl(objectKey, expiryMinutes);
            log.info("Generated presigned URL: {}", preSignedUrl);
            
            String contentType = fileStorageService.getFileContentType(objectKey);
            log.info("File content type: {}", contentType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "url", preSignedUrl,
                "contentType", contentType,
                "expiryMinutes", expiryMinutes
            ));
            
            log.info("=== END FILE PREVIEW DEBUG ===");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating preview URL for objectKey: {}", objectKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to generate preview URL: " + e.getMessage()));
        }
    }

    @DeleteMapping("/**")
    @Operation(summary = "Delete a file")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO', 'HHRMD', 'HRMO', 'DO')")
    public ResponseEntity<?> deleteFile(HttpServletRequest request) {
        
        try {
            // Extract object key from request path
            String path = request.getRequestURI();
            String prefix = "/api/files/";
            String objectKey = path.substring(path.indexOf(prefix) + prefix.length());
            
            // URL decode the object key
            objectKey = java.net.URLDecoder.decode(objectKey, java.nio.charset.StandardCharsets.UTF_8);
            
            log.info("Deleting file: {}", objectKey);
            
            if (!fileStorageService.fileExists(objectKey)) {
                return ResponseEntity.notFound().build();
            }
            
            fileStorageService.deleteFile(objectKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to delete file"));
        }
    }

    @GetMapping("/exists/**")
    @Operation(summary = "Check if a file exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'HRO', 'HHRMD', 'HRMO', 'DO', 'EMPLOYEE', 'CSCS', 'HRRP', 'PO')")
    public ResponseEntity<?> fileExists(HttpServletRequest request) {
        
        try {
            // Extract object key from request path
            String path = request.getRequestURI();
            String prefix = "/api/files/exists/";
            String objectKey = path.substring(path.indexOf(prefix) + prefix.length());
            
            // URL decode the object key
            objectKey = java.net.URLDecoder.decode(objectKey, java.nio.charset.StandardCharsets.UTF_8);
            
            boolean exists = fileStorageService.fileExists(objectKey);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("exists", exists);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error checking file existence: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to check file existence"));
        }
    }
}