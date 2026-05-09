package com.starcourse.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcourse.entity.TeacherProfile;
import com.starcourse.entity.TopicCandidate;
import com.starcourse.entity.TopicCandidateStatus;
import com.starcourse.repository.TeacherProfileRepository;
import com.starcourse.repository.TopicCandidateRepository;
import com.starcourse.service.playbook.PlaybookMaterial;
import com.starcourse.service.playbook.PlaybookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptGenerationService {

    private final DeepSeekClient deepSeekClient;
    private final PlaybookService playbookService;
    private final TeacherProfileRepository profileRepository;
    private final TopicCandidateRepository topicCandidateRepository;
    private final ObjectMapper objectMapper;

    public TopicCandidate generateSingleScript(UUID teacherProfileId, String narrativeContext, String pullReason) {
        TeacherProfile profile = profileRepository.findById(teacherProfileId)
                .orElseThrow(() -> new RuntimeException("TeacherProfile not found: " + teacherProfileId));

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);
        String prompt = buildPrompt(profile, materials, narrativeContext);

        log.info("Generating single script for teacher {} with narrative: {}", teacherProfileId, narrativeContext);
        String llmResponse = deepSeekClient.chat(prompt);

        TopicCandidate candidate = parseSingleResponse(llmResponse, teacherProfileId, narrativeContext, pullReason);
        TopicCandidate saved = topicCandidateRepository.save(candidate);
        log.info("Saved script candidate {} for teacher {}", saved.getId(), teacherProfileId);
        return saved;
    }

    String buildPrompt(TeacherProfile profile, List<PlaybookMaterial> materials, String narrativeContext) {
        String template = loadTemplate("prompt/script-generation.md");
        String methodology = loadTemplate("prompt/_shared/methodology.txt");
        String toneGuide = loadTemplate("prompt/_shared/tone-guide.txt");

        String materialsText = materials.stream()
                .map(m -> "【" + m.title() + "】\n" + m.exampleContent())
                .collect(Collectors.joining("\n\n"));

        return template
                .replace("{{count}}", "1")
                .replace("{{nickname}}", nullSafe(profile.getNickname()))
                .replace("{{subject}}", nullSafe(profile.getSubject()))
                .replace("{{gradeLevel}}", nullSafe(profile.getGradeLevel()))
                .replace("{{style}}", nullSafe(profile.getStyle()))
                .replace("{{strengths}}", nullSafe(profile.getStrengths()))
                .replace("{{differentiator}}", nullSafe(profile.getDifferentiator()))
                .replace("{{positioning}}", nullSafe(profile.getPositioning()))
                .replace("{{materials}}", materialsText)
                .replace("{{narrativeContext}}", nullSafe(narrativeContext))
                .replace("{{include \"methodology\"}}", methodology)
                .replace("{{include \"tone-guide\"}}", toneGuide);
    }

    TopicCandidate parseSingleResponse(String llmResponse, UUID teacherProfileId,
                                         String narrativeContext, String pullReason) {
        try {
            String json = extractJson(llmResponse);
            JsonNode node;

            // Handle both single object and array responses
            if (json.startsWith("[")) {
                List<JsonNode> nodes = objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<>() {});
                if (nodes.isEmpty()) {
                    throw new RuntimeException("Empty JSON array in LLM response");
                }
                node = nodes.get(0);
            } else {
                node = objectMapper.readTree(json);
            }

            return TopicCandidate.builder()
                    .teacherProfileId(teacherProfileId)
                    .title(node.get("title").asText())
                    .hook(node.get("hook").asText())
                    .fullScript(node.get("fullScript").asText())
                    .shootingTips(node.has("shootingTips") ? node.get("shootingTips").asText() : null)
                    .storyboard(node.has("storyboard") ? node.get("storyboard").toString() : null)
                    .narrativeContext(narrativeContext)
                    .pullReason(pullReason)
                    .status(TopicCandidateStatus.DRAFT)
                    .version(1)
                    .build();
        } catch (IOException e) {
            log.error("Failed to parse LLM response: {}", llmResponse, e);
            throw new RuntimeException("Invalid JSON response from LLM", e);
        }
    }

    private String extractJson(String response) {
        // Try array first, then object
        int arrayStart = response.indexOf('[');
        int arrayEnd = response.lastIndexOf(']');
        int objStart = response.indexOf('{');
        int objEnd = response.lastIndexOf('}');

        if (arrayStart != -1 && arrayEnd != -1 && arrayEnd > arrayStart) {
            if (objStart == -1 || arrayStart < objStart) {
                return response.substring(arrayStart, arrayEnd + 1);
            }
        }
        if (objStart != -1 && objEnd != -1 && objEnd > objStart) {
            return response.substring(objStart, objEnd + 1);
        }
        throw new RuntimeException("No JSON found in LLM response");
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
