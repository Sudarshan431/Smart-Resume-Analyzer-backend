package com.sudarshan.SmartResumeAnalyzer.dto.auth.analysis;

import lombok.Data;

@Data
public class AnalysisRequest {
    private Long resumeId;
    private Long jobDescriptionId;
}
