package com.starcourse.repository;

import com.starcourse.entity.TopicQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicQueueRepository extends JpaRepository<TopicQueue, UUID> {
    List<TopicQueue> findByStoryProfileIdOrderByPriorityDesc(UUID storyProfileId);
    Optional<TopicQueue> findFirstByStoryProfileIdAndStatusOrderByPriorityDesc(UUID storyProfileId, String status);
    List<TopicQueue> findByStoryProfileIdAndStatus(UUID storyProfileId, String status);
}
