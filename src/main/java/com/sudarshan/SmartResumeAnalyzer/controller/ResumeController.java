package com.sudarshan.SmartResumeAnalyzer.controller;

import com.sudarshan.SmartResumeAnalyzer.entity.Resume;
import com.sudarshan.SmartResumeAnalyzer.service.AuthService;
import com.sudarshan.SmartResumeAnalyzer.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final AuthService authService;

    @PostMapping("/upload")
    public ResponseEntity<Resume> uploadResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "version", required = false) Integer version
    ) throws IOException {

        Long userId = authService.getCurrentUserId();
        Resume saved = resumeService.uploadResume(file, userId, title, version);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getMyResumes() {
        Long userId = authService.getCurrentUserId();
        return ResponseEntity.ok(resumeService.getUserResumes(userId));
    }
}