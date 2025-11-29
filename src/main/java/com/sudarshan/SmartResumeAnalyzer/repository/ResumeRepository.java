package com.sudarshan.SmartResumeAnalyzer.repository;

import com.sudarshan.SmartResumeAnalyzer.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // Get all resumes uploaded by a particular user
    List<Resume> findByUserId(Long userId);

    // Optional: find by user and title
    List<Resume> findByUserIdAndTitle(Long userId, String title);
}
