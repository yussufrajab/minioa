package com.zanzibar.csms.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    // Allowed file types
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/jpg",
            "image/png",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    // Max file size: 2MB
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("Created bucket: {}", bucketName);
            } else {
                log.info("Bucket already exists: {}", bucketName);
            }
        } catch (Exception e) {
            log.warn("MinIO is not available during startup. File operations will be disabled until MinIO is running: {}", e.getMessage());
            // Don't throw exception - let the app start without MinIO
        }
    }

    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Validate file
            validateFile(file);

            // Generate unique object key
            String objectKey = generateObjectKey(file.getOriginalFilename(), folder);

            // Upload to MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("File uploaded successfully: {}", objectKey);
            return objectKey;

        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            if (e.getMessage().contains("Connection refused") || e.getMessage().contains("ConnectException")) {
                throw new RuntimeException("MinIO service is not available. Please ensure MinIO is running on " + minioClient.toString(), e);
            }
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public InputStream downloadFile(String objectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download file", e);
        }
    }

    public String getPreSignedUrl(String objectKey, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            log.info("File deleted successfully: {}", objectKey);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    public boolean fileExists(String objectKey) {
        try {
            log.info("Checking if file exists in MinIO: bucket={}, objectKey={}", bucketName, objectKey);
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            log.info("File exists in MinIO: {}", objectKey);
            return true;
        } catch (Exception e) {
            log.error("File does not exist in MinIO: {} - Error: {}", objectKey, e.getMessage());
            return false;
        }
    }

    public String getFileContentType(String objectKey) {
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            return stat.contentType();
        } catch (Exception e) {
            log.error("Error getting file content type: {}", e.getMessage(), e);
            return "application/octet-stream";
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 2MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "File type not allowed. Supported types: " + String.join(", ", ALLOWED_CONTENT_TYPES)
            );
        }
    }

    private String generateObjectKey(String originalFilename, String folder) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);
        
        return String.format("%s/%s_%s%s", folder, timestamp, uuid, extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}