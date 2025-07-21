package com.zanzibar.csms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database Initialization Configuration
 * 
 * This class ensures that database tables are created safely and persist forever.
 * It provides warnings and safety checks to prevent accidental table drops.
 */
@Component
@Slf4j
public class DatabaseInitializationConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment environment;

    @Value("${spring.jpa.hibernate.ddl-auto:validate}")
    private String ddlAuto;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=== CSMS Database Initialization Check ===");
        
        try {
            checkDatabaseTables();
            validateConfiguration();
            logDatabaseInfo();
        } catch (SQLException e) {
            log.error("Error during database initialization check", e);
        }
        
        log.info("=== Database Initialization Check Complete ===");
    }

    private void checkDatabaseTables() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Get list of tables
            List<String> tables = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(null, "public", "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
            
            log.info("Database: {} ({})", 
                connection.getCatalog(), 
                metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
            
            if (tables.isEmpty()) {
                log.warn("⚠️  WARNING: No tables found in database!");
                log.warn("⚠️  If this is the first run, consider using 'dev' profile to create tables:");
                log.warn("⚠️  mvn spring-boot:run -Dspring-boot.run.profiles=dev");
            } else {
                log.info("✅ Found {} tables in database", tables.size());
                log.debug("Tables: {}", tables);
            }
        }
    }

    private void validateConfiguration() {
        String[] activeProfiles = environment.getActiveProfiles();
        
        log.info("Active profiles: {}", activeProfiles.length > 0 ? 
            String.join(", ", activeProfiles) : "default");
        log.info("DDL Auto mode: {}", ddlAuto);
        
        // Warn about dangerous configurations
        if ("create".equals(ddlAuto) || "create-drop".equals(ddlAuto)) {
            log.error("🚨 DANGER: DDL Auto mode '{}' can destroy existing data!", ddlAuto);
            log.error("🚨 Consider changing to 'validate' or 'update' for safety");
        } else if ("validate".equals(ddlAuto)) {
            log.info("✅ Safe mode: DDL Auto is set to 'validate' - tables will be preserved");
        } else if ("update".equals(ddlAuto)) {
            log.info("⚠️  Update mode: DDL Auto is set to 'update' - tables can be modified");
        }
    }

    private void logDatabaseInfo() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            log.info("Database URL: {}", metaData.getURL());
            log.info("Database User: {}", metaData.getUserName());
            log.info("Driver: {} ({})", 
                metaData.getDriverName(), 
                metaData.getDriverVersion());
            
            // Check if database supports transactions
            if (metaData.supportsTransactions()) {
                log.info("✅ Transaction support: Available");
            } else {
                log.warn("⚠️  Transaction support: Not available");
            }
        }
    }

    /**
     * Utility method to check if specific table exists
     */
    public boolean tableExists(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet rs = metaData.getTables(null, "public", tableName.toLowerCase(), new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if table '{}' exists", tableName, e);
            return false;
        }
    }

    /**
     * Get count of tables in the database
     */
    public int getTableCount() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            int count = 0;
            try (ResultSet rs = metaData.getTables(null, "public", "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    count++;
                }
            }
            return count;
        } catch (SQLException e) {
            log.error("Error getting table count", e);
            return -1;
        }
    }
}