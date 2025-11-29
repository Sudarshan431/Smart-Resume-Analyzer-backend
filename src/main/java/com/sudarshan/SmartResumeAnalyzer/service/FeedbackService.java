package com.sudarshan.SmartResumeAnalyzer.service;

import com.sudarshan.SmartResumeAnalyzer.dto.auth.analysis.FeedbackResult;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FeedbackService {

    public FeedbackResult generateFeedback(
            double skillMatch,
            double expMatch,
            double eduMatch,
            Set<String> requiredSkills,
            Set<String> resumeSkills,
            Set<String> missingRequired,
            double skillsSectionScore,
            double experienceSectionScore,
            double projectsSectionScore
    ) {
        StringBuilder summary = new StringBuilder();
        StringBuilder details = new StringBuilder();

        summary.append(String.format(
                "Skill match: %.1f%%, Experience match: %.1f%%, Education match: %.1f%%.",
                skillMatch, expMatch, eduMatch
        ));

        if (!missingRequired.isEmpty()) {
            details.append("You are missing important required skills: ")
                    .append(String.join(", ", missingRequired))
                    .append(". Consider learning or showcasing them via projects or internships.\n\n");
        }

        if (skillsSectionScore < 60) {
            details.append("Your skills section could be improved. Clearly list technical skills that match this JD (")
                    .append(String.join(", ", requiredSkills))
                    .append(") in a dedicated ‘Skills’ section with bullet points.\n\n");
        }

        if (experienceSectionScore < 60) {
            details.append("Your experience section doesn’t strongly highlight the required tech stack. ")
                    .append("Add bullet points under each role describing how you used these technologies and what impact you delivered.\n\n");
        }

        if (projectsSectionScore < 60) {
            details.append("Projects can help you stand out. Add or expand projects that use the target stack and mention tools, responsibilities, and outcomes.\n\n");
        }

        if (expMatch < 50) {
            details.append("Your overall experience is lower than what the JD asks for. Emphasize internships, freelance work, hackathons, and academic projects more clearly.\n\n");
        }

        if (details.length() == 0) {
            details.append("Overall, your resume is quite aligned with this job description. You can further polish wording, formatting, and add more measurable impact in your bullet points.");
        }

        return new FeedbackResult(summary.toString(), details.toString());
    }
}
