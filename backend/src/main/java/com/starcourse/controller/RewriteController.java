package com.starcourse.controller;

import com.starcourse.entity.ScriptFeedback;
import com.starcourse.record.RewriteRequestDTO;
import com.starcourse.record.ScriptFeedbackDTO;
import com.starcourse.service.RewriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rewrite")
@RequiredArgsConstructor
public class RewriteController {

    private final RewriteService rewriteService;

    @PostMapping
    public ResponseEntity<ScriptFeedbackDTO> rewrite(@RequestBody RewriteRequestDTO request) {
        if (request.direction() == null || request.direction().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        ScriptFeedback feedback = rewriteService.rewrite(request.topicCandidateId(), request.direction());
        return ResponseEntity.ok(ScriptFeedbackDTO.fromEntity(feedback));
    }

    @GetMapping("/history/{topicCandidateId}")
    public ResponseEntity<List<ScriptFeedbackDTO>> history(@PathVariable UUID topicCandidateId) {
        List<ScriptFeedbackDTO> history = rewriteService.getHistory(topicCandidateId)
                .stream()
                .map(ScriptFeedbackDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(history);
    }
}
