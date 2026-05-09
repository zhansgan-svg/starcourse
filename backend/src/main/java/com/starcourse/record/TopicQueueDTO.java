package com.starcourse.record;

import com.starcourse.entity.TopicQueue;

import java.time.LocalDateTime;
import java.util.UUID;

public record TopicQueueDTO(
        UUID id,
        UUID storyProfileId,
        UUID topicCandidateId,
        Integer priority,
        String status,
        LocalDateTime createdAt
) {
    public static TopicQueueDTO fromEntity(TopicQueue entity) {
        return new TopicQueueDTO(
                entity.getId(),
                entity.getStoryProfileId(),
                entity.getTopicCandidateId(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public TopicQueue toEntity() {
        TopicQueue entity = new TopicQueue();
        entity.setId(id);
        entity.setStoryProfileId(storyProfileId);
        entity.setTopicCandidateId(topicCandidateId);
        entity.setPriority(priority);
        entity.setStatus(status);
        return entity;
    }
}
