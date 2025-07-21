package com.zanzibar.csms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto {

    private List<ActivityItem> recentActivities;
    private List<TaskItem> pendingTasks;
    private List<NotificationItem> notifications;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityItem {
        private String id;
        private String type;
        private String action;
        private String description;
        private String entityType;
        private String entityId;
        private String entityName;
        private String userName;
        private LocalDateTime timestamp;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskItem {
        private String id;
        private String type;
        private String title;
        private String description;
        private String priority;
        private String status;
        private LocalDateTime dueDate;
        private LocalDateTime createdDate;
        private String assignedTo;
        private String createdBy;
        private Boolean isOverdue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationItem {
        private String id;
        private String type;
        private String title;
        private String message;
        private String priority;
        private LocalDateTime timestamp;
        private Boolean isRead;
        private String actionUrl;
        private String entityType;
        private String entityId;
    }
}