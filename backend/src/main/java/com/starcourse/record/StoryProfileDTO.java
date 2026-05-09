package com.starcourse.record;

import com.starcourse.entity.StoryProfile;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoryProfileDTO(
        UUID id,
        UUID teacherProfileId,
        String currentNarrative,
        String teachingGoal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StoryProfileDTO fromEntity(StoryProfile entity) {
        return new StoryProfileDTO(
                entity.getId(),
                entity.getTeacherProfileId(),
                entity.getCurrentNarrative(),
                entity.getTeachingGoal(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public StoryProfile toEntity() {
        StoryProfile entity = new StoryProfile();
        entity.setId(id);
        entity.setTeacherProfileId(teacherProfileId);
        entity.setCurrentNarrative(currentNarrative);
        entity.setTeachingGoal(teachingGoal);
        return entity;
    }
}
