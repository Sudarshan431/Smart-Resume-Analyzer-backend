package com.sudarshan.SmartResumeAnalyzer.nlp;

import com.sudarshan.SmartResumeAnalyzer.nlp.ParsedResume;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ResumeParser {

    public ParsedResume parse(String rawText) {
        ParsedResume parsed = new ParsedResume();

        // Normalize
        String text = rawText.replace("\r", "");
        String upper = text.toUpperCase();

        parsed.setSummary(extractSection(upper, text, "SUMMARY", "SKILLS", "TECHNICAL SKILLS", "EDUCATION"));
        parsed.setSkills(extractLines(extractSection(upper, text, "SKILLS", "TECHNICAL SKILLS", "EDUCATION", "EXPERIENCE", "WORK EXPERIENCE", "PROJECTS")));
        parsed.setEducation(extractLines(extractSection(upper, text, "EDUCATION", "EXPERIENCE", "WORK EXPERIENCE", "PROJECTS")));
        parsed.setExperience(extractLines(extractSection(upper, text, "EXPERIENCE", "WORK EXPERIENCE", "PROJECTS", "ACHIEVEMENTS")));
        parsed.setProjects(extractLines(extractSection(upper, text, "PROJECTS", "ACHIEVEMENTS", "CERTIFICATIONS", "SKILLS")));

        // TODO: later parse dates to estimate experience
        parsed.setTotalExperienceMonths(0);

        return parsed;
    }

    private String extractSection(String upper, String original,
                                  String sectionTitle, String... untilTitles) {
        int start = upper.indexOf(sectionTitle);
        if (start == -1) return "";

        int end = upper.length();
        for (String until : untilTitles) {
            int idx = upper.indexOf(until, start + sectionTitle.length());
            if (idx != -1 && idx < end) {
                end = idx;
            }
        }
        return original.substring(start, end).trim();
    }

    private List<String> extractLines(String sectionText) {
        if (sectionText == null || sectionText.isBlank()) return List.of();
        return Arrays.stream(sectionText.split("\\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}