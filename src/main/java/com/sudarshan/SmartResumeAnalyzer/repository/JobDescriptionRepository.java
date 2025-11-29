package com.sudarshan.SmartResumeAnalyzer.repository;

import com.sudarshan.SmartResumeAnalyzer.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, Long> {

    // (Optional) if you link JD to User later:
    List<JobDescription> findByTitleContainingIgnoreCase(String title);

    // you can add: List<JobDescription> findByCreatedById(Long userId);
    // once createdBy field is added in entity
}