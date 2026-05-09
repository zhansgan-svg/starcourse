package com.starcourse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcourse.entity.TeacherProfile;
import com.starcourse.repository.TeacherProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireServiceTest {

    @Mock
    private TeacherProfileRepository profileRepository;

    @Mock
    private DeepSeekClient deepSeekClient;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private QuestionnaireService questionnaireService;

    @Test
    void generateProfile_shouldParseLlmResponseAndSave() {
        // Given
        TeacherProfile profile = TeacherProfile.builder()
                .subject("数学")
                .gradeLevel("小学")
                .style("幽默风趣")
                .strengths("[\"解题技巧\", \"思维训练\"]")
                .shootableContent("[\"解题过程\", \"学习方法\"]")
                .frequency("每周3-4次")
                .targetAudience("[\"小学生家长\"]")
                .pricingRange("100-200元/小时")
                .differentiator("独创思维导图教学法")
                .platformPreference("小红书")
                .build();

        String llmResponse = """
                这是生成的结果：
                ```json
                {
                    "nickname": "数学喵",
                    "avatarSuggestion": "卡通猫咪形象，蓝色背景，手持铅笔",
                    "positioning": "用思维导图让孩子爱上数学"
                }
                ```
                """;

        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse);
        when(profileRepository.save(any(TeacherProfile.class))).thenAnswer(i -> i.getArgument(0));

        // When
        TeacherProfile result = questionnaireService.generateProfile(profile);

        // Then
        assertEquals("数学喵", result.getNickname());
        assertEquals("卡通猫咪形象，蓝色背景，手持铅笔", result.getAvatarSuggestion());
        assertEquals("用思维导图让孩子爱上数学", result.getPositioning());
        verify(profileRepository).save(profile);
    }

    @Test
    void generateProfile_shouldHandleDirectJsonResponse() {
        // Given
        TeacherProfile profile = TeacherProfile.builder()
                .subject("英语")
                .gradeLevel("初中")
                .build();

        String llmResponse = """
                {
                    "nickname": "英语侠",
                    "avatarSuggestion": "超级英雄风格，红色披风",
                    "positioning": "让每个孩子都能开口说英语"
                }
                """;

        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse);
        when(profileRepository.save(any(TeacherProfile.class))).thenAnswer(i -> i.getArgument(0));

        // When
        TeacherProfile result = questionnaireService.generateProfile(profile);

        // Then
        assertEquals("英语侠", result.getNickname());
        assertEquals("超级英雄风格，红色披风", result.getAvatarSuggestion());
        assertEquals("让每个孩子都能开口说英语", result.getPositioning());
    }

    @Test
    void generateProfile_shouldThrowOnInvalidJson() {
        // Given
        TeacherProfile profile = TeacherProfile.builder()
                .subject("物理")
                .build();

        when(deepSeekClient.chat(anyString())).thenReturn("这不是一个JSON响应");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            questionnaireService.generateProfile(profile);
        });
    }

    @Test
    void generateProfile_shouldThrowOnMissingFields() {
        // Given
        TeacherProfile profile = TeacherProfile.builder()
                .subject("化学")
                .build();

        String llmResponse = """
                {
                    "nickname": "化学老师"
                }
                """;

        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            questionnaireService.generateProfile(profile);
        });
    }

    @Test
    void buildPrompt_shouldHandleNullFields() {
        // Given
        TeacherProfile profile = TeacherProfile.builder()
                .subject("语文")
                .build();

        // When - should not throw
        String prompt = questionnaireService.buildPrompt(profile);

        // Then
        assertNotNull(prompt);
        assertTrue(prompt.contains("语文"));
    }
}
