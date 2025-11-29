package com.sudarshan.SmartResumeAnalyzer.dto.auth.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResult {
    private String summary;
    private String details;
}
