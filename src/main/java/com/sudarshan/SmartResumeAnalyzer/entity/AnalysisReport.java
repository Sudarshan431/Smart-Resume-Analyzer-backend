package com.sudarshan.SmartResumeAnalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analysis_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AnalysisReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    private JobDescription jobDescription;

    private Double overallFitScore;
    private Double skillMatchScore;
    private Double experienceMatchScore;
    private Double educationMatchScore;

    private Double summaryScore;
    private Double skillsSectionScore;
    private Double experienceSectionScore;
    private Double projectsSectionScore;

    @Lob
    private String matchedSkillsJson;
    @Lob
    private String missingRequiredSkillsJson;
    @Lob
    private String missingPreferredSkillsJson;

    @Lob
    private String feedbackSummary;
    @Lob
    private String feedbackDetails;
}
