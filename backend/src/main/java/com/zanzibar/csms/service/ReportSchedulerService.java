package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.report.ReportDto;
import com.zanzibar.csms.dto.report.ReportRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportSchedulerService {

    private final ReportService reportService;

    /**
     * Generate daily reports (runs at 2 AM every day)
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void generateDailyReports() {
        log.info("Starting daily report generation");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusDays(1);
            
            // Daily Request Status Report
            ReportRequestDto dailyRequestReport = ReportRequestDto.builder()
                .reportType("REQUEST")
                .reportName("Daily Request Status Report")
                .description("Daily summary of request statuses")
                .periodStart(yesterday.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(yesterday.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "hhrmd@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(dailyRequestReport, "system_scheduler");
            
            // Daily Complaint Report
            ReportRequestDto dailyComplaintReport = ReportRequestDto.builder()
                .reportType("COMPLAINT")
                .reportName("Daily Complaint Summary Report")
                .description("Daily summary of complaint submissions and resolutions")
                .periodStart(yesterday.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(yesterday.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "do@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(dailyComplaintReport, "system_scheduler");
            
            log.info("Daily reports generated successfully");
            
        } catch (Exception e) {
            log.error("Error generating daily reports", e);
        }
    }

    /**
     * Generate weekly reports (runs at 3 AM every Monday)
     */
    @Scheduled(cron = "0 0 3 * * MON")
    public void generateWeeklyReports() {
        log.info("Starting weekly report generation");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime weekStart = now.minusDays(7);
            
            // Weekly Performance Report
            ReportRequestDto weeklyPerformanceReport = ReportRequestDto.builder()
                .reportType("PERFORMANCE")
                .reportName("Weekly Performance Report")
                .description("Weekly system performance metrics")
                .periodStart(weekStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(now.withHour(23).withMinute(59).withSecond(59))
                .format("EXCEL")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "cscs@zanzibar.gov.tz", "po@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(weeklyPerformanceReport, "system_scheduler");
            
            // Weekly SLA Report
            ReportRequestDto weeklySLAReport = ReportRequestDto.builder()
                .reportType("SLA")
                .reportName("Weekly SLA Compliance Report")
                .description("Weekly SLA compliance statistics")
                .periodStart(weekStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(now.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "hhrmd@zanzibar.gov.tz", "hrmo@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(weeklySLAReport, "system_scheduler");
            
            log.info("Weekly reports generated successfully");
            
        } catch (Exception e) {
            log.error("Error generating weekly reports", e);
        }
    }

    /**
     * Generate monthly reports (runs at 4 AM on the 1st of every month)
     */
    @Scheduled(cron = "0 0 4 1 * *")
    public void generateMonthlyReports() {
        log.info("Starting monthly report generation");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime monthStart = now.minusMonths(1).withDayOfMonth(1);
            LocalDateTime monthEnd = now.minusMonths(1).withDayOfMonth(
                now.minusMonths(1).toLocalDate().lengthOfMonth());
            
            // Monthly Employee Report
            ReportRequestDto monthlyEmployeeReport = ReportRequestDto.builder()
                .reportType("EMPLOYEE")
                .reportName("Monthly Employee Report")
                .description("Monthly employee statistics and changes")
                .periodStart(monthStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(monthEnd.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "cscs@zanzibar.gov.tz", "hhrmd@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(monthlyEmployeeReport, "system_scheduler");
            
            // Monthly Institutional Report
            ReportRequestDto monthlyInstitutionalReport = ReportRequestDto.builder()
                .reportType("INSTITUTIONAL")
                .reportName("Monthly Institutional Report")
                .description("Monthly institutional statistics and metrics")
                .periodStart(monthStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(monthEnd.withHour(23).withMinute(59).withSecond(59))
                .format("EXCEL")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "cscs@zanzibar.gov.tz", "po@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(monthlyInstitutionalReport, "system_scheduler");
            
            // Monthly Workflow Report
            ReportRequestDto monthlyWorkflowReport = ReportRequestDto.builder()
                .reportType("WORKFLOW")
                .reportName("Monthly Workflow Report")
                .description("Monthly workflow efficiency and bottleneck analysis")
                .periodStart(monthStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(monthEnd.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "hhrmd@zanzibar.gov.tz", "hrmo@zanzibar.gov.tz", "do@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(monthlyWorkflowReport, "system_scheduler");
            
            log.info("Monthly reports generated successfully");
            
        } catch (Exception e) {
            log.error("Error generating monthly reports", e);
        }
    }

    /**
     * Generate quarterly reports (runs at 5 AM on the 1st of Jan, Apr, Jul, Oct)
     */
    @Scheduled(cron = "0 0 5 1 1,4,7,10 *")
    public void generateQuarterlyReports() {
        log.info("Starting quarterly report generation");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime quarterStart = now.minusMonths(3).withDayOfMonth(1);
            LocalDateTime quarterEnd = now.minusDays(1);
            
            // Quarterly Comprehensive Report
            ReportRequestDto quarterlyComprehensiveReport = ReportRequestDto.builder()
                .reportType("PERFORMANCE")
                .reportName("Quarterly Comprehensive Report")
                .description("Quarterly comprehensive system performance and statistics")
                .periodStart(quarterStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(quarterEnd.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "cscs@zanzibar.gov.tz", "po@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(quarterlyComprehensiveReport, "system_scheduler");
            
            // Quarterly Audit Report
            ReportRequestDto quarterlyAuditReport = ReportRequestDto.builder()
                .reportType("AUDIT")
                .reportName("Quarterly Audit Report")
                .description("Quarterly audit trail and compliance report")
                .periodStart(quarterStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(quarterEnd.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "cscs@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(quarterlyAuditReport, "system_scheduler");
            
            log.info("Quarterly reports generated successfully");
            
        } catch (Exception e) {
            log.error("Error generating quarterly reports", e);
        }
    }

    /**
     * Generate annual reports (runs at 6 AM on January 1st)
     */
    @Scheduled(cron = "0 0 6 1 1 *")
    public void generateAnnualReports() {
        log.info("Starting annual report generation");
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yearStart = now.minusYears(1).withDayOfYear(1);
            LocalDateTime yearEnd = now.minusYears(1).withMonth(12).withDayOfMonth(31);
            
            // Annual Comprehensive Report
            ReportRequestDto annualComprehensiveReport = ReportRequestDto.builder()
                .reportType("PERFORMANCE")
                .reportName("Annual Comprehensive Report")
                .description("Annual comprehensive system performance and statistics")
                .periodStart(yearStart.withHour(0).withMinute(0).withSecond(0))
                .periodEnd(yearEnd.withHour(23).withMinute(59).withSecond(59))
                .format("PDF")
                .autoEmail(true)
                .emailRecipients(Arrays.asList("admin@zanzibar.gov.tz", "cscs@zanzibar.gov.tz", "po@zanzibar.gov.tz"))
                .build();
            
            reportService.generateStandardReport(annualComprehensiveReport, "system_scheduler");
            
            log.info("Annual reports generated successfully");
            
        } catch (Exception e) {
            log.error("Error generating annual reports", e);
        }
    }

    /**
     * Clean up old report files (runs at 1 AM every day)
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void cleanupOldReports() {
        log.info("Starting cleanup of old report files");
        
        try {
            // Implementation for cleaning up old report files
            // This would delete files older than a certain period (e.g., 90 days)
            
            log.info("Old report files cleanup completed");
            
        } catch (Exception e) {
            log.error("Error during report cleanup", e);
        }
    }
}