package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.Request;
import com.zanzibar.csms.entity.SLATracker;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.RequestStatus;
import com.zanzibar.csms.entity.enums.UserRole;
import com.zanzibar.csms.repository.RequestRepository;
import com.zanzibar.csms.repository.SLATrackerRepository;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAManagementService {

    private final RequestRepository requestRepository;
    private final SLATrackerRepository slaTrackerRepository;
    private final UserRepository userRepository;
    private final BusinessRuleEngine businessRuleEngine;
    private final NotificationService notificationService;
    private final WorkflowService workflowService;
    private final AuditService auditService;

    /**
     * Creates SLA tracker for a new request
     */
    @Transactional
    public SLATracker createSLATracker(Request request) {
        log.info("Creating SLA tracker for request: {}", request.getRequestNumber());

        LocalDateTime dueDate = businessRuleEngine.calculateSLADueDate(request);
        
        SLATracker slaTracker = SLATracker.builder()
            .request(request)
            .dueDate(dueDate)
            .isBreached(false)
            .escalationLevel(0)
            .build();

        return slaTrackerRepository.save(slaTracker);
    }

    /**
     * Updates SLA tracker when request status changes
     */
    @Transactional
    public void updateSLATracker(Request request) {
        Optional<SLATracker> trackerOpt = slaTrackerRepository.findByRequest(request);
        
        if (trackerOpt.isPresent()) {
            SLATracker tracker = trackerOpt.get();
            
            // Update breach status
            boolean isBreached = businessRuleEngine.hasBreachedSLA(request);
            tracker.setIsBreached(isBreached);
            
            // If request is completed, set completion date
            if (request.getStatus().isTerminal()) {
                tracker.setCompletedAt(LocalDateTime.now());
                tracker.setCompletedWithinSLA(!isBreached);
            }
            
            slaTrackerRepository.save(tracker);
            
            // If breached and not completed, trigger escalation
            if (isBreached && !request.getStatus().isTerminal()) {
                escalateRequest(request, tracker);
            }
        }
    }

    /**
     * Escalates overdue requests
     */
    @Transactional
    public void escalateRequest(Request request, SLATracker tracker) {
        log.info("Escalating overdue request: {}", request.getRequestNumber());

        List<UserRole> escalationPath = businessRuleEngine.getEscalationPath(request.getRequestType());
        
        // Increment escalation level
        int currentLevel = tracker.getEscalationLevel();
        int nextLevel = Math.min(currentLevel + 1, escalationPath.size() - 1);
        
        if (nextLevel > currentLevel) {
            UserRole escalationRole = escalationPath.get(nextLevel);
            
            // Find users with the escalation role
            List<User> escalationUsers = userRepository.findByRole(escalationRole);
            
            if (!escalationUsers.isEmpty()) {
                User escalationUser = escalationUsers.get(0); // Simple assignment, could be improved
                
                // Update current reviewer
                request.setCurrentReviewer(escalationUser);
                requestRepository.save(request);
                
                // Update tracker
                tracker.setEscalationLevel(nextLevel);
                tracker.setEscalatedAt(LocalDateTime.now());
                tracker.setEscalatedTo(escalationUser);
                slaTrackerRepository.save(tracker);
                
                // Send escalation notification
                notificationService.sendEscalationNotification(request, escalationUser);
                
                // Log audit event
                auditService.logAction(
                    "SYSTEM",
                    "SYSTEM",
                    "REQUEST_ESCALATED",
                    "Request",
                    request.getId(),
                    "Level " + currentLevel,
                    "Level " + nextLevel,
                    true,
                    null
                );
            }
        }
    }

    /**
     * Scheduled job to check for overdue requests
     */
    @Scheduled(cron = "0 0 */2 * * *") // Every 2 hours
    @Transactional
    public void checkOverdueRequests() {
        log.info("Checking for overdue requests");

        List<Request> pendingRequests = requestRepository.findByStatusIn(
            List.of(RequestStatus.SUBMITTED, RequestStatus.HRO_REVIEW, 
                   RequestStatus.HRMO_REVIEW, RequestStatus.HHRMD_REVIEW)
        );

        for (Request request : pendingRequests) {
            if (businessRuleEngine.hasBreachedSLA(request)) {
                updateSLATracker(request);
            }
        }
    }

    /**
     * Scheduled job to send SLA warning notifications
     */
    @Scheduled(cron = "0 0 8 * * *") // Every day at 8 AM
    @Async
    public void sendSLAWarningNotifications() {
        log.info("Sending SLA warning notifications");

        List<Request> pendingRequests = requestRepository.findByStatusIn(
            List.of(RequestStatus.SUBMITTED, RequestStatus.HRO_REVIEW, 
                   RequestStatus.HRMO_REVIEW, RequestStatus.HHRMD_REVIEW)
        );

        for (Request request : pendingRequests) {
            if (businessRuleEngine.isApproachingSLADeadline(request)) {
                notificationService.sendSLAWarningNotification(request);
            }
        }
    }

    /**
     * Gets SLA performance metrics
     */
    public SLAPerformanceMetrics getSLAMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        List<SLATracker> trackers = slaTrackerRepository.findByRequestSubmissionDateBetween(startDate, endDate);
        
        long totalRequests = trackers.size();
        long completedRequests = trackers.stream()
            .filter(t -> t.getCompletedAt() != null)
            .count();
        
        long completedWithinSLA = trackers.stream()
            .filter(t -> Boolean.TRUE.equals(t.getCompletedWithinSLA()))
            .count();
        
        long breachedRequests = trackers.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsBreached()))
            .count();
        
        double slaComplianceRate = completedRequests > 0 ? 
            (double) completedWithinSLA / completedRequests * 100 : 0;
        
        return SLAPerformanceMetrics.builder()
            .totalRequests(totalRequests)
            .completedRequests(completedRequests)
            .completedWithinSLA(completedWithinSLA)
            .breachedRequests(breachedRequests)
            .slaComplianceRate(slaComplianceRate)
            .build();
    }

    /**
     * Gets overdue requests for a specific user
     */
    public Page<Request> getOverdueRequestsForUser(String userId, Pageable pageable) {
        return requestRepository.findOverdueRequestsByCurrentReviewer(userId, LocalDateTime.now(), pageable);
    }

    /**
     * Gets requests approaching SLA deadline
     */
    public Page<Request> getRequestsApproachingSLA(Pageable pageable) {
        LocalDateTime warningThreshold = LocalDateTime.now().plusDays(2);
        return requestRepository.findRequestsApproachingSLA(warningThreshold, pageable);
    }

    /**
     * Extends SLA deadline for a request (with justification)
     */
    @Transactional
    public void extendSLADeadline(String requestId, int extensionDays, String justification, String userId) {
        log.info("Extending SLA deadline for request: {} by {} days", requestId, extensionDays);

        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        Optional<SLATracker> trackerOpt = slaTrackerRepository.findByRequest(request);
        
        if (trackerOpt.isPresent()) {
            SLATracker tracker = trackerOpt.get();
            LocalDateTime newDueDate = tracker.getDueDate().plusDays(extensionDays);
            
            tracker.setDueDate(newDueDate);
            tracker.setExtensionDays(tracker.getExtensionDays() + extensionDays);
            tracker.setExtensionJustification(justification);
            tracker.setExtendedBy(userId);
            tracker.setExtendedAt(LocalDateTime.now());
            
            slaTrackerRepository.save(tracker);
            
            // Log audit event
            auditService.logAction(
                userId,
                null,
                "SLA_EXTENDED",
                "Request",
                requestId,
                extensionDays + " days",
                justification,
                true,
                null
            );
        }
    }

    /**
     * Data class for SLA performance metrics
     */
    public static class SLAPerformanceMetrics {
        private final long totalRequests;
        private final long completedRequests;
        private final long completedWithinSLA;
        private final long breachedRequests;
        private final double slaComplianceRate;

        public SLAPerformanceMetrics(long totalRequests, long completedRequests, 
                                   long completedWithinSLA, long breachedRequests, 
                                   double slaComplianceRate) {
            this.totalRequests = totalRequests;
            this.completedRequests = completedRequests;
            this.completedWithinSLA = completedWithinSLA;
            this.breachedRequests = breachedRequests;
            this.slaComplianceRate = slaComplianceRate;
        }

        public static SLAPerformanceMetricsBuilder builder() {
            return new SLAPerformanceMetricsBuilder();
        }

        // Getters
        public long getTotalRequests() { return totalRequests; }
        public long getCompletedRequests() { return completedRequests; }
        public long getCompletedWithinSLA() { return completedWithinSLA; }
        public long getBreachedRequests() { return breachedRequests; }
        public double getSlaComplianceRate() { return slaComplianceRate; }

        public static class SLAPerformanceMetricsBuilder {
            private long totalRequests;
            private long completedRequests;
            private long completedWithinSLA;
            private long breachedRequests;
            private double slaComplianceRate;

            public SLAPerformanceMetricsBuilder totalRequests(long totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public SLAPerformanceMetricsBuilder completedRequests(long completedRequests) {
                this.completedRequests = completedRequests;
                return this;
            }

            public SLAPerformanceMetricsBuilder completedWithinSLA(long completedWithinSLA) {
                this.completedWithinSLA = completedWithinSLA;
                return this;
            }

            public SLAPerformanceMetricsBuilder breachedRequests(long breachedRequests) {
                this.breachedRequests = breachedRequests;
                return this;
            }

            public SLAPerformanceMetricsBuilder slaComplianceRate(double slaComplianceRate) {
                this.slaComplianceRate = slaComplianceRate;
                return this;
            }

            public SLAPerformanceMetrics build() {
                return new SLAPerformanceMetrics(totalRequests, completedRequests, 
                                               completedWithinSLA, breachedRequests, 
                                               slaComplianceRate);
            }
        }
    }
}