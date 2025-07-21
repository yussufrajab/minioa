package com.zanzibar.csms.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReportDto {

    private String reportId;
    private String reportName;
    private String reportType;
    private LocalDateTime generatedAt;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String generatedBy;
    
    private SystemPerformance systemPerformance;
    private List<TrendData> trends;
    private List<ComparisonData> comparisons;
    private Map<String, Object> summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemPerformance {
        private Double requestCompletionRate;
        private Double averageProcessingTime;
        private Double userSatisfactionScore;
        private Long totalTransactions;
        private Map<String, Double> performanceByModule;
        private Map<String, Long> volumeByModule;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String category;
        private String metric;
        private List<DataPoint> dataPoints;
        private String trendDirection;
        private Double changePercentage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private LocalDateTime timestamp;
        private String label;
        private Double value;
        private Map<String, Object> metadata;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonData {
        private String category;
        private String currentPeriodLabel;
        private String previousPeriodLabel;
        private Double currentValue;
        private Double previousValue;
        private Double changePercentage;
        private String changeDirection;
    }
}