package com.payvance.erp_saas.erp.controller;

import com.payvance.erp_saas.erp.entity.TallyWritebackJob;
import com.payvance.erp_saas.erp.repository.TallyWritebackJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sync/writeback")
@RequiredArgsConstructor
@Slf4j
public class WritebackController {

    private final TallyWritebackJobRepository jobRepository;

    @GetMapping("/jobs")
    public ResponseEntity<List<TallyWritebackJob>> getPendingJobs(@RequestParam String companyId) {
        log.info("Fetching pending writeback jobs for/api/v1/sync/writeback/jobs company: {}", companyId);
        List<TallyWritebackJob> jobs = jobRepository.findByCompanyIdAndStatus(companyId, "PENDING");
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/job/status")
    public ResponseEntity<Void> updateJobStatus(@RequestBody Map<String, Object> updateRequest) {
        try {
            Long jobId = Long.valueOf(updateRequest.get("id").toString());
            String status = (String) updateRequest.get("status");
            String errorMessage = (String) updateRequest.getOrDefault("errorMessage", null);

            log.info("Updating job {} status to {}", jobId, status);

            Optional<TallyWritebackJob> jobOpt = jobRepository.findById(jobId);
            if (jobOpt.isPresent()) {
                TallyWritebackJob job = jobOpt.get();
                job.setStatus(status);
                job.setErrorMessage(errorMessage);
                job.setUpdatedAt(LocalDateTime.now());
                jobRepository.save(job);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error updating job status: ", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/job")
    public ResponseEntity<TallyWritebackJob> createJob(@RequestBody TallyWritebackJob job) {
        try {
            log.info("Creating new writeback job for entity: {} type: {}", job.getEntityId(), job.getEntityType());
            job.setCreatedAt(LocalDateTime.now());
            job.setUpdatedAt(LocalDateTime.now());
            job.setStatus("PENDING"); // Default status
            TallyWritebackJob savedJob = jobRepository.save(job);
            return ResponseEntity.ok(savedJob);
        } catch (Exception e) {
            log.error("Error creating writeback job: ", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
