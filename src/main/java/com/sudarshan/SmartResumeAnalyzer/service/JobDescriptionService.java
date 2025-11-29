package com.sudarshan.SmartResumeAnalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.jd.JobDescriptionRequest;
import com.sudarshan.SmartResumeAnalyzer.entity.JobDescription;
import com.sudarshan.SmartResumeAnalyzer.exception.ResourceNotFoundException;
import com.sudarshan.SmartResumeAnalyzer.nlp.JobDescriptionParser;
import com.sudarshan.SmartResumeAnalyzer.nlp.SkillExtractor;
import com.sudarshan.SmartResumeAnalyzer.repository.JobDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobDescriptionService {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobDescriptionParser jobDescriptionParser;
    private final SkillExtractor skillExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create and save a new JobDescription by:
     *  - Extracting skills from JD text using SkillExtractor
     *  - Extracting min/max experience using JobDescriptionParser
     *  - Storing everything in the JobDescription entity
     */
    public JobDescription createJobDescription(JobDescriptionRequest request) {

        // 1️⃣ Extract skills from JD text
        Set<String> jdSkills = skillExtractor.extractSkills(request.getJdText());
        String requiredSkillsJson = toJson(jdSkills);
        String preferredSkillsJson = toJson(List.of()); // not used yet, keep empty

        // 2️⃣ Extract experience requirements
        Integer minExp = jobDescriptionParser.extractMinExperienceMonths(request.getJdText());
        Integer maxExp = jobDescriptionParser.extractMaxExperienceMonths(request.getJdText());

        // 3️⃣ Build entity
        JobDescription jd = JobDescription.builder()
                .title(request.getTitle())
                .companyName(request.getCompanyName())
                .jdText(request.getJdText())
                .requiredSkillsJson(requiredSkillsJson)
                .preferredSkillsJson(preferredSkillsJson)
                .minExperienceMonths(minExp)
                .maxExperienceMonths(maxExp)
                .build();

        // 4️⃣ Save in DB
        return jobDescriptionRepository.save(jd);
    }

    /**
     * Get all job descriptions (for listing in UI).
     */
    public List<JobDescription> getAll() {
        return jobDescriptionRepository.findAll();
    }

    /**
     * Get a single JobDescription by ID or throw 404.
     */
    public JobDescription getById(Long id) {
        return jobDescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found"));
    }

    // Helper: convert object to JSON string
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }
}