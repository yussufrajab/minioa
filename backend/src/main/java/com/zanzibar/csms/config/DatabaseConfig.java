package com.zanzibar.csms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database Configuration for CSMS
 * Configures Spring Boot to work with existing Prisma database schema
 * 
 * Note: Removed custom DataSource bean to let Spring Boot auto-configure it
 * from application.properties
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.zanzibar.csms.repository")
@EntityScan(basePackages = "com.zanzibar.csms.entity")
@EnableTransactionManagement
public class DatabaseConfig {
    // Let Spring Boot auto-configure DataSource from application.properties
}