package com.sudarshan.SmartResumeAnalyzer.controller;

import com.sudarshan.SmartResumeAnalyzer.dto.auth.jd.JobDescriptionRequest;
import com.sudarshan.SmartResumeAnalyzer.entity.JobDescription;
import com.sudarshan.SmartResumeAnalyzer.service.JobDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jds")
@RequiredArgsConstructor
public class JobDescriptionController {

    private final JobDescriptionService jobDescriptionService;

    /**
     * Create a new Job Description.
     * Requires a valid JWT (because of your SecurityConfig).
     *
     * POST /api/jds
     */
    @PostMapping
    public ResponseEntity<JobDescription> create(@RequestBody JobDescriptionRequest request) {
        JobDescription saved = jobDescriptionService.createJobDescription(request);
        return ResponseEntity.ok(saved);
    }

    /**
     * List all JDs.
     *
     * GET /api/jds
     */
    @GetMapping
    public ResponseEntity<List<JobDescription>> getAll() {
        return ResponseEntity.ok(jobDescriptionService.getAll());
    }

    /**
     * Get a particular JD by ID.
     *
     * GET /api/jds/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobDescription> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobDescriptionService.getById(id));
    }
}
