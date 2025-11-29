package com.sudarshan.SmartResumeAnalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_descriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String companyName;

    @Lob
    private String jdText;

    @Lob
    private String requiredSkillsJson;   // JSON array: ["java","spring boot"]
    @Lob
    private String preferredSkillsJson;

    private Integer minExperienceMonths;
    private Integer maxExperienceMonths;
}
