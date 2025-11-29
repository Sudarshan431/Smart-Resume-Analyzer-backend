package com.sudarshan.SmartResumeAnalyzer.nlp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudarshan.SmartResumeAnalyzer.entity.Skill;
import com.sudarshan.SmartResumeAnalyzer.repository.SkillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SkillExtractor {

    private final SkillRepository skillRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<Skill> cachedSkills;

    @PostConstruct
    public void loadSkills() {
        cachedSkills = skillRepository.findAll();
    }

    public Set<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return Set.of();

        String lower = text.toLowerCase();
        Set<String> found = new HashSet<>();

        for (Skill skill : cachedSkills) {
            String name = skill.getName();
            if (name != null && lower.contains(name.toLowerCase())) {
                found.add(name);
            }

            if (skill.getAliasesJson() != null && !skill.getAliasesJson().isBlank()) {
                List<String> aliases = fromJsonList(skill.getAliasesJson());
                for (String alias : aliases) {
                    if (lower.contains(alias.toLowerCase())) {
                        found.add(name);
                        break;
                    }
                }
            }
        }

        return found;
    }

    private List<String> fromJsonList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
