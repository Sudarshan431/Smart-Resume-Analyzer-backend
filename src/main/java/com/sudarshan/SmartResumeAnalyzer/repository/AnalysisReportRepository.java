package com.sudarshan.SmartResumeAnalyzer.repository;

import com.sudarshan.SmartResumeAnalyzer.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {

    // All analyses done for a particular resume
    List<AnalysisReport> findByResumeId(Long resumeId);

    // All analyses done for a particular JD
    List<AnalysisReport> findByJobDescriptionId(Long jobDescriptionId);

    // Optional: sorted by most recent
    List<AnalysisReport> findByResumeIdOrderByIdDesc(Long resumeId);
}