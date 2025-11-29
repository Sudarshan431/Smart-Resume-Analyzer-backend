package com.sudarshan.SmartResumeAnalyzer.dto.auth.jd;

import lombok.Data;

@Data
public class JobDescriptionRequest {
    private String title;
    private String companyName;
    private String jdText;
}