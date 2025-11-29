package com.sudarshan.SmartResumeAnalyzer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudarshan.SmartResumeAnalyzer.entity.Resume;
import com.sudarshan.SmartResumeAnalyzer.entity.User;
import com.sudarshan.SmartResumeAnalyzer.exception.ResourceNotFoundException;
import com.sudarshan.SmartResumeAnalyzer.nlp.ParsedResume;
import com.sudarshan.SmartResumeAnalyzer.nlp.ResumeParser;
import com.sudarshan.SmartResumeAnalyzer.repository.ResumeRepository;
import com.sudarshan.SmartResumeAnalyzer.repository.UserRepository;
import com.sudarshan.SmartResumeAnalyzer.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final FileStorageUtil fileStorageUtil;
    private final ResumeParser resumeParser;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Tika tika = new Tika();

    public Resume uploadResume(MultipartFile file, Long userId, String title, Integer version) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1️⃣ Save file to disk
        String storedPath = fileStorageUtil.save(file, userId);

        // 2️⃣ Extract text using Apache Tika
        String rawText;
        try {
            rawText = tika.parseToString(Path.of(storedPath));
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract text from resume file", e);
        }


        // 3️⃣ Parse into sections
        ParsedResume parsed = resumeParser.parse(rawText);

        // 4️⃣ Build entity
        Resume resume = Resume.builder()
                .user(user)
                .title(title)
                .filePath(storedPath)
                .rawText(rawText)
                .parsedSummary(parsed.getSummary())
                .parsedSkillsJson(toJson(parsed.getSkills()))
                .parsedEducationJson(toJson(parsed.getEducation()))
                .parsedExperienceJson(toJson(parsed.getExperience()))
                .parsedProjectsJson(toJson(parsed.getProjects()))
                .totalExperienceMonths(parsed.getTotalExperienceMonths())
                .versionNumber(version != null ? version : 1)
                .build();

        return resumeRepository.save(resume);
    }

    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    // Helpers
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }

    public List<String> fromJsonList(String json) {
        if (json == null) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}