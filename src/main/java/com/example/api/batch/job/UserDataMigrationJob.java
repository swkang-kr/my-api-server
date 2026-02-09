package com.example.api.batch.job;

import com.example.api.batch.listener.JobCompletionListener;
import com.example.api.batch.processor.UserItemProcessor;
import com.example.api.batch.reader.UserItemReader;
import com.example.api.batch.writer.UserItemWriter;
import com.example.api.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class UserDataMigrationJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;
    private final UserItemReader userItemReader;
    private final UserItemProcessor userItemProcessor;
    private final UserItemWriter userItemWriter;

    @Bean
    public Job userMigrationJob() {
        return new JobBuilder("userMigrationJob", jobRepository)
                .listener(jobCompletionListener)
                .start(userMigrationStep())
                .build();
    }

    @Bean
    public Step userMigrationStep() {
        return new StepBuilder("userMigrationStep", jobRepository)
                .<User, User>chunk(10, transactionManager)
                .reader(userItemReader)
                .processor(userItemProcessor)
                .writer(userItemWriter)
                .build();
    }

}
