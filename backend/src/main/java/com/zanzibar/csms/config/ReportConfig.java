package com.zanzibar.csms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class ReportConfig {

    @Value("${app.reports.thread-pool.core-size:5}")
    private int corePoolSize;

    @Value("${app.reports.thread-pool.max-size:10}")
    private int maxPoolSize;

    @Value("${app.reports.thread-pool.queue-capacity:100}")
    private int queueCapacity;

    @Value("${app.reports.thread-pool.thread-name-prefix:report-}")
    private String threadNamePrefix;

    /**
     * Thread pool executor for report generation
     */
    @Bean(name = "reportExecutor")
    public Executor reportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * Report generation configuration properties
     */
    @Bean
    public ReportProperties reportProperties() {
        return new ReportProperties();
    }

    public static class ReportProperties {
        private String storagePath = "./reports";
        private String baseUrl = "http://localhost:8080/api/reports/download";
        private int fileRetentionDays = 90;
        private boolean enableScheduledReports = true;
        private String defaultFormat = "PDF";
        private int maxReportsPerUser = 10;
        private long maxFileSizeBytes = 50 * 1024 * 1024; // 50MB

        // Getters and setters
        public String getStoragePath() { return storagePath; }
        public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
        
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        
        public int getFileRetentionDays() { return fileRetentionDays; }
        public void setFileRetentionDays(int fileRetentionDays) { this.fileRetentionDays = fileRetentionDays; }
        
        public boolean isEnableScheduledReports() { return enableScheduledReports; }
        public void setEnableScheduledReports(boolean enableScheduledReports) { this.enableScheduledReports = enableScheduledReports; }
        
        public String getDefaultFormat() { return defaultFormat; }
        public void setDefaultFormat(String defaultFormat) { this.defaultFormat = defaultFormat; }
        
        public int getMaxReportsPerUser() { return maxReportsPerUser; }
        public void setMaxReportsPerUser(int maxReportsPerUser) { this.maxReportsPerUser = maxReportsPerUser; }
        
        public long getMaxFileSizeBytes() { return maxFileSizeBytes; }
        public void setMaxFileSizeBytes(long maxFileSizeBytes) { this.maxFileSizeBytes = maxFileSizeBytes; }
    }
}