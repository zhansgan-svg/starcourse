package com.starcourse.record;

import com.starcourse.entity.TopicCandidate;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScriptPullResponseDTO(
        UUID id,
        UUID teacherProfileId,
        String title,
        String hook,
        String fullScript,
        String shootingTips,
        String storyboard,
        String narrativeContext,
        String pullReason,
        String status,
        Integer version,
        LocalDateTime createdAt
) {
    public static ScriptPullResponseDTO fromEntity(TopicCandidate entity) {
        return new ScriptPullResponseDTO(
                entity.getId(),
                entity.getTeacherProfileId(),
                entity.getTitle(),
                entity.getHook(),
                entity.getFullScript(),
                entity.getShootingTips(),
                entity.getStoryboard(),
                entity.getNarrativeContext(),
                entity.getPullReason(),
                entity.getStatus().name(),
                entity.getVersion(),
                entity.getCreatedAt()
        );
    }
}
