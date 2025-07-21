package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, String> {

    List<AuditLog> findByUserId(String userId);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByTimestampBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByUserIdAndTimestampBetween(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.success = :success")
    List<AuditLog> findBySuccess(@Param("success") Boolean success);

    @Query("SELECT a FROM AuditLog a WHERE a.username LIKE CONCAT('%', :searchTerm, '%') OR a.action LIKE CONCAT('%', :searchTerm, '%') OR a.entityType LIKE CONCAT('%', :searchTerm, '%')")
    Page<AuditLog> searchAuditLogs(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action")
    Long countByAction(@Param("action") String action);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId")
    Long countByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate")
    Long countByTimestampBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC")
    Page<AuditLog> findAllOrderByTimestampDesc(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress")
    List<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress);

    @Query("SELECT a FROM AuditLog a WHERE a.success = false AND a.timestamp >= :since")
    List<AuditLog> findFailedActionsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT a.userId) FROM AuditLog a WHERE a.timestamp >= :timestamp")
    Long countDistinctUsersByTimestampAfter(@Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.success = :isSuccessful AND a.timestamp BETWEEN :startDate AND :endDate")
    Long countByIsSuccessfulAndTimestampBetween(@Param("isSuccessful") boolean isSuccessful, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action AND a.timestamp BETWEEN :startDate AND :endDate")
    Long countByActionAndTimestampBetween(@Param("action") String action, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    Page<AuditLog> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId IN (SELECT u.id FROM User u WHERE u.role = :userRole) AND a.timestamp BETWEEN :startDate AND :endDate")
    Long countByUserRoleAndTimestampBetween(@Param("userRole") com.zanzibar.csms.entity.enums.UserRole userRole, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}