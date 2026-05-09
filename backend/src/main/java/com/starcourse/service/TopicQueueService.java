package com.starcourse.service;

import com.starcourse.entity.TopicQueue;
import com.starcourse.repository.TopicQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicQueueService {

    private final TopicQueueRepository topicQueueRepository;

    @Transactional
    public TopicQueue addToQueue(UUID storyProfileId, UUID topicCandidateId, int priority) {
        TopicQueue queue = TopicQueue.builder()
                .storyProfileId(storyProfileId)
                .topicCandidateId(topicCandidateId)
                .priority(priority)
                .status("PENDING")
                .build();
        TopicQueue saved = topicQueueRepository.save(queue);
        log.info("Added to queue: {} for story profile {}", saved.getId(), storyProfileId);
        return saved;
    }

    public TopicQueue getNextFromQueue(UUID storyProfileId) {
        return topicQueueRepository.findFirstByStoryProfileIdAndStatusOrderByPriorityDesc(storyProfileId, "PENDING")
                .orElse(null);
    }

    @Transactional
    public TopicQueue markCompleted(UUID queueId) {
        TopicQueue queue = topicQueueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("TopicQueue not found: " + queueId));
        queue.setStatus("COMPLETED");
        TopicQueue saved = topicQueueRepository.save(queue);
        log.info("Marked queue item {} as COMPLETED", queueId);
        return saved;
    }

    public List<TopicQueue> getQueueByStoryProfile(UUID storyProfileId) {
        return topicQueueRepository.findByStoryProfileIdOrderByPriorityDesc(storyProfileId);
    }

    public List<TopicQueue> getPendingItems(UUID storyProfileId) {
        return topicQueueRepository.findByStoryProfileIdAndStatus(storyProfileId, "PENDING");
    }
}
