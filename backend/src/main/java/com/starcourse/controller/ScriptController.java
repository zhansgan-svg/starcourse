package com.starcourse.controller;

import com.starcourse.entity.TopicCandidate;
import com.starcourse.record.NarrativeRequestDTO;
import com.starcourse.record.ScriptPullResponseDTO;
import com.starcourse.record.TopicCandidateDTO;
import com.starcourse.repository.TopicCandidateRepository;
import com.starcourse.service.ScriptGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/scripts")
@RequiredArgsConstructor
public class ScriptController {

    private final ScriptGenerationService scriptGenerationService;
    private final TopicCandidateRepository topicCandidateRepository;

    @PostMapping("/pull")
    public ResponseEntity<ScriptPullResponseDTO> pull(@RequestBody NarrativeRequestDTO request) {
        String pullReason = request.narrative() != null ? request.narrative() : "用户请求拉取脚本";
        TopicCandidate candidate = scriptGenerationService.generateSingleScript(
                request.teacherProfileId(),
                request.narrative(),
                pullReason
        );
        return ResponseEntity.ok(ScriptPullResponseDTO.fromEntity(candidate));
    }

    @GetMapping("/list/{teacherProfileId}")
    public ResponseEntity<List<TopicCandidateDTO>> list(@PathVariable UUID teacherProfileId) {
        List<TopicCandidateDTO> scripts = topicCandidateRepository.findByTeacherProfileId(teacherProfileId)
                .stream()
                .map(TopicCandidateDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(scripts);
    }
}
