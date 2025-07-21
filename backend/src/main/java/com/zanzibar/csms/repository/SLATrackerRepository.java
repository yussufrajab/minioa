package com.zanzibar.csms.repository;

import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.SLATracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SLATrackerRepository extends JpaRepository<SLATracker, String> {

    /**
     * Find SLA tracker by request
     */
    Optional<SLATracker> findByRequest(Request request);

    /**
     * Find overdue requests (breached SLA and not completed)
     */
    @Query("SELECT s FROM SLATracker s WHERE s.isBreached = true AND s.completedAt IS NULL")
    List<SLATracker> findOverdueRequests();

    /**
     * Find requests approaching SLA deadline
     */
    @Query("SELECT s FROM SLATracker s WHERE s.dueDate BETWEEN :now AND :warningThreshold AND s.completedAt IS NULL")
    List<SLATracker> findRequestsApproachingSLA(@Param("now") LocalDateTime now, 
                                               @Param("warningThreshold") LocalDateTime warningThreshold);

    /**
     * Find SLA trackers by request submission date range
     */
    @Query("SELECT s FROM SLATracker s WHERE s.request.submissionDate BETWEEN :startDate AND :endDate")
    List<SLATracker> findByRequestSubmissionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Find completed requests within SLA
     */
    @Query("SELECT s FROM SLATracker s WHERE s.completedWithinSLA = true AND s.completedAt BETWEEN :startDate AND :endDate")
    List<SLATracker> findCompletedWithinSLA(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find breached requests by date range
     */
    @Query("SELECT s FROM SLATracker s WHERE s.isBreached = true AND s.request.submissionDate BETWEEN :startDate AND :endDate")
    List<SLATracker> findBreachedRequests(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Count requests by SLA compliance status
     */
    @Query("SELECT COUNT(s) FROM SLATracker s WHERE s.completedWithinSLA = :withinSLA AND s.completedAt BETWEEN :startDate AND :endDate")
    long countByComplianceStatus(@Param("withinSLA") Boolean withinSLA, 
                                @Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);

    /**
     * Find escalated requests
     */
    @Query("SELECT s FROM SLATracker s WHERE s.escalationLevel > 0 AND s.completedAt IS NULL")
    List<SLATracker> findEscalatedRequests();

    /**
     * Find requests that need warning notifications
     */
    @Query("SELECT s FROM SLATracker s WHERE s.warningSent = false AND s.dueDate BETWEEN :now AND :warningThreshold AND s.completedAt IS NULL")
    List<SLATracker> findRequestsNeedingWarning(@Param("now") LocalDateTime now, 
                                               @Param("warningThreshold") LocalDateTime warningThreshold);

    /**
     * Find extended requests
     */
    @Query("SELECT s FROM SLATracker s WHERE s.extensionDays > 0")
    List<SLATracker> findExtendedRequests();

    /**
     * Get average processing time for completed requests
     */
    @Query("SELECT AVG(CAST((EXTRACT(EPOCH FROM s.completedAt) - EXTRACT(EPOCH FROM s.request.submissionDate))/3600 AS double)) FROM SLATracker s WHERE s.completedAt IS NOT NULL AND s.completedAt BETWEEN :startDate AND :endDate")
    Double getAverageProcessingTimeInHours(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Get SLA compliance rate
     */
    @Query("SELECT (COUNT(s) * 100.0 / (SELECT COUNT(t) FROM SLATracker t WHERE t.completedAt IS NOT NULL AND t.completedAt BETWEEN :startDate AND :endDate)) FROM SLATracker s WHERE s.completedWithinSLA = true AND s.completedAt BETWEEN :startDate AND :endDate")
    Double getSLAComplianceRate(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);

    /**
     * Find most escalated request types
     */
    @Query("SELECT s.request.requestType, COUNT(s) FROM SLATracker s WHERE s.escalationLevel > 0 GROUP BY s.request.requestType ORDER BY COUNT(s) DESC")
    List<Object[]> findMostEscalatedRequestTypes();

    /**
     * Find requests by escalation level
     */
    @Query("SELECT s FROM SLATracker s WHERE s.escalationLevel = :level AND s.completedAt IS NULL")
    List<SLATracker> findByEscalationLevel(@Param("level") Integer level);

    /**
     * Find requests escalated to specific user
     */
    @Query("SELECT s FROM SLATracker s WHERE s.escalatedTo.id = :userId AND s.completedAt IS NULL")
    Page<SLATracker> findByEscalatedToUser(@Param("userId") String userId, Pageable pageable);
}