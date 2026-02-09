package com.example.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {

    private final JobLauncher jobLauncher;

    @PostMapping("/run/{jobName}")
    public ResponseEntity<Map<String, Object>> runJob(@PathVariable String jobName) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Job을 실행하려면 Job 빈을 주입받아야 함
            // 현재는 placeholder
            response.put("message", "Batch job execution endpoint - Job beans implementation needed");
            response.put("jobName", jobName);
            response.put("status", "PENDING");

            log.info("Batch job requested: {}", jobName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to run batch job: {}", jobName, e);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status/{jobName}")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable String jobName) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch job status endpoint - Implementation needed");
        response.put("jobName", jobName);
        return ResponseEntity.ok(response);
    }

}
