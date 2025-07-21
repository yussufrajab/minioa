package com.zanzibar.csms.dto.report;

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
public class ReportDto {
    
    private String reportId;
    private String reportName;
    private String reportType;
    private String description;
    private LocalDateTime generatedAt;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String generatedBy;
    private String institutionId;
    private String institutionName;
    private String format; // PDF, EXCEL, CSV
    private String status; // GENERATING, COMPLETED, FAILED
    private String downloadUrl;
    private Long totalRecords;
    private Map<String, Object> parameters;
    private List<ReportSectionDto> sections;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportSectionDto {
        private String sectionId;
        private String sectionName;
        private String sectionType;
        private Integer order;
        private List<ReportDataDto> data;
        private Map<String, Object> metadata;
        private ChartDataDto chartData;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDataDto {
        private String id;
        private String name;
        private String type;
        private Object value;
        private String unit;
        private String description;
        private Map<String, Object> attributes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartDataDto {
        private String chartType; // BAR, PIE, LINE, AREA
        private String title;
        private List<ChartSeriesDto> series;
        private List<String> labels;
        private Map<String, Object> options;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartSeriesDto {
        private String name;
        private String type;
        private List<Object> data;
        private String color;
        private Map<String, Object> options;
    }
}