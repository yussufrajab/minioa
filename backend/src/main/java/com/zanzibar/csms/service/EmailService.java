package com.zanzibar.csms.service;

import com.zanzibar.csms.dto.report.ReportDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:CSMS}")
    private String appName;

    @Value("${app.reports.base-url:http://localhost:8080/api/reports/download}")
    private String reportsBaseUrl;

    /**
     * Send report email notification
     */
    @Async
    public void sendReportEmail(ReportDto report, List<String> recipients) {
        log.info("Sending report email for report: {} to {} recipients", report.getReportId(), recipients.size());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipients.toArray(new String[0]));
            message.setSubject(buildReportEmailSubject(report));
            message.setText(buildReportEmailBody(report));
            
            mailSender.send(message);
            log.info("Report email sent successfully for report: {}", report.getReportId());
            
        } catch (Exception e) {
            log.error("Failed to send report email for report: {}", report.getReportId(), e);
            // Don't throw exception as this is an async operation
        }
    }

    /**
     * Send report generation failure notification
     */
    @Async
    public void sendReportFailureEmail(String reportId, String reportName, String recipient, String error) {
        log.info("Sending report failure email for report: {} to: {}", reportId, recipient);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipient);
            message.setSubject(String.format("[%s] Report Generation Failed - %s", appName, reportName));
            message.setText(buildReportFailureEmailBody(reportId, reportName, error));
            
            mailSender.send(message);
            log.info("Report failure email sent successfully for report: {}", reportId);
            
        } catch (Exception e) {
            log.error("Failed to send report failure email for report: {}", reportId, e);
        }
    }

    /**
     * Send scheduled report notification
     */
    @Async
    public void sendScheduledReportEmail(ReportDto report, List<String> recipients) {
        log.info("Sending scheduled report email for report: {} to {} recipients", report.getReportId(), recipients.size());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipients.toArray(new String[0]));
            message.setSubject(buildScheduledReportEmailSubject(report));
            message.setText(buildScheduledReportEmailBody(report));
            
            mailSender.send(message);
            log.info("Scheduled report email sent successfully for report: {}", report.getReportId());
            
        } catch (Exception e) {
            log.error("Failed to send scheduled report email for report: {}", report.getReportId(), e);
        }
    }

    /**
     * Build email subject for report
     */
    private String buildReportEmailSubject(ReportDto report) {
        return String.format("[%s] %s - %s Report Ready", 
            appName, 
            report.getReportName(), 
            report.getReportType());
    }

    /**
     * Build email body for report
     */
    private String buildReportEmailBody(ReportDto report) {
        StringBuilder body = new StringBuilder();
        
        body.append("Dear User,\n\n");
        body.append("Your requested report has been generated successfully.\n\n");
        body.append("Report Details:\n");
        body.append("- Report Name: ").append(report.getReportName()).append("\n");
        body.append("- Report Type: ").append(report.getReportType()).append("\n");
        body.append("- Generated At: ").append(report.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        body.append("- Period: ").append(report.getPeriodStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .append(" to ").append(report.getPeriodEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        body.append("- Format: ").append(report.getFormat()).append("\n");
        body.append("- Total Records: ").append(report.getTotalRecords()).append("\n");
        
        if (report.getDescription() != null) {
            body.append("- Description: ").append(report.getDescription()).append("\n");
        }
        
        body.append("\nDownload Link: ").append(report.getDownloadUrl()).append("\n\n");
        body.append("Please note that this link will expire after 30 days.\n\n");
        body.append("If you have any questions or concerns, please contact the system administrator.\n\n");
        body.append("Best regards,\n");
        body.append("Civil Service Management System\n");
        body.append("Government of Zanzibar");
        
        return body.toString();
    }

    /**
     * Build email subject for scheduled report
     */
    private String buildScheduledReportEmailSubject(ReportDto report) {
        return String.format("[%s] Scheduled Report - %s (%s)", 
            appName, 
            report.getReportName(), 
            report.getReportType());
    }

    /**
     * Build email body for scheduled report
     */
    private String buildScheduledReportEmailBody(ReportDto report) {
        StringBuilder body = new StringBuilder();
        
        body.append("Dear User,\n\n");
        body.append("Your scheduled report has been generated automatically.\n\n");
        body.append("Report Details:\n");
        body.append("- Report Name: ").append(report.getReportName()).append("\n");
        body.append("- Report Type: ").append(report.getReportType()).append("\n");
        body.append("- Generated At: ").append(report.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        body.append("- Period: ").append(report.getPeriodStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .append(" to ").append(report.getPeriodEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        body.append("- Format: ").append(report.getFormat()).append("\n");
        body.append("- Total Records: ").append(report.getTotalRecords()).append("\n");
        
        body.append("\nDownload Link: ").append(report.getDownloadUrl()).append("\n\n");
        body.append("This is an automated email for your scheduled report.\n");
        body.append("If you no longer wish to receive these reports, please contact the system administrator.\n\n");
        body.append("Best regards,\n");
        body.append("Civil Service Management System\n");
        body.append("Government of Zanzibar");
        
        return body.toString();
    }

    /**
     * Build email body for report failure
     */
    private String buildReportFailureEmailBody(String reportId, String reportName, String error) {
        StringBuilder body = new StringBuilder();
        
        body.append("Dear User,\n\n");
        body.append("We're sorry to inform you that your requested report failed to generate.\n\n");
        body.append("Report Details:\n");
        body.append("- Report ID: ").append(reportId).append("\n");
        body.append("- Report Name: ").append(reportName).append("\n");
        body.append("- Error: ").append(error).append("\n\n");
        body.append("Please try generating the report again or contact the system administrator if the problem persists.\n\n");
        body.append("Best regards,\n");
        body.append("Civil Service Management System\n");
        body.append("Government of Zanzibar");
        
        return body.toString();
    }
}