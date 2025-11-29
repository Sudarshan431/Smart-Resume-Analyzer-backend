package com.sudarshan.SmartResumeAnalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Resume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;        // "Backend Resume v1"
    private String filePath;     // path to stored file

    @Lob
    private String rawText;      // full extracted text

    @Lob
    private String parsedSummary;

    @Lob
    private String parsedSkillsJson;     // JSON string
    @Lob
    private String parsedEducationJson;
    @Lob
    private String parsedExperienceJson;
    @Lob
    private String parsedProjectsJson;

    private Integer totalExperienceMonths;
    private Integer versionNumber;
}