package com.starcourse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcourse.entity.ScriptFeedback;
import com.starcourse.entity.TopicCandidate;
import com.starcourse.repository.ScriptFeedbackRepository;
import com.starcourse.repository.TopicCandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewriteService {

    private final DeepSeekClient deepSeekClient;
    private final ScriptFeedbackRepository scriptFeedbackRepository;
    private final TopicCandidateRepository topicCandidateRepository;
    private final ObjectMapper objectMapper;

    public ScriptFeedback rewrite(UUID topicCandidateId, String direction) {
        TopicCandidate candidate = topicCandidateRepository.findById(topicCandidateId)
                .orElseThrow(() -> new RuntimeException("TopicCandidate not found: " + topicCandidateId));

        String prompt = buildPrompt(candidate.getFullScript(), direction);
        log.info("Rewriting script {} with direction: {}", topicCandidateId, direction);

        String llmResponse = deepSeekClient.chat(prompt);
        String rewrittenContent = parseResponse(llmResponse);

        ScriptFeedback feedback = ScriptFeedback.builder()
                .topicCandidateId(topicCandidateId)
                .direction(direction)
                .originalContent(candidate.getFullScript())
                .rewrittenContent(rewrittenContent)
                .build();

        ScriptFeedback saved = scriptFeedbackRepository.save(feedback);
        log.info("Saved rewrite feedback {} for script {}", saved.getId(), topicCandidateId);
        return saved;
    }

    public List<ScriptFeedback> getHistory(UUID topicCandidateId) {
        return scriptFeedbackRepository.findByTopicCandidateIdOrderByCreatedAtDesc(topicCandidateId);
    }

    String buildPrompt(String originalContent, String direction) {
        String template = loadTemplate("prompt/rewrite.md");
        String toneGuide = loadTemplate("prompt/_shared/tone-guide.txt");

        return template
                .replace("{{originalContent}}", originalContent)
                .replace("{{direction}}", direction)
                .replace("{{include \"tone-guide\"}}", toneGuide);
    }

    String parseResponse(String llmResponse) {
        try {
            String json = extractJson(llmResponse);
            JsonNode node = objectMapper.readTree(json);
            return node.get("rewrittenContent").asText();
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
}
