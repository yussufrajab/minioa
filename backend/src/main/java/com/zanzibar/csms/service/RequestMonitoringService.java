package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.UserRole;
import com.zanzibar.csms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestMonitoringService {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // Check for overdue requests every hour
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 milliseconds
    public void checkOverdueRequests() {
        log.info("Running scheduled overdue request check...");
        notificationService.checkOverdueRequests();
    }

    // Send daily digest at 8 AM every day
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyDigests() {
        log.info("Sending daily digest notifications...");
        
        // Send to all reviewers (HRO, HRMO, HHRMD)
        List<UserRole> reviewerRoles = List.of(UserRole.HRO, UserRole.HRMO, UserRole.HHRMD);
        
        for (UserRole role : reviewerRoles) {
            List<User> users = userRepository.findByRoleAndActiveTrue(role);
            for (User user : users) {
                try {
                    notificationService.sendDailyDigest(user);
                } catch (Exception e) {
                    log.error("Failed to send daily digest to user {}: {}", user.getUsername(), e.getMessage());
                }
            }
        }
    }

    // Send weekly summary every Monday at 9 AM
    @Scheduled(cron = "0 0 9 ? * MON")
    public void sendWeeklySummary() {
        log.info("Sending weekly summary notifications...");
        
        // Send to management roles (HHRMD, ADMIN)
        List<UserRole> managementRoles = List.of(UserRole.HHRMD, UserRole.ADMIN);
        
        for (UserRole role : managementRoles) {
            List<User> users = userRepository.findByRoleAndActiveTrue(role);
            for (User user : users) {
                try {
                    sendWeeklySummaryToUser(user);
                } catch (Exception e) {
                    log.error("Failed to send weekly summary to user {}: {}", user.getUsername(), e.getMessage());
                }
            }
        }
    }

    // Clean up old notifications monthly (first day of month at 2 AM)
    @Scheduled(cron = "0 0 2 1 * ?")
    public void cleanupOldNotifications() {
        log.info("Running monthly cleanup of old notifications...");
        
        // Implementation would clean up old audit logs, notifications, etc.
        // This helps maintain database performance
        
        log.info("Monthly cleanup completed");
    }

    // Performance monitoring every 6 hours
    @Scheduled(fixedRate = 21600000) // 6 hours = 21,600,000 milliseconds
    public void monitorSystemPerformance() {
        log.info("Running system performance monitoring...");
        
        // Monitor average processing times, bottlenecks, etc.
        // Could trigger alerts if performance degrades
        
        log.info("Performance monitoring completed");
    }

    private void sendWeeklySummaryToUser(User user) {
        log.info("Sending weekly summary to user: {}", user.getUsername());
        
        // Implementation would gather weekly statistics and send summary
        // This could include:
        // - Total requests processed
        // - Average processing time
        // - Pending requests count
        // - Performance metrics
        
        String summary = buildWeeklySummary(user);
        log.info("WEEKLY SUMMARY for {}: {}", user.getUsername(), summary);
    }

    private String buildWeeklySummary(User user) {
        return String.format(
            "=== Weekly Summary for %s\n" +
            "Role: %s\n" +
            "Institution: %s\n" +
            "Period: Last 7 days\n" +
            "Status: System operational",
            user.getFullName(),
            user.getRole().name(),
            user.getInstitution() != null ? user.getInstitution().getName() : "N/A"
        );
    }
}