package com.starcourse.record;

import com.starcourse.entity.TopicCandidate;
import com.starcourse.entity.TopicCandidateStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TopicCandidateDTO(
        UUID id,
        UUID teacherProfileId,
        String title,
        String hook,
        String outline,
        String fullScript,
        String shootingTips,
        String storyboard,
        String narrativeContext,
        String pullReason,
        TopicCandidateStatus status,
        Integer version,
        LocalDateTime createdAt
) {
    public static TopicCandidateDTO fromEntity(TopicCandidate entity) {
        return new TopicCandidateDTO(
                entity.getId(),
                entity.getTeacherProfileId(),
                entity.getTitle(),
                entity.getHook(),
                entity.getOutline(),
                entity.getFullScript(),
                entity.getShootingTips(),
                entity.getStoryboard(),
                entity.getNarrativeContext(),
                entity.getPullReason(),
                entity.getStatus(),
                entity.getVersion(),
                entity.getCreatedAt()
        );
    }

    public TopicCandidate toEntity() {
        TopicCandidate entity = new TopicCandidate();
        entity.setId(id);
        entity.setTeacherProfileId(teacherProfileId);
        entity.setTitle(title);
        entity.setHook(hook);
        entity.setOutline(outline);
        entity.setFullScript(fullScript);
        entity.setShootingTips(shootingTips);
        entity.setStoryboard(storyboard);
        entity.setNarrativeContext(narrativeContext);
        entity.setPullReason(pullReason);
        entity.setStatus(status);
        entity.setVersion(version);
        return entity;
    }
}
