package com.sudarshan.SmartResumeAnalyzer.nlp;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JobDescriptionParser {

    // Matches patterns like:
    // "1-3 years", "2 - 5 years", "1 to 3 years"
    private static final Pattern RANGE_YEARS =
            Pattern.compile("(\\d+)\\s*(?:-|to)\\s*(\\d+)\\s+years", Pattern.CASE_INSENSITIVE);

    // Matches:
    // "2+ years", "3 years", "1 year"
    private static final Pattern SINGLE_YEARS =
            Pattern.compile("(\\d+)\\+?\\s+years", Pattern.CASE_INSENSITIVE);

    /**
     * Extract minimum required experience in MONTHS
     */
    public Integer extractMinExperienceMonths(String jdText) {
        if (jdText == null) return null;

        Matcher rangeMatcher = RANGE_YEARS.matcher(jdText);
        if (rangeMatcher.find()) {
            int minYears = Integer.parseInt(rangeMatcher.group(1));
            return minYears * 12;
        }

        Matcher singleMatcher = SINGLE_YEARS.matcher(jdText);
        if (singleMatcher.find()) {
            int years = Integer.parseInt(singleMatcher.group(1));
            return years * 12;
        }

        return null; // Unable to detect
    }

    /**
     * Extract maximum required experience in MONTHS
     */
    public Integer extractMaxExperienceMonths(String jdText) {
        if (jdText == null) return null;

        Matcher rangeMatcher = RANGE_YEARS.matcher(jdText);
        if (rangeMatcher.find()) {
            int maxYears = Integer.parseInt(rangeMatcher.group(2));
            return maxYears * 12;
        }

        // If JD only says "2 years" or "2+ years", no upper limit
        return null;
    }
}
