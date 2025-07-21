package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.dashboard.AnalyticsReportDto;
import com.zanzibar.csms.entity.enums.*;
import com.zanzibar.csms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAnalyticsService {

    private final EmployeeRepository employeeRepository;
    private final RequestRepository requestRepository;
    private final ComplaintRepository complaintRepository;
    private final InstitutionRepository institutionRepository;
    private final AuditLogRepository auditLogRepository;

    public AnalyticsReportDto generateSystemAnalytics(LocalDateTime startDate, LocalDateTime endDate, String username) {
        String reportId = generateReportId();
        
        return AnalyticsReportDto.builder()
            .reportId(reportId)
            .reportName("System Analytics Report")
            .reportType("SYSTEM_WIDE")
            .generatedAt(LocalDateTime.now())
            .periodStart(startDate)
            .periodEnd(endDate)
            .generatedBy(username)
            .systemPerformance(buildSystemPerformance(startDate, endDate))
            .trends(buildTrendAnalysis(startDate, endDate))
            .comparisons(buildComparisonAnalysis(startDate, endDate))
            .summary(buildSummaryMetrics(startDate, endDate))
            .build();
    }

    public AnalyticsReportDto generateInstitutionalAnalytics(String institutionId, LocalDateTime startDate, 
                                                           LocalDateTime endDate, String username) {
        String reportId = generateReportId();
        
        return AnalyticsReportDto.builder()
            .reportId(reportId)
            .reportName("Institutional Analytics Report")
            .reportType("INSTITUTIONAL")
            .generatedAt(LocalDateTime.now())
            .periodStart(startDate)
            .periodEnd(endDate)
            .generatedBy(username)
            .systemPerformance(buildInstitutionalPerformance(institutionId, startDate, endDate))
            .trends(buildInstitutionalTrends(institutionId, startDate, endDate))
            .summary(buildInstitutionalSummary(institutionId, startDate, endDate))
            .build();
    }

    public Map<String, Object> getSystemHealthMetrics() {
        Map<String, Object> health = new HashMap<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayAgo = now.minusDays(1);
        LocalDateTime weekAgo = now.minusDays(7);
        LocalDateTime monthAgo = now.minusMonths(1);

        // System activity metrics
        health.put("dailyLogins", auditLogRepository.countByActionAndTimestampBetween("LOGIN", dayAgo, now));
        health.put("weeklyRequests", requestRepository.countBySubmissionDateBetween(weekAgo, now));
        health.put("monthlyComplaints", complaintRepository.countBySubmissionDateBetween(monthAgo, now));
        
        // Performance indicators
        health.put("averageRequestProcessingTime", calculateAverageProcessingTime(monthAgo, now));
        health.put("systemUptime", calculateSystemUptime());
        health.put("activeUsersToday", getActiveUsersToday());
        
        // Alert indicators
        health.put("overdueRequests", getOverdueRequestsCount());
        health.put("pendingEscalations", getPendingEscalationsCount());
        health.put("systemErrors", getSystemErrorsCount(dayAgo, now));

        return health;
    }

    public List<Map<String, Object>> getWorkflowBottlenecks() {
        List<Map<String, Object>> bottlenecks = new ArrayList<>();
        
        // Analyze request processing bottlenecks
        Map<String, Object> requestBottleneck = new HashMap<>();
        requestBottleneck.put("process", "REQUEST_APPROVAL");
        requestBottleneck.put("averageStepTime", calculateAverageStepTime("REQUEST_APPROVAL"));
        requestBottleneck.put("bottleneckStep", identifyBottleneckStep("REQUEST_APPROVAL"));
        requestBottleneck.put("impactLevel", "HIGH");
        bottlenecks.add(requestBottleneck);

        // Analyze complaint investigation bottlenecks
        Map<String, Object> complaintBottleneck = new HashMap<>();
        complaintBottleneck.put("process", "COMPLAINT_INVESTIGATION");
        complaintBottleneck.put("averageStepTime", calculateAverageStepTime("COMPLAINT_INVESTIGATION"));
        complaintBottleneck.put("bottleneckStep", identifyBottleneckStep("COMPLAINT_INVESTIGATION"));
        complaintBottleneck.put("impactLevel", "MEDIUM");
        bottlenecks.add(complaintBottleneck);

        return bottlenecks;
    }

    public Map<String, Object> getUserProductivityMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> productivity = new HashMap<>();

        // By user role
        Map<String, Long> actionsByRole = Arrays.stream(UserRole.values())
            .collect(Collectors.toMap(
                UserRole::name,
                role -> auditLogRepository.countByUserRoleAndTimestampBetween(role, startDate, endDate)
            ));

        // Top performers
        List<Map<String, Object>> topUsers = getTopPerformingUsers(startDate, endDate);

        // Workload distribution
        Map<String, Double> workloadDistribution = calculateWorkloadDistribution(startDate, endDate);

        productivity.put("actionsByRole", actionsByRole);
        productivity.put("topPerformers", topUsers);
        productivity.put("workloadDistribution", workloadDistribution);

        return productivity;
    }

    private AnalyticsReportDto.SystemPerformance buildSystemPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        Double requestCompletionRate = calculateRequestCompletionRate(startDate, endDate);
        Double avgProcessingTime = calculateAverageProcessingTime(startDate, endDate);
        Double userSatisfactionScore = calculateUserSatisfactionScore(startDate, endDate);
        Long totalTransactions = getTotalTransactions(startDate, endDate);

        Map<String, Double> performanceByModule = new HashMap<>();
        performanceByModule.put("EMPLOYEE_MANAGEMENT", 95.0);
        performanceByModule.put("REQUEST_PROCESSING", requestCompletionRate);
        performanceByModule.put("COMPLAINT_MANAGEMENT", 88.0);

        Map<String, Long> volumeByModule = new HashMap<>();
        volumeByModule.put("REQUESTS", requestRepository.countBySubmissionDateBetween(startDate, endDate));
        volumeByModule.put("COMPLAINTS", complaintRepository.countBySubmissionDateBetween(startDate, endDate));
        volumeByModule.put("EMPLOYEES", employeeRepository.countByCreatedAtBetween(startDate, endDate));

        return AnalyticsReportDto.SystemPerformance.builder()
            .requestCompletionRate(requestCompletionRate)
            .averageProcessingTime(avgProcessingTime)
            .userSatisfactionScore(userSatisfactionScore)
            .totalTransactions(totalTransactions)
            .performanceByModule(performanceByModule)
            .volumeByModule(volumeByModule)
            .build();
    }

    private List<AnalyticsReportDto.TrendData> buildTrendAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        List<AnalyticsReportDto.TrendData> trends = new ArrayList<>();

        // Request volume trend
        AnalyticsReportDto.TrendData requestTrend = AnalyticsReportDto.TrendData.builder()
            .category("REQUESTS")
            .metric("VOLUME")
            .dataPoints(generateRequestVolumeDataPoints(startDate, endDate))
            .trendDirection("INCREASING")
            .changePercentage(12.5)
            .build();
        trends.add(requestTrend);

        // Complaint resolution time trend
        AnalyticsReportDto.TrendData resolutionTrend = AnalyticsReportDto.TrendData.builder()
            .category("COMPLAINTS")
            .metric("RESOLUTION_TIME")
            .dataPoints(generateResolutionTimeDataPoints(startDate, endDate))
            .trendDirection("DECREASING")
            .changePercentage(-8.2)
            .build();
        trends.add(resolutionTrend);

        return trends;
    }

    private List<AnalyticsReportDto.ComparisonData> buildComparisonAnalysis(LocalDateTime startDate, LocalDateTime endDate) {
        List<AnalyticsReportDto.ComparisonData> comparisons = new ArrayList<>();

        LocalDateTime previousPeriodStart = startDate.minusDays(endDate.toLocalDate().getDayOfMonth());
        LocalDateTime previousPeriodEnd = endDate.minusDays(endDate.toLocalDate().getDayOfMonth());

        // Request volume comparison
        Long currentRequests = requestRepository.countBySubmissionDateBetween(startDate, endDate);
        Long previousRequests = requestRepository.countBySubmissionDateBetween(previousPeriodStart, previousPeriodEnd);
        
        AnalyticsReportDto.ComparisonData requestComparison = AnalyticsReportDto.ComparisonData.builder()
            .category("REQUEST_VOLUME")
            .currentPeriodLabel("Current Period")
            .previousPeriodLabel("Previous Period")
            .currentValue(currentRequests.doubleValue())
            .previousValue(previousRequests.doubleValue())
            .changePercentage(calculatePercentageChange(previousRequests, currentRequests))
            .changeDirection(currentRequests > previousRequests ? "INCREASE" : "DECREASE")
            .build();
        comparisons.add(requestComparison);

        return comparisons;
    }

    private Map<String, Object> buildSummaryMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> summary = new HashMap<>();

        summary.put("totalEmployees", employeeRepository.count());
        summary.put("activeInstitutions", institutionRepository.countByIsActive(true));
        summary.put("requestsProcessed", requestRepository.countBySubmissionDateBetween(startDate, endDate));
        summary.put("complaintsResolved", complaintRepository.countByStatusAndSubmissionDateBetween(
            ComplaintStatus.RESOLVED, startDate, endDate));
        summary.put("systemEfficiency", calculateSystemEfficiency(startDate, endDate));

        return summary;
    }

    private AnalyticsReportDto.SystemPerformance buildInstitutionalPerformance(String institutionId, 
                                                                              LocalDateTime startDate, 
                                                                              LocalDateTime endDate) {
        // Implementation for institutional performance metrics
        return AnalyticsReportDto.SystemPerformance.builder()
            .requestCompletionRate(90.0)
            .averageProcessingTime(5.2)
            .userSatisfactionScore(4.2)
            .totalTransactions(100L)
            .performanceByModule(new HashMap<>())
            .volumeByModule(new HashMap<>())
            .build();
    }

    private List<AnalyticsReportDto.TrendData> buildInstitutionalTrends(String institutionId, 
                                                                       LocalDateTime startDate, 
                                                                       LocalDateTime endDate) {
        // Implementation for institutional trend analysis
        return new ArrayList<>();
    }

    private Map<String, Object> buildInstitutionalSummary(String institutionId, 
                                                         LocalDateTime startDate, 
                                                         LocalDateTime endDate) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("institutionEmployees", employeeRepository.countByInstitutionId(institutionId));
        summary.put("institutionRequests", requestRepository.countByEmployeeInstitutionIdAndSubmissionDateBetween(
            institutionId, startDate, endDate));
        summary.put("institutionComplaints", complaintRepository.countByComplainantInstitutionIdAndSubmissionDateBetween(
            institutionId, startDate, endDate));

        return summary;
    }

    // Helper methods
    private String generateReportId() {
        return "RPT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    private Double calculateRequestCompletionRate(LocalDateTime startDate, LocalDateTime endDate) {
        Long totalRequests = requestRepository.countBySubmissionDateBetween(startDate, endDate);
        Long completedRequests = requestRepository.countByStatusAndSubmissionDateBetween(
            RequestStatus.APPROVED, startDate, endDate) + 
            requestRepository.countByStatusAndSubmissionDateBetween(
                RequestStatus.REJECTED, startDate, endDate);
        
        return totalRequests > 0 ? (completedRequests.doubleValue() / totalRequests.doubleValue()) * 100 : 0.0;
    }

    private Double calculateAverageProcessingTime(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation to calculate average processing time
        return 4.5; // Placeholder
    }

    private Double calculateUserSatisfactionScore(LocalDateTime startDate, LocalDateTime endDate) {
        Double avgRating = complaintRepository.getAverageSatisfactionRating(startDate, endDate);
        return avgRating != null ? avgRating : 0.0;
    }

    private Long getTotalTransactions(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.countByTimestampBetween(startDate, endDate);
    }

    private List<AnalyticsReportDto.DataPoint> generateRequestVolumeDataPoints(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation to generate data points for request volume over time
        return new ArrayList<>();
    }

    private List<AnalyticsReportDto.DataPoint> generateResolutionTimeDataPoints(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation to generate data points for resolution time trends
        return new ArrayList<>();
    }

    private Double calculatePercentageChange(Long previous, Long current) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((double)(current - previous) / previous.doubleValue()) * 100;
    }

    private Double calculateSystemEfficiency(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation to calculate overall system efficiency
        return 92.5; // Placeholder
    }

    private Double calculateSystemUptime() {
        // Implementation to calculate system uptime percentage
        return 99.8; // Placeholder
    }

    private Long getActiveUsersToday() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return auditLogRepository.countDistinctUsersByTimestampAfter(today);
    }

    private Long getOverdueRequestsCount() {
        // Implementation to count overdue requests
        return 15L; // Placeholder
    }

    private Long getPendingEscalationsCount() {
        // Implementation to count pending escalations
        return 3L; // Placeholder
    }

    private Long getSystemErrorsCount(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.countByIsSuccessfulAndTimestampBetween(false, startDate, endDate);
    }

    private Double calculateAverageStepTime(String processType) {
        // Implementation to calculate average time per workflow step
        return 2.5; // Placeholder
    }

    private String identifyBottleneckStep(String processType) {
        // Implementation to identify workflow bottleneck
        return "REVIEW_STEP"; // Placeholder
    }

    private List<Map<String, Object>> getTopPerformingUsers(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation to get top performing users
        return new ArrayList<>();
    }

    private Map<String, Double> calculateWorkloadDistribution(LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation to calculate workload distribution
        return new HashMap<>();
    }
}