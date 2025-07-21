package com.zanzibar.csms.service;

import com.zanzibar.csms.entity.enums.ComplaintSeverity;
import com.zanzibar.csms.entity.enums.ComplaintStatus;
import com.zanzibar.csms.entity.enums.ComplaintType;
import com.zanzibar.csms.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComplaintAnalyticsService {

    private final ComplaintRepository complaintRepository;

    public Map<String, Object> getComplaintAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalComplaints", getTotalComplaints(startDate, endDate));
        analytics.put("complaintsByStatus", getComplaintsByStatus(startDate, endDate));
        analytics.put("complaintsByType", getComplaintsByType(startDate, endDate));
        analytics.put("complaintsBySeverity", getComplaintsBySeverity(startDate, endDate));
        analytics.put("averageResolutionTime", getAverageResolutionTime(startDate, endDate));
        analytics.put("averageSatisfactionRating", getAverageSatisfactionRating(startDate, endDate));
        analytics.put("resolutionRate", getResolutionRate(startDate, endDate));

        return analytics;
    }

    public Map<String, Long> getDashboardMetrics() {
        Map<String, Long> metrics = new HashMap<>();

        metrics.put("totalComplaints", complaintRepository.count());
        metrics.put("pendingComplaints", complaintRepository.countByStatus(ComplaintStatus.SUBMITTED));
        metrics.put("underInvestigation", complaintRepository.countByStatus(ComplaintStatus.UNDER_INVESTIGATION));
        metrics.put("resolvedComplaints", complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        metrics.put("overdueComplaints", (long) complaintRepository.findOverdueComplaints(
            LocalDateTime.now(), 
            List.of(ComplaintStatus.RESOLVED, ComplaintStatus.CLOSED, ComplaintStatus.DISMISSED, ComplaintStatus.WITHDRAWN)
        ).size());

        return metrics;
    }

    private Long getTotalComplaints(LocalDateTime startDate, LocalDateTime endDate) {
        return complaintRepository.countBySubmissionDateBetween(startDate, endDate);
    }

    private Map<String, Long> getComplaintsByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> statusMap = new HashMap<>();
        
        for (ComplaintStatus status : ComplaintStatus.values()) {
            statusMap.put(status.name(), complaintRepository.countByStatus(status));
        }
        
        return statusMap;
    }

    private Map<String, Long> getComplaintsByType(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> typeMap = new HashMap<>();
        List<Object[]> typeStatistics = complaintRepository.getComplaintTypeStatistics(startDate, endDate);
        
        for (Object[] row : typeStatistics) {
            ComplaintType type = (ComplaintType) row[0];
            Long count = (Long) row[1];
            typeMap.put(type.name(), count);
        }
        
        return typeMap;
    }

    private Map<String, Long> getComplaintsBySeverity(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> severityMap = new HashMap<>();
        
        for (ComplaintSeverity severity : ComplaintSeverity.values()) {
            severityMap.put(severity.name(), complaintRepository.countBySeverity(severity));
        }
        
        return severityMap;
    }

    private Double getAverageResolutionTime(LocalDateTime startDate, LocalDateTime endDate) {
        Double avgDays = complaintRepository.getAverageResolutionTimeInDays(startDate, endDate);
        return avgDays != null ? avgDays : 0.0;
    }

    private Double getAverageSatisfactionRating(LocalDateTime startDate, LocalDateTime endDate) {
        Double avgRating = complaintRepository.getAverageSatisfactionRating(startDate, endDate);
        return avgRating != null ? avgRating : 0.0;
    }

    private Double getResolutionRate(LocalDateTime startDate, LocalDateTime endDate) {
        Long totalComplaints = getTotalComplaints(startDate, endDate);
        Long resolvedComplaints = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        
        if (totalComplaints == 0) return 0.0;
        
        return (resolvedComplaints.doubleValue() / totalComplaints.doubleValue()) * 100;
    }
}