package com.sudarshan.SmartResumeAnalyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;    // "spring boot"

    private String category; // "backend", "cloud" etc.

    @Lob
    private String aliasesJson;  // JSON: ["spring-boot","springboot"]
}
