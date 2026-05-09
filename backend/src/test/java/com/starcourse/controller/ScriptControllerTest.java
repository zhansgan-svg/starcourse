package com.starcourse.controller;

import com.starcourse.entity.TopicCandidate;
import com.starcourse.entity.TopicCandidateStatus;
import com.starcourse.record.NarrativeRequestDTO;
import com.starcourse.record.ScriptPullResponseDTO;
import com.starcourse.repository.TopicCandidateRepository;
import com.starcourse.service.ScriptGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScriptControllerTest {

    @Mock
    private ScriptGenerationService scriptGenerationService;

    @Mock
    private TopicCandidateRepository topicCandidateRepository;

    @InjectMocks
    private ScriptController scriptController;

    @Test
    void pull_shouldReturnSingleScript() {
        UUID teacherId = UUID.randomUUID();
        NarrativeRequestDTO request = new NarrativeRequestDTO(teacherId, "想讲口算技巧", null);

        TopicCandidate candidate = TopicCandidate.builder()
                .id(UUID.randomUUID())
                .teacherProfileId(teacherId)
                .title("口算速算法")
                .hook("口算太慢？试试这个方法")
                .fullScript("今天教你凑十法...")
                .narrativeContext("想讲口算技巧")
                .pullReason("想讲口算技巧")
                .status(TopicCandidateStatus.DRAFT)
                .version(1)
                .build();

        when(scriptGenerationService.generateSingleScript(teacherId, "想讲口算技巧", "想讲口算技巧"))
                .thenReturn(candidate);

        ResponseEntity<ScriptPullResponseDTO> response = scriptController.pull(request);

        assertEquals(200, response.getStatusCode().value());
        ScriptPullResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("口算速算法", body.title());
        assertEquals("想讲口算技巧", body.narrativeContext());
        assertEquals("想讲口算技巧", body.pullReason());
        assertEquals(teacherId, body.teacherProfileId());
        verify(scriptGenerationService).generateSingleScript(teacherId, "想讲口算技巧", "想讲口算技巧");
    }

    @Test
    void pull_shouldUseDefaultPullReasonWhenNarrativeIsNull() {
        UUID teacherId = UUID.randomUUID();
        NarrativeRequestDTO request = new NarrativeRequestDTO(teacherId, null, null);

        TopicCandidate candidate = TopicCandidate.builder()
                .id(UUID.randomUUID())
                .teacherProfileId(teacherId)
                .title("测试脚本")
                .hook("测试hook")
                .fullScript("测试内容")
                .pullReason("用户请求拉取脚本")
                .status(TopicCandidateStatus.DRAFT)
                .version(1)
                .build();

        when(scriptGenerationService.generateSingleScript(teacherId, null, "用户请求拉取脚本"))
                .thenReturn(candidate);

        ResponseEntity<ScriptPullResponseDTO> response = scriptController.pull(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("用户请求拉取脚本", response.getBody().pullReason());
    }

    @Test
    void list_shouldReturnScriptsForTeacher() {
        UUID teacherId = UUID.randomUUID();
        List<TopicCandidate> candidates = List.of(
                TopicCandidate.builder()
                        .id(UUID.randomUUID())
                        .teacherProfileId(teacherId)
                        .title("脚本1")
                        .status(TopicCandidateStatus.DRAFT)
                        .version(1)
                        .build(),
                TopicCandidate.builder()
                        .id(UUID.randomUUID())
                        .teacherProfileId(teacherId)
                        .title("脚本2")
                        .status(TopicCandidateStatus.DRAFT)
                        .version(1)
                        .build()
        );

        when(topicCandidateRepository.findByTeacherProfileId(teacherId)).thenReturn(candidates);

        var response = scriptController.list(teacherId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertEquals("脚本1", response.getBody().get(0).title());
        assertEquals("脚本2", response.getBody().get(1).title());
    }

    @Test
    void list_shouldReturnEmptyListWhenNoScripts() {
        UUID teacherId = UUID.randomUUID();
        when(topicCandidateRepository.findByTeacherProfileId(teacherId)).thenReturn(List.of());

        var response = scriptController.list(teacherId);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
    }
}
