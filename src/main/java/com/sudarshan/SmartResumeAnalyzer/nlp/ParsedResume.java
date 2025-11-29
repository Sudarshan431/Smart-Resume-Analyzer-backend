package com.sudarshan.SmartResumeAnalyzer.nlp;

import lombok.Data;

import java.util.List;

@Data
public class ParsedResume {
    private String summary;
    private List<String> skills;
    private List<String> education;
    private List<String> experience;
    private List<String> projects;
    private Integer totalExperienceMonths;
}
