package com.starcourse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcourse.entity.TeacherProfile;
import com.starcourse.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final TeacherProfileRepository profileRepository;
    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public TeacherProfile generateProfile(TeacherProfile profile) {
        String prompt = buildPrompt(profile);
        String llmResponse = deepSeekClient.chat(prompt);

        parseAndApply(llmResponse, profile);
        return profileRepository.save(profile);
    }

    String buildPrompt(TeacherProfile profile) {
        String template = loadTemplate("prompt/questionnaire.md");

        String methodology = loadTemplate("prompt/_shared/methodology.txt");
        String toneGuide = loadTemplate("prompt/_shared/tone-guide.txt");

        String prompt = template
                .replace("{{subject}}", nullSafe(profile.getSubject()))
                .replace("{{gradeLevel}}", nullSafe(profile.getGradeLevel()))
                .replace("{{style}}", nullSafe(profile.getStyle()))
                .replace("{{strengths}}", nullSafe(profile.getStrengths()))
                .replace("{{shootableContent}}", nullSafe(profile.getShootableContent()))
                .replace("{{frequency}}", nullSafe(profile.getFrequency()))
                .replace("{{targetAudience}}", nullSafe(profile.getTargetAudience()))
                .replace("{{pricingRange}}", nullSafe(profile.getPricingRange()))
                .replace("{{differentiator}}", nullSafe(profile.getDifferentiator()))
                .replace("{{platformPreference}}", nullSafe(profile.getPlatformPreference()))
                .replace("{{include \"methodology\"}}", methodology)
                .replace("{{include \"tone-guide\"}}", toneGuide);

        return prompt;
    }

    void parseAndApply(String llmResponse, TeacherProfile profile) {
        try {
            String json = extractJson(llmResponse);
            JsonNode node = objectMapper.readTree(json);

            profile.setNickname(node.get("nickname").asText());
            profile.setAvatarSuggestion(node.get("avatarSuggestion").asText());
            profile.setPositioning(node.get("positioning").asText());
        } catch (IOException e) {
            log.error("Failed to parse LLM response: {}", llmResponse, e);
            throw new RuntimeException("Invalid JSON response from LLM", e);
        }
    }

    private String extractJson(String response) {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("No JSON object found in LLM response");
        }
        return response.substring(start, end + 1);
    }

    private String loadTemplate(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Template not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template: " + path, e);
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
