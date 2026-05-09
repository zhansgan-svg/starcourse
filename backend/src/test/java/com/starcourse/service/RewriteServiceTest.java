package com.starcourse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcourse.entity.ScriptFeedback;
import com.starcourse.entity.TopicCandidate;
import com.starcourse.entity.TopicCandidateStatus;
import com.starcourse.repository.ScriptFeedbackRepository;
import com.starcourse.repository.TopicCandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class RewriteServiceTest {

    @Mock
    private DeepSeekClient deepSeekClient;

    @Mock
    private ScriptFeedbackRepository scriptFeedbackRepository;

    @Mock
    private TopicCandidateRepository topicCandidateRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RewriteService rewriteService;

    @Test
    void rewrite_shouldSaveFeedbackWithOriginalAndRewrittenContent() {
        UUID candidateId = UUID.randomUUID();
        TopicCandidate candidate = TopicCandidate.builder()
                .id(candidateId)
                .fullScript("原始脚本内容，讲解口算速算技巧")
                .status(TopicCandidateStatus.DRAFT)
                .version(1)
                .build();

        String llmResponse = """
                ```json
                {"rewrittenContent": "改写后的内容：口算速算技巧大揭秘！"}
                ```
                """;

        when(topicCandidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse);
        when(scriptFeedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ScriptFeedback result = rewriteService.rewrite(candidateId, "更口语化");

        ArgumentCaptor<ScriptFeedback> captor = ArgumentCaptor.forClass(ScriptFeedback.class);
        verify(scriptFeedbackRepository).save(captor.capture());

        ScriptFeedback saved = captor.getValue();
        assertEquals(candidateId, saved.getTopicCandidateId());
        assertEquals("更口语化", saved.getDirection());
        assertEquals("原始脚本内容，讲解口算速算技巧", saved.getOriginalContent());
        assertEquals("改写后的内容：口算速算技巧大揭秘！", saved.getRewrittenContent());

        assertEquals(saved.getOriginalContent(), result.getOriginalContent());
        assertEquals(saved.getRewrittenContent(), result.getRewrittenContent());
    }

    @Test
    void rewrite_shouldPreserveOriginalTopicCandidateContent() {
        UUID candidateId = UUID.randomUUID();
        String originalScript = "这是原始脚本，不会被修改";
        TopicCandidate candidate = TopicCandidate.builder()
                .id(candidateId)
                .fullScript(originalScript)
                .status(TopicCandidateStatus.DRAFT)
                .version(1)
                .build();

        String llmResponse = """
                {"rewrittenContent": "改写后的内容"}
                """;

        when(topicCandidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse);
        when(scriptFeedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        rewriteService.rewrite(candidateId, "加家长痛点");

        verify(topicCandidateRepository, never()).save(any());
        assertEquals(originalScript, candidate.getFullScript());
    }

    @Test
    void rewrite_shouldThrowWhenTopicCandidateNotFound() {
        UUID candidateId = UUID.randomUUID();
        when(topicCandidateRepository.findById(candidateId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                rewriteService.rewrite(candidateId, "更口语化"));

        verify(deepSeekClient, never()).chat(anyString());
        verify(scriptFeedbackRepository, never()).save(any());
    }

    @Test
    void rewrite_shouldSaveMultipleRewritesForHistory() {
        UUID candidateId = UUID.randomUUID();
        TopicCandidate candidate = TopicCandidate.builder()
                .id(candidateId)
                .fullScript("原始脚本")
                .status(TopicCandidateStatus.DRAFT)
                .version(1)
                .build();

        String llmResponse1 = """
                {"rewrittenContent": "改写版本1：更口语化"}
                """;
        String llmResponse2 = """
                {"rewrittenContent": "改写版本2：加家长痛点"}
                """;

        when(topicCandidateRepository.findById(candidateId)).thenReturn(Optional.of(candidate));
        when(deepSeekClient.chat(anyString())).thenReturn(llmResponse1, llmResponse2);
        when(scriptFeedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        rewriteService.rewrite(candidateId, "更口语化");
        rewriteService.rewrite(candidateId, "加家长痛点");

        verify(scriptFeedbackRepository, times(2)).save(any());

        List<ScriptFeedback> history = List.of(
                ScriptFeedback.builder().topicCandidateId(candidateId).direction("更口语化").build(),
                ScriptFeedback.builder().topicCandidateId(candidateId).direction("加家长痛点").build()
        );
        when(scriptFeedbackRepository.findByTopicCandidateIdOrderByCreatedAtDesc(candidateId))
                .thenReturn(history);

        List<ScriptFeedback> result = rewriteService.getHistory(candidateId);
        assertEquals(2, result.size());
    }

    @Test
    void parseResponse_shouldExtractJsonFromMarkdown() {
        String llmResponse = """
                ```json
                {"rewrittenContent": "这是改写后的内容"}
                ```
                """;

        String result = rewriteService.parseResponse(llmResponse);
        assertEquals("这是改写后的内容", result);
    }

    @Test
    void parseResponse_shouldHandleDirectJson() {
        String llmResponse = """
                {"rewrittenContent": "直接JSON格式"}
                """;

        String result = rewriteService.parseResponse(llmResponse);
        assertEquals("直接JSON格式", result);
    }

    @Test
    void parseResponse_shouldThrowOnInvalidJson() {
        assertThrows(RuntimeException.class, () ->
                rewriteService.parseResponse("这不是JSON"));
    }
}
