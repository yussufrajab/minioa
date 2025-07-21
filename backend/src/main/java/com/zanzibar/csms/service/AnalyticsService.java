package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.dashboard.AnalyticsReportDto;
import com.zanzibar.csms.entity.User;
import com.zanzibar.csms.entity.enums.UserRole;
import com.zanzibar.csms.exception.ResourceNotFoundException;
import com.zanzibar.csms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final RequestRepository requestRepository;
    private final ComplaintRepository complaintRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final SLATrackerRepository slaTrackerRepository;

    /**
     * Get analytics overview
     */
    public AnalyticsReportDto getAnalyticsOverview(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                                  String institutionId, String userId) {
        log.info("Generating analytics overview for period: {} to {}", periodStart, periodEnd);
        
        User user = userRepository.findByUsername(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Generate system performance metrics
        AnalyticsReportDto.SystemPerformance performance = getSystemPerformance(
            periodStart, periodEnd, institutionId, userId);
        
        // Generate trend data
        List<AnalyticsReportDto.TrendData> trends = generateTrendData(periodStart, periodEnd, institutionId);
        
        // Generate comparison data
        List<AnalyticsReportDto.ComparisonData> comparisons = generateComparisonData(
            periodStart, periodEnd, institutionId);
        
        // Generate summary data
        Map<String, Object> summary = generateSummaryData(periodStart, periodEnd, institutionId);
        
        return AnalyticsReportDto.builder()
            .reportId("ANALYTICS_" + System.currentTimeMillis())
            .reportName("Analytics Overview")
            .reportType("ANALYTICS")
            .generatedAt(LocalDateTime.now())
            .periodStart(periodStart)
            .periodEnd(periodEnd)
            .generatedBy(userId)
            .systemPerformance(performance)
            .trends(trends)
            .comparisons(comparisons)
            .summary(summary)
            .build();
    }

    /**
     * Get analytics trends
     */
    public List<AnalyticsReportDto.TrendData> getAnalyticsTrends(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                                               String category, String institutionId, String userId) {
        log.info("Generating analytics trends for category: {}", category);
        
        return generateTrendDataForCategory(periodStart, periodEnd, category, institutionId);
    }

    /**
     * Get system performance metrics
     */
    public AnalyticsReportDto.SystemPerformance getSystemPerformance(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                                                    String institutionId, String userId) {
        log.info("Generating system performance metrics");
        
        // Calculate request completion rate
        long totalRequests = institutionId != null ? 
            requestRepository.countByEmployeeInstitutionId(institutionId) :
            requestRepository.count();
        
        long completedRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndStatus(
                institutionId, com.zanzibar.csms.entity.enums.RequestStatus.APPROVED) :
            requestRepository.countByStatus(com.zanzibar.csms.entity.enums.RequestStatus.APPROVED);
        
        double completionRate = totalRequests > 0 ? (double) completedRequests / totalRequests * 100 : 0.0;
        
        // Calculate average processing time
        Double avgProcessingTime = slaTrackerRepository.getAverageProcessingTimeInHours(periodStart, periodEnd);
        if (avgProcessingTime == null) avgProcessingTime = 0.0;
        
        // Calculate user satisfaction score (placeholder)
        Double satisfactionScore = complaintRepository.getAverageSatisfactionRating(periodStart, periodEnd);
        if (satisfactionScore == null) satisfactionScore = 0.0;
        
        // Performance by module
        Map<String, Double> performanceByModule = new HashMap<>();
        performanceByModule.put("CONFIRMATION", calculateModulePerformance("CONFIRMATION", periodStart, periodEnd, institutionId));
        performanceByModule.put("LWOP", calculateModulePerformance("LWOP", periodStart, periodEnd, institutionId));
        performanceByModule.put("PROMOTION", calculateModulePerformance("PROMOTION", periodStart, periodEnd, institutionId));
        performanceByModule.put("COMPLAINTS", calculateComplaintPerformance(periodStart, periodEnd, institutionId));
        performanceByModule.put("RETIREMENT", calculateModulePerformance("RETIREMENT", periodStart, periodEnd, institutionId));
        performanceByModule.put("RESIGNATION", calculateModulePerformance("RESIGNATION", periodStart, periodEnd, institutionId));
        
        // Volume by module
        Map<String, Long> volumeByModule = new HashMap<>();
        volumeByModule.put("CONFIRMATION", getModuleVolume("CONFIRMATION", periodStart, periodEnd, institutionId));
        volumeByModule.put("LWOP", getModuleVolume("LWOP", periodStart, periodEnd, institutionId));
        volumeByModule.put("PROMOTION", getModuleVolume("PROMOTION", periodStart, periodEnd, institutionId));
        volumeByModule.put("COMPLAINTS", getComplaintVolume(periodStart, periodEnd, institutionId));
        volumeByModule.put("RETIREMENT", getModuleVolume("RETIREMENT", periodStart, periodEnd, institutionId));
        volumeByModule.put("RESIGNATION", getModuleVolume("RESIGNATION", periodStart, periodEnd, institutionId));
        
        return AnalyticsReportDto.SystemPerformance.builder()
            .requestCompletionRate(completionRate)
            .averageProcessingTime(avgProcessingTime)
            .userSatisfactionScore(satisfactionScore)
            .totalTransactions(totalRequests)
            .performanceByModule(performanceByModule)
            .volumeByModule(volumeByModule)
            .build();
    }

    /**
     * Get comparison analytics
     */
    public List<AnalyticsReportDto.ComparisonData> getComparisonAnalytics(
            LocalDateTime currentPeriodStart, LocalDateTime currentPeriodEnd,
            LocalDateTime previousPeriodStart, LocalDateTime previousPeriodEnd,
            String category, String institutionId, String userId) {
        
        log.info("Generating comparison analytics for category: {}", category);
        
        List<AnalyticsReportDto.ComparisonData> comparisons = new ArrayList<>();
        
        switch (category.toUpperCase()) {
            case "REQUESTS":
                comparisons.addAll(generateRequestComparisons(
                    currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
                break;
            case "COMPLAINTS":
                comparisons.addAll(generateComplaintComparisons(
                    currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
                break;
            case "PERFORMANCE":
                comparisons.addAll(generatePerformanceComparisons(
                    currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
                break;
            case "EMPLOYEES":
                comparisons.addAll(generateEmployeeComparisons(
                    currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
                break;
            default:
                comparisons.addAll(generateAllComparisons(
                    currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
        }
        
        return comparisons;
    }

    /**
     * Get KPIs
     */
    public Map<String, Object> getKPIs(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                      String institutionId, String userId) {
        log.info("Generating KPIs for period: {} to {}", periodStart, periodEnd);
        
        Map<String, Object> kpis = new HashMap<>();
        
        // Request KPIs
        long totalRequests = institutionId != null ? 
            requestRepository.countByEmployeeInstitutionId(institutionId) :
            requestRepository.count();
        
        long approvedRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndStatus(
                institutionId, com.zanzibar.csms.entity.enums.RequestStatus.APPROVED) :
            requestRepository.countByStatus(com.zanzibar.csms.entity.enums.RequestStatus.APPROVED);
        
        double approvalRate = totalRequests > 0 ? (double) approvedRequests / totalRequests * 100 : 0.0;
        
        // SLA KPIs
        Double slaCompliance = slaTrackerRepository.getSLAComplianceRate(periodStart, periodEnd);
        if (slaCompliance == null) slaCompliance = 0.0;
        
        // Complaint KPIs
        long totalComplaints = institutionId != null ?
            complaintRepository.countByComplainantInstitutionId(institutionId) :
            complaintRepository.count();
        
        long resolvedComplaints = complaintRepository.countByStatus(
            com.zanzibar.csms.entity.enums.ComplaintStatus.RESOLVED);
        
        double resolutionRate = totalComplaints > 0 ? (double) resolvedComplaints / totalComplaints * 100 : 0.0;
        
        // Employee KPIs
        long totalEmployees = institutionId != null ?
            employeeRepository.countByInstitutionId(institutionId) :
            employeeRepository.count();
        
        long activeEmployees = institutionId != null ?
            employeeRepository.countByInstitutionIdAndIsActive(institutionId, true) :
            employeeRepository.countByIsActive(true);
        
        // Build KPIs map
        kpis.put("totalRequests", totalRequests);
        kpis.put("approvalRate", approvalRate);
        kpis.put("slaCompliance", slaCompliance);
        kpis.put("totalComplaints", totalComplaints);
        kpis.put("complaintResolutionRate", resolutionRate);
        kpis.put("totalEmployees", totalEmployees);
        kpis.put("activeEmployees", activeEmployees);
        kpis.put("employeeUtilizationRate", totalEmployees > 0 ? (double) activeEmployees / totalEmployees * 100 : 0.0);
        
        // Add processing time metrics
        Double avgProcessingTime = slaTrackerRepository.getAverageProcessingTimeInHours(periodStart, periodEnd);
        kpis.put("avgProcessingTimeHours", avgProcessingTime != null ? avgProcessingTime : 0.0);
        
        // Add satisfaction metrics
        Double avgSatisfaction = complaintRepository.getAverageSatisfactionRating(periodStart, periodEnd);
        kpis.put("avgSatisfactionRating", avgSatisfaction != null ? avgSatisfaction : 0.0);
        
        return kpis;
    }

    /**
     * Get bottlenecks analysis
     */
    public Map<String, Object> getBottlenecksAnalysis(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                                     String institutionId, String userId) {
        log.info("Generating bottlenecks analysis");
        
        Map<String, Object> bottlenecks = new HashMap<>();
        
        // Find overdue requests
        List<com.zanzibar.csms.entity.SLATracker> overdueRequests = slaTrackerRepository.findOverdueRequests();
        Map<String, Long> overdueByType = overdueRequests.stream()
            .collect(Collectors.groupingBy(
                sla -> sla.getRequest().getRequestType().name(),
                Collectors.counting()
            ));
        
        // Find most escalated request types
        List<Object[]> escalatedTypes = slaTrackerRepository.findMostEscalatedRequestTypes();
        Map<String, Long> escalationsByType = escalatedTypes.stream()
            .collect(Collectors.toMap(
                arr -> arr[0].toString(),
                arr -> (Long) arr[1]
            ));
        
        // Find workflow bottlenecks (requests stuck in specific stages)
        Map<String, Long> workflowBottlenecks = new HashMap<>();
        // This would analyze workflow steps and identify stages with longest processing times
        
        bottlenecks.put("overdueRequestsByType", overdueByType);
        bottlenecks.put("escalationsByType", escalationsByType);
        bottlenecks.put("workflowBottlenecks", workflowBottlenecks);
        bottlenecks.put("totalOverdueRequests", overdueRequests.size());
        
        return bottlenecks;
    }

    /**
     * Get predictive analytics
     */
    public Map<String, Object> getPredictiveAnalytics(String predictionType, Integer horizonDays, 
                                                     String institutionId, String userId) {
        log.info("Generating predictive analytics for type: {}", predictionType);
        
        Map<String, Object> predictions = new HashMap<>();
        
        // This is a placeholder for predictive analytics
        // In a real implementation, you would use machine learning models
        switch (predictionType.toUpperCase()) {
            case "REQUEST_VOLUME":
                predictions.put("predictedVolume", generateVolumePredicition(horizonDays, institutionId));
                break;
            case "COMPLAINT_TRENDS":
                predictions.put("predictedComplaints", generateComplaintPrediction(horizonDays, institutionId));
                break;
            case "RESOURCE_UTILIZATION":
                predictions.put("predictedUtilization", generateUtilizationPrediction(horizonDays, institutionId));
                break;
            default:
                predictions.put("error", "Unknown prediction type");
        }
        
        predictions.put("predictionType", predictionType);
        predictions.put("horizonDays", horizonDays);
        predictions.put("generatedAt", LocalDateTime.now());
        
        return predictions;
    }

    /**
     * Export analytics data
     */
    public String exportAnalyticsData(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                    String format, List<String> categories, 
                                    String institutionId, String userId) {
        log.info("Exporting analytics data in format: {}", format);
        
        // Generate export file
        String exportId = "ANALYTICS_EXPORT_" + System.currentTimeMillis();
        String fileName = String.format("analytics_export_%s_%s.%s", 
            periodStart.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            periodEnd.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            format.toLowerCase());
        
        // This would generate the actual export file
        // For now, return a placeholder URL
        return "/api/analytics/download/" + fileName;
    }

    // Helper methods for generating trend data, comparisons, etc.
    
    private List<AnalyticsReportDto.TrendData> generateTrendData(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        List<AnalyticsReportDto.TrendData> trends = new ArrayList<>();
        
        // Request trends
        trends.add(generateRequestTrends(periodStart, periodEnd, institutionId));
        
        // Complaint trends
        trends.add(generateComplaintTrends(periodStart, periodEnd, institutionId));
        
        // Performance trends
        trends.add(generatePerformanceTrends(periodStart, periodEnd, institutionId));
        
        return trends;
    }

    private List<AnalyticsReportDto.TrendData> generateTrendDataForCategory(LocalDateTime periodStart, LocalDateTime periodEnd, 
                                                                           String category, String institutionId) {
        List<AnalyticsReportDto.TrendData> trends = new ArrayList<>();
        
        switch (category.toUpperCase()) {
            case "REQUESTS":
                trends.add(generateRequestTrends(periodStart, periodEnd, institutionId));
                break;
            case "COMPLAINTS":
                trends.add(generateComplaintTrends(periodStart, periodEnd, institutionId));
                break;
            case "PERFORMANCE":
                trends.add(generatePerformanceTrends(periodStart, periodEnd, institutionId));
                break;
        }
        
        return trends;
    }

    private AnalyticsReportDto.TrendData generateRequestTrends(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        // Generate request trend data points
        List<AnalyticsReportDto.DataPoint> dataPoints = new ArrayList<>();
        
        // This would generate actual trend data based on historical data
        // For now, create sample data points
        // Generate sample data points for trend analysis
        dataPoints.add(AnalyticsReportDto.DataPoint.builder()
            .timestamp(periodStart)
            .label(periodStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .value(100.0)
            .build());
        
        dataPoints.add(AnalyticsReportDto.DataPoint.builder()
            .timestamp(periodEnd)
            .label(periodEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .value(120.0)
            .build());
        
        return AnalyticsReportDto.TrendData.builder()
            .category("REQUESTS")
            .metric("Daily Request Count")
            .dataPoints(dataPoints)
            .trendDirection(calculateTrendDirection(dataPoints))
            .changePercentage(calculateChangePercentage(dataPoints))
            .build();
    }

    private AnalyticsReportDto.TrendData generateComplaintTrends(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        // Similar implementation for complaint trends
        List<AnalyticsReportDto.DataPoint> dataPoints = new ArrayList<>();
        
        return AnalyticsReportDto.TrendData.builder()
            .category("COMPLAINTS")
            .metric("Daily Complaint Count")
            .dataPoints(dataPoints)
            .trendDirection("STABLE")
            .changePercentage(0.0)
            .build();
    }

    private AnalyticsReportDto.TrendData generatePerformanceTrends(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        // Similar implementation for performance trends
        List<AnalyticsReportDto.DataPoint> dataPoints = new ArrayList<>();
        
        return AnalyticsReportDto.TrendData.builder()
            .category("PERFORMANCE")
            .metric("Daily Processing Time")
            .dataPoints(dataPoints)
            .trendDirection("IMPROVING")
            .changePercentage(-5.2)
            .build();
    }

    private List<AnalyticsReportDto.ComparisonData> generateComparisonData(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        List<AnalyticsReportDto.ComparisonData> comparisons = new ArrayList<>();
        
        // Calculate previous period for comparison
        long periodDays = java.time.Duration.between(periodStart, periodEnd).toDays();
        LocalDateTime previousPeriodStart = periodStart.minusDays(periodDays);
        LocalDateTime previousPeriodEnd = periodStart.minusDays(1);
        
        // Generate comparisons for different categories
        comparisons.addAll(generateRequestComparisons(periodStart, periodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
        comparisons.addAll(generateComplaintComparisons(periodStart, periodEnd, previousPeriodStart, previousPeriodEnd, institutionId));
        
        return comparisons;
    }

    private List<AnalyticsReportDto.ComparisonData> generateRequestComparisons(
            LocalDateTime currentStart, LocalDateTime currentEnd, 
            LocalDateTime previousStart, LocalDateTime previousEnd, String institutionId) {
        
        List<AnalyticsReportDto.ComparisonData> comparisons = new ArrayList<>();
        
        long currentRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionId(institutionId) :
            requestRepository.count();
        
        long previousRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionId(institutionId) :
            requestRepository.count();
        
        double changePercentage = previousRequests > 0 ? 
            ((double) (currentRequests - previousRequests) / previousRequests) * 100 : 0.0;
        
        comparisons.add(AnalyticsReportDto.ComparisonData.builder()
            .category("REQUESTS")
            .currentPeriodLabel("Current Period")
            .previousPeriodLabel("Previous Period")
            .currentValue((double) currentRequests)
            .previousValue((double) previousRequests)
            .changePercentage(changePercentage)
            .changeDirection(changePercentage > 0 ? "INCREASE" : changePercentage < 0 ? "DECREASE" : "STABLE")
            .build());
        
        return comparisons;
    }

    private List<AnalyticsReportDto.ComparisonData> generateComplaintComparisons(
            LocalDateTime currentStart, LocalDateTime currentEnd, 
            LocalDateTime previousStart, LocalDateTime previousEnd, String institutionId) {
        
        List<AnalyticsReportDto.ComparisonData> comparisons = new ArrayList<>();
        
        long currentComplaints = institutionId != null ?
            complaintRepository.countByComplainantInstitutionId(institutionId) :
            complaintRepository.count();
        
        long previousComplaints = institutionId != null ?
            complaintRepository.countByComplainantInstitutionId(institutionId) :
            complaintRepository.count();
        
        double changePercentage = previousComplaints > 0 ? 
            ((double) (currentComplaints - previousComplaints) / previousComplaints) * 100 : 0.0;
        
        comparisons.add(AnalyticsReportDto.ComparisonData.builder()
            .category("COMPLAINTS")
            .currentPeriodLabel("Current Period")
            .previousPeriodLabel("Previous Period")
            .currentValue((double) currentComplaints)
            .previousValue((double) previousComplaints)
            .changePercentage(changePercentage)
            .changeDirection(changePercentage > 0 ? "INCREASE" : changePercentage < 0 ? "DECREASE" : "STABLE")
            .build());
        
        return comparisons;
    }

    private List<AnalyticsReportDto.ComparisonData> generatePerformanceComparisons(
            LocalDateTime currentStart, LocalDateTime currentEnd, 
            LocalDateTime previousStart, LocalDateTime previousEnd, String institutionId) {
        return new ArrayList<>();
    }

    private List<AnalyticsReportDto.ComparisonData> generateEmployeeComparisons(
            LocalDateTime currentStart, LocalDateTime currentEnd, 
            LocalDateTime previousStart, LocalDateTime previousEnd, String institutionId) {
        return new ArrayList<>();
    }

    private List<AnalyticsReportDto.ComparisonData> generateAllComparisons(
            LocalDateTime currentStart, LocalDateTime currentEnd, 
            LocalDateTime previousStart, LocalDateTime previousEnd, String institutionId) {
        
        List<AnalyticsReportDto.ComparisonData> comparisons = new ArrayList<>();
        comparisons.addAll(generateRequestComparisons(currentStart, currentEnd, previousStart, previousEnd, institutionId));
        comparisons.addAll(generateComplaintComparisons(currentStart, currentEnd, previousStart, previousEnd, institutionId));
        return comparisons;
    }

    private Map<String, Object> generateSummaryData(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        Map<String, Object> summary = new HashMap<>();
        
        // Request summary
        long totalRequests = institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndSubmissionDateBetween(institutionId, periodStart, periodEnd) :
            requestRepository.countBySubmissionDateBetween(periodStart, periodEnd);
        
        // Complaint summary
        long totalComplaints = institutionId != null ?
            complaintRepository.countByComplainantInstitutionIdAndSubmissionDateBetween(institutionId, periodStart, periodEnd) :
            complaintRepository.countBySubmissionDateBetween(periodStart, periodEnd);
        
        summary.put("totalRequests", totalRequests);
        summary.put("totalComplaints", totalComplaints);
        summary.put("periodStart", periodStart);
        summary.put("periodEnd", periodEnd);
        summary.put("institutionId", institutionId);
        
        return summary;
    }

    // Helper methods for calculations
    
    private double calculateModulePerformance(String module, LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        // Calculate performance metrics for specific module
        // This would involve complex calculations based on completion rates, processing times, etc.
        return 85.0; // Placeholder
    }

    private double calculateComplaintPerformance(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        // Calculate complaint resolution performance
        return 78.0; // Placeholder
    }

    private long getModuleVolume(String module, LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        // Get request volume for specific module
        return institutionId != null ?
            requestRepository.countByEmployeeInstitutionIdAndSubmissionDateBetween(institutionId, periodStart, periodEnd) :
            requestRepository.countBySubmissionDateBetween(periodStart, periodEnd);
    }

    private long getComplaintVolume(LocalDateTime periodStart, LocalDateTime periodEnd, String institutionId) {
        return institutionId != null ?
            complaintRepository.countByComplainantInstitutionIdAndSubmissionDateBetween(institutionId, periodStart, periodEnd) :
            complaintRepository.countBySubmissionDateBetween(periodStart, periodEnd);
    }

    private String calculateTrendDirection(List<AnalyticsReportDto.DataPoint> dataPoints) {
        if (dataPoints.size() < 2) return "STABLE";
        
        double first = dataPoints.get(0).getValue();
        double last = dataPoints.get(dataPoints.size() - 1).getValue();
        
        if (last > first) return "INCREASING";
        if (last < first) return "DECREASING";
        return "STABLE";
    }

    private double calculateChangePercentage(List<AnalyticsReportDto.DataPoint> dataPoints) {
        if (dataPoints.size() < 2) return 0.0;
        
        double first = dataPoints.get(0).getValue();
        double last = dataPoints.get(dataPoints.size() - 1).getValue();
        
        return first > 0 ? ((last - first) / first) * 100 : 0.0;
    }

    private Map<String, Object> generateVolumePredicition(Integer horizonDays, String institutionId) {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("predictedVolume", 150); // Placeholder
        prediction.put("confidence", 75.0);
        return prediction;
    }

    private Map<String, Object> generateComplaintPrediction(Integer horizonDays, String institutionId) {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("predictedComplaints", 25); // Placeholder
        prediction.put("confidence", 70.0);
        return prediction;
    }

    private Map<String, Object> generateUtilizationPrediction(Integer horizonDays, String institutionId) {
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("predictedUtilization", 85.0); // Placeholder
        prediction.put("confidence", 80.0);
        return prediction;
    }
}