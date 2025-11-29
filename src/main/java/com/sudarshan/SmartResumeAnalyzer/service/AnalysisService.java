package com.sudarshan.SmartResumeAnalyzer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.analysis.AnalysisRequest;
import com.sudarshan.SmartResumeAnalyzer.dto.auth.analysis.FeedbackResult;
import com.sudarshan.SmartResumeAnalyzer.entity.AnalysisReport;
import com.sudarshan.SmartResumeAnalyzer.entity.JobDescription;
import com.sudarshan.SmartResumeAnalyzer.entity.Resume;
import com.sudarshan.SmartResumeAnalyzer.exception.ResourceNotFoundException;
import com.sudarshan.SmartResumeAnalyzer.exception.UnauthorizedException;
import com.sudarshan.SmartResumeAnalyzer.nlp.SkillExtractor;
import com.sudarshan.SmartResumeAnalyzer.repository.AnalysisReportRepository;
import com.sudarshan.SmartResumeAnalyzer.repository.JobDescriptionRepository;
import com.sudarshan.SmartResumeAnalyzer.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final AnalysisReportRepository analysisReportRepository;
    private final SkillExtractor skillExtractor;
    private final ScoringService scoringService;
    private final FeedbackService feedbackService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisReport analyze(AnalysisRequest request, Long currentUserId) {

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        JobDescription jd = jobDescriptionRepository.findById(request.getJobDescriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Job description not found"));

        // ensure user owns the resume
        if (!resume.getUser().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You do not own this resume");
        }

        // 1️⃣ Extract skills
        Set<String> resumeSkills = skillExtractor.extractSkills(resume.getRawText());
        Set<String> requiredSkills = extractRequiredSkillsFromJd(jd);

        // 2️⃣ Scores
        double skillMatch = scoringService.computeSkillMatchScore(resumeSkills, requiredSkills);
        double expMatch = scoringService.computeExperienceMatchScore(
                resume.getTotalExperienceMonths(),
                jd.getMinExperienceMonths()
        );
        double eduMatch = scoringService.computeEducationMatchScore(
                fromJsonList(resume.getParsedEducationJson())
        );

        double skillsSectionScore = scoringService.computeSectionScore(
                requiredSkills,
                fromJsonList(resume.getParsedSkillsJson())
        );
        double experienceSectionScore = scoringService.computeSectionScore(
                requiredSkills,
                fromJsonList(resume.getParsedExperienceJson())
        );
        double projectsSectionScore = scoringService.computeSectionScore(
                requiredSkills,
                fromJsonList(resume.getParsedProjectsJson())
        );

        double summaryScore = scoringService.computeSectionScore(
                requiredSkills,
                List.of(resume.getParsedSummary() == null ? "" : resume.getParsedSummary())
        );

        double overall = scoringService.computeOverallScore(skillMatch, expMatch, eduMatch);

        // 3️⃣ Missing skills
        Set<String> missingRequired = new HashSet<>(requiredSkills);
        missingRequired.removeAll(resumeSkills);

        // 4️⃣ Feedback
        FeedbackResult feedback = feedbackService.generateFeedback(
                skillMatch,
                expMatch,
                eduMatch,
                requiredSkills,
                resumeSkills,
                missingRequired,
                skillsSectionScore,
                experienceSectionScore,
                projectsSectionScore
        );

        // 5️⃣ Save report
        AnalysisReport report = AnalysisReport.builder()
                .resume(resume)
                .jobDescription(jd)
                .overallFitScore(overall)
                .skillMatchScore(skillMatch)
                .experienceMatchScore(expMatch)
                .educationMatchScore(eduMatch)
                .summaryScore(summaryScore)
                .skillsSectionScore(skillsSectionScore)
                .experienceSectionScore(experienceSectionScore)
                .projectsSectionScore(projectsSectionScore)
                .matchedSkillsJson(toJson(resumeSkills))
                .missingRequiredSkillsJson(toJson(missingRequired))
                .missingPreferredSkillsJson(toJson(List.of())) // not used yet
                .feedbackSummary(feedback.getSummary())
                .feedbackDetails(feedback.getDetails())
                .build();

        return analysisReportRepository.save(report);
    }

    public List<AnalysisReport> getReportsByResume(Long resumeId, Long currentUserId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        if (!resume.getUser().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You do not own this resume");
        }
        return analysisReportRepository.findByResumeId(resumeId);
    }

    private Set<String> extractRequiredSkillsFromJd(JobDescription jd) {
        // if we already stored requiredSkillsJson, use it; otherwise fallback to extractor
        if (jd.getRequiredSkillsJson() != null && !jd.getRequiredSkillsJson().isBlank()) {
            List<String> list = fromJsonList(jd.getRequiredSkillsJson());
            return new HashSet<>(list);
        }
        return skillExtractor.extractSkills(jd.getJdText());
    }

    private List<String> fromJsonList(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }
}
