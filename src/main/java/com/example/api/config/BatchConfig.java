package com.example.api.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Spring Batch Configuration
 * - Automatically creates beans like JobRepository and JobLauncher
 * - spring.batch.job.enabled=false: Prevents automatic job execution on startup
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfiguration {

    @Autowired
    private DataSource dataSource;

    // Extends DefaultBatchConfiguration to use default settings
    // Override methods below if customization is needed

    // @Override
    // protected DataSource getDataSource() {
    //     return dataSource;
    // }

}
