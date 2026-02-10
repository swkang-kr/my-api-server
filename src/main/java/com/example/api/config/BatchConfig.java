package com.example.api.config;

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

/**
 * Spring Batch Configuration
 * - Automatically creates beans like JobRepository and JobLauncher
 * - spring.batch.job.enabled=false: Prevents automatic job execution on startup
 * - In Spring Boot 3.x, @EnableBatchProcessing is not needed and can interfere with auto-configuration
 */
@Configuration
public class BatchConfig extends DefaultBatchConfiguration {

    private final DataSource dataSource;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected DataSource getDataSource() {
        return dataSource;
    }

}
