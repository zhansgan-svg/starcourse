package com.starcourse.record;

import com.starcourse.entity.ScriptFeedback;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScriptFeedbackDTO(
        UUID id,
        UUID topicCandidateId,
        String direction,
        String originalContent,
        String rewrittenContent,
        LocalDateTime createdAt
) {
    public static ScriptFeedbackDTO fromEntity(ScriptFeedback entity) {
        return new ScriptFeedbackDTO(
                entity.getId(),
                entity.getTopicCandidateId(),
                entity.getDirection(),
                entity.getOriginalContent(),
                entity.getRewrittenContent(),
                entity.getCreatedAt()
        );
    }

    public ScriptFeedback toEntity() {
        ScriptFeedback entity = new ScriptFeedback();
        entity.setId(id);
        entity.setTopicCandidateId(topicCandidateId);
        entity.setDirection(direction);
        entity.setOriginalContent(originalContent);
        entity.setRewrittenContent(rewrittenContent);
        return entity;
    }
}
