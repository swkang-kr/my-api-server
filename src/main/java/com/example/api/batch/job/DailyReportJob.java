package com.example.api.batch.job;

import com.example.api.batch.listener.JobCompletionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DailyReportJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionListener jobCompletionListener;

    @Bean
    public Job dailyReportJob() {
        return new JobBuilder("dailyReportJob", jobRepository)
                .listener(jobCompletionListener)
                .start(reportGenerationStep())
                .build();
    }

    @Bean
    public Step reportGenerationStep() {
        return new StepBuilder("reportGenerationStep", jobRepository)
                .tasklet(reportTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet reportTasklet() {
        return (contribution, chunkContext) -> {
            log.info("Generating daily report...");

            // Example: Generate report logic
            // Count users, calculate statistics, etc.

            log.info("Daily report generated successfully");
            return RepeatStatus.FINISHED;
        };
    }

}
