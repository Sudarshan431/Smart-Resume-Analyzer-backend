package com.sudarshan.SmartResumeAnalyzer.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ScoringService {

    public double computeSkillMatchScore(Set<String> resumeSkills, Set<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 0.0;
        if (resumeSkills == null) return 0.0;

        long matched = requiredSkills.stream()
                .filter(s -> resumeSkills.contains(s))
                .count();

        return (matched * 100.0) / requiredSkills.size();
    }

    public double computeExperienceMatchScore(Integer resumeMonths, Integer jdMinMonths) {
        if (resumeMonths == null || jdMinMonths == null || jdMinMonths == 0) {
            return 0.0;
        }

        if (resumeMonths >= jdMinMonths) {
            return 100.0;
        }

        // proportional score if less than required
        return (resumeMonths * 100.0) / jdMinMonths;
    }

    public double computeEducationMatchScore(List<String> educationLines) {
        if (educationLines == null || educationLines.isEmpty()) return 0.0;

        String text = String.join(" ", educationLines).toLowerCase();

        // extremely simple logic for now: if BTech/BE/BSc appears → 100
        if (text.contains("b.tech") || text.contains("btech") ||
                text.contains("b.e") || text.contains("be ") ||
                text.contains("bsc") || text.contains("b.sc")) {
            return 100.0;
        }

        return 50.0; // some neutral value if we can't detect properly
    }

    public double computeSectionScore(Set<String> jdSkills, List<String> sectionLines) {
        if (jdSkills == null || jdSkills.isEmpty() || sectionLines == null) return 0.0;

        String sectionText = String.join(" ", sectionLines).toLowerCase();
        long count = jdSkills.stream()
                .filter(skill -> sectionText.contains(skill.toLowerCase()))
                .count();

        return (count * 100.0) / jdSkills.size();
    }

    public double computeOverallScore(double skillMatch, double expMatch, double eduMatch) {
        // weights you can tweak later
        return 0.6 * skillMatch + 0.25 * expMatch + 0.15 * eduMatch;
    }
}
