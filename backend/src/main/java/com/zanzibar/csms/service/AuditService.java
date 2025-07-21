package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.AuditLog;
import com.zanzibar.csms.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    
    @Value("${csms.audit.enabled:true}")
    private boolean auditEnabled;

    @Transactional
    public void logAction(String userId, String username, String action, String entityType, String entityId,
                         String beforeValue, String afterValue, Boolean success, String errorMessage) {
        if (!auditEnabled) {
            return; // Skip audit logging if disabled
        }
        
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .beforeValue(beforeValue)
                    .afterValue(afterValue)
                    .success(success)
                    .errorMessage(errorMessage)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Log audit failure but don't break the main functionality
            System.err.println("Failed to save audit log: " + e.getMessage());
            // In production, you might want to use a proper logger or fallback mechanism
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllOrderByTimestampDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByUserId(String userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByEntityTypeAndId(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> searchAuditLogs(String searchTerm, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(searchTerm, pageable);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getFailedActionsSince(LocalDateTime since) {
        return auditLogRepository.findFailedActionsSince(since);
    }

    @Transactional(readOnly = true)
    public Long countByAction(String action) {
        return auditLogRepository.countByAction(action);
    }

    @Transactional(readOnly = true)
    public Long countByUserId(String userId) {
        return auditLogRepository.countByUserId(userId);
    }
}