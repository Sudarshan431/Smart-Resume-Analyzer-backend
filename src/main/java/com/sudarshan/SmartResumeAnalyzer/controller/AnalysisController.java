package com.sudarshan.SmartResumeAnalyzer.controller;

import com.sudarshan.SmartResumeAnalyzer.dto.auth.analysis.AnalysisRequest;
import com.sudarshan.SmartResumeAnalyzer.entity.AnalysisReport;
import com.sudarshan.SmartResumeAnalyzer.service.AnalysisService;
import com.sudarshan.SmartResumeAnalyzer.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<AnalysisReport> analyze(@RequestBody AnalysisRequest request) {
        Long userId = authService.getCurrentUserId();
        AnalysisReport report = analysisService.analyze(request, userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/by-resume/{resumeId}")
    public ResponseEntity<List<AnalysisReport>> getByResume(@PathVariable Long resumeId) {
        Long userId = authService.getCurrentUserId();
        return ResponseEntity.ok(analysisService.getReportsByResume(resumeId, userId));
    }
}
