package com.starcourse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcourse.entity.TeacherProfile;
import com.starcourse.entity.TopicCandidate;
import com.starcourse.entity.TopicCandidateStatus;
import com.starcourse.repository.TeacherProfileRepository;
import com.starcourse.repository.TopicCandidateRepository;
import com.starcourse.service.playbook.PlaybookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptGenerationServiceTest {

    @Mock
    private DeepSeekClient deepSeekClient;

    @Mock
    private PlaybookService playbookService;

    @Mock
    private TeacherProfileRepository profileRepository;

    @Mock
    private TopicCandidateRepository topicCandidateRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ScriptGenerationService scriptGenerationService;

    @Test
    void parseSingleResponse_shouldParseJsonObjectIntoCandidate() {
        UUID teacherId = UUID.randomUUID();
        String llmResponse = """
                ```json
                {
                  "title": "3秒速算技巧",
                  "hook": "你知道吗？99%的孩子都在用错误的方法算口算",
                  "fullScript": "今天教大家一个口算速算技巧...",
                  "shootingTips": "正面镜头，手持白板，边写边讲",
                  "storyboard": [
                    {"scene": 1, "duration": "0-3秒", "description": "老师出镜", "voiceover": "你知道吗？"}
                  ]
                }
                ```
                """;

        TopicCandidate result = scriptGenerationService.parseSingleResponse(
                llmResponse, teacherId, "想讲口算技巧", "用户请求");

        assertEquals("3秒速算技巧", result.getTitle());
        assertEquals("你知道吗？99%的孩子都在用错误的方法算口算", result.getHook());
        assertEquals("今天教大家一个口算速算技巧...", result.getFullScript());
        assertEquals("正面镜头，手持白板，边写边讲", result.getShootingTips());
        assertNotNull(result.getStoryboard());
        assertEquals("想讲口算技巧", result.getNarrativeContext());
        assertEquals("用户请求", result.getPullReason());
        assertEquals(TopicCandidateStatus.DRAFT, result.getStatus());
        assertEquals(1, result.getVersion());
        assertEquals(teacherId, result.getTeacherProfileId());
    }

    @Test
    void parseSingleResponse_shouldHandleArrayResponse() {
        UUID teacherId = UUID.randomUUID();
        String llmResponse = """
                [
                  {
                    "title": "测试标题",
                    "hook": "测试hook",
                    "fullScript": "测试脚本",
                    "shootingTips": "测试拍摄建议"
                  }
                ]
                """;

        TopicCandidate result = scriptGenerationService.parseSingleResponse(
                llmResponse, teacherId, "测试叙事", "测试原因");

        assertEquals("测试标题", result.getTitle());
        assertEquals("测试叙事", result.getNarrativeContext());
        assertEquals("测试原因", result.getPullReason());
    }

    @Test
    void parseSingleResponse_shouldThrowOnInvalidJson() {
        UUID teacherId = UUID.randomUUID();

        assertThrows(RuntimeException.class, () ->
                scriptGenerationService.parseSingleResponse("这不是JSON", teacherId, "叙事", "原因"));
    }

    @Test
    void parseSingleResponse_shouldThrowOnMissingRequiredFields() {
        UUID teacherId = UUID.randomUUID();
        String llmResponse = """
                {
                  "title": "只有标题"
                }
                """;

        assertThrows(RuntimeException.class, () ->
                scriptGenerationService.parseSingleResponse(llmResponse, teacherId, "叙事", "原因"));
    }

    @Test
    void generateSingleScript_shouldLoadProfileAndSaveCandidate() {
        UUID teacherId = UUID.randomUUID();
        TeacherProfile profile = TeacherProfile.builder()
                .id(teacherId)
                .subject("数学")
                .gradeLevel("小学")
                .nickname("数学喵")
                .build();

        String llmResponse = """
                {
                  "title": "口算速算法",
                  "hook": "口算太慢？试试这个方法",
                  "fullScript": "今天教你凑十法...",
                  "shootingTips": "正面镜头"
                }
                """;

        when(profileRepository.findById(teacherId)).thenReturn(Optional.of(profile));
        when(playbookService.getRelevantMaterials(any(TeacherProfile.class))).thenReturn(List.of());
        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse);
        when(topicCandidateRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TopicCandidate result = scriptGenerationService.generateSingleScript(teacherId, "想讲口算", "用户拉取");

        verify(profileRepository).findById(teacherId);
        verify(playbookService).getRelevantMaterials(profile);
        verify(deepSeekClient).chat(anyString());
        verify(topicCandidateRepository).save(any());

        assertEquals("口算速算法", result.getTitle());
        assertEquals("想讲口算", result.getNarrativeContext());
        assertEquals("用户拉取", result.getPullReason());
    }

    @Test
    void generateSingleScript_shouldThrowWhenProfileNotFound() {
        UUID teacherId = UUID.randomUUID();

        when(profileRepository.findById(teacherId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                scriptGenerationService.generateSingleScript(teacherId, "叙事", "原因"));
    }

    @Test
    void buildPrompt_shouldIncludeTeacherInfoAndNarrative() {
        TeacherProfile profile = TeacherProfile.builder()
                .nickname("数学喵")
                .subject("数学")
                .gradeLevel("小学")
                .style("幽默风趣")
                .strengths("解题技巧")
                .differentiator("思维导图教学法")
                .positioning("用思维导图让孩子爱上数学")
                .build();

        String prompt = scriptGenerationService.buildPrompt(profile, List.of(), "想讲口算技巧");

        assertTrue(prompt.contains("数学喵"));
        assertTrue(prompt.contains("数学"));
        assertTrue(prompt.contains("小学"));
        assertTrue(prompt.contains("幽默风趣"));
        assertTrue(prompt.contains("想讲口算技巧"));
    }

    @Test
    void buildPrompt_shouldIncludeMaterials() {
        TeacherProfile profile = TeacherProfile.builder()
                .nickname("数学喵")
                .subject("数学")
                .build();

        var materials = List.of(
                new com.starcourse.service.playbook.PlaybookMaterial(
                        "math-oral-calc", "数学", "口算速算", "内容正文", List.of("口算"))
        );

        String prompt = scriptGenerationService.buildPrompt(profile, materials, "想讲口算");

        assertTrue(prompt.contains("口算速算"));
        assertTrue(prompt.contains("内容正文"));
    }
}
