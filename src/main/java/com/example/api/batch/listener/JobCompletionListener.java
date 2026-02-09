package com.example.api.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job started: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job ID: {}", jobExecution.getId());
        log.info("Start time: {}", jobExecution.getStartTime());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job finished: {}", jobExecution.getJobInstance().getJobName());
        log.info("Job ID: {}", jobExecution.getId());
        log.info("Status: {}", jobExecution.getStatus());
        log.info("End time: {}", jobExecution.getEndTime());

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job completed successfully!");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job failed!");
            jobExecution.getAllFailureExceptions().forEach(e ->
                    log.error("Failure: {}", e.getMessage()));
        }
    }

}
