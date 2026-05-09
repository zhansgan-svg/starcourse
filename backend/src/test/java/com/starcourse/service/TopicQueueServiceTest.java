package com.starcourse.service;

import com.starcourse.entity.TopicQueue;
import com.starcourse.repository.TopicQueueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicQueueServiceTest {

    @Mock
    private TopicQueueRepository topicQueueRepository;

    @InjectMocks
    private TopicQueueService topicQueueService;

    @Test
    void addToQueue_shouldSaveWithPendingStatus() {
        UUID storyProfileId = UUID.randomUUID();
        UUID candidateId = UUID.randomUUID();

        when(topicQueueRepository.save(any())).thenAnswer(i -> {
            TopicQueue q = i.getArgument(0);
            q.setId(UUID.randomUUID());
            return q;
        });

        TopicQueue result = topicQueueService.addToQueue(storyProfileId, candidateId, 5);

        assertNotNull(result.getId());
        assertEquals(storyProfileId, result.getStoryProfileId());
        assertEquals(candidateId, result.getTopicCandidateId());
        assertEquals(5, result.getPriority());
        assertEquals("PENDING", result.getStatus());
        verify(topicQueueRepository).save(any());
    }

    @Test
    void getNextFromQueue_shouldReturnHighestPriorityPending() {
        UUID storyProfileId = UUID.randomUUID();
        TopicQueue queue = TopicQueue.builder()
                .id(UUID.randomUUID())
                .storyProfileId(storyProfileId)
                .priority(10)
                .status("PENDING")
                .build();

        when(topicQueueRepository.findFirstByStoryProfileIdAndStatusOrderByPriorityDesc(storyProfileId, "PENDING"))
                .thenReturn(Optional.of(queue));

        TopicQueue result = topicQueueService.getNextFromQueue(storyProfileId);
        assertNotNull(result);
        assertEquals(10, result.getPriority());
    }

    @Test
    void getNextFromQueue_shouldReturnNullWhenEmpty() {
        UUID storyProfileId = UUID.randomUUID();
        when(topicQueueRepository.findFirstByStoryProfileIdAndStatusOrderByPriorityDesc(storyProfileId, "PENDING"))
                .thenReturn(Optional.empty());

        TopicQueue result = topicQueueService.getNextFromQueue(storyProfileId);
        assertNull(result);
    }

    @Test
    void markCompleted_shouldUpdateStatus() {
        UUID queueId = UUID.randomUUID();
        TopicQueue queue = TopicQueue.builder()
                .id(queueId)
                .status("PENDING")
                .build();

        when(topicQueueRepository.findById(queueId)).thenReturn(Optional.of(queue));
        when(topicQueueRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TopicQueue result = topicQueueService.markCompleted(queueId);

        assertEquals("COMPLETED", result.getStatus());
        verify(topicQueueRepository).save(queue);
    }

    @Test
    void markCompleted_shouldThrowWhenNotFound() {
        UUID queueId = UUID.randomUUID();
        when(topicQueueRepository.findById(queueId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                topicQueueService.markCompleted(queueId));
    }

    @Test
    void getQueueByStoryProfile_shouldReturnAllItems() {
        UUID storyProfileId = UUID.randomUUID();
        List<TopicQueue> items = List.of(
                TopicQueue.builder().id(UUID.randomUUID()).priority(10).status("PENDING").build(),
                TopicQueue.builder().id(UUID.randomUUID()).priority(5).status("COMPLETED").build()
        );

        when(topicQueueRepository.findByStoryProfileIdOrderByPriorityDesc(storyProfileId)).thenReturn(items);

        List<TopicQueue> result = topicQueueService.getQueueByStoryProfile(storyProfileId);
        assertEquals(2, result.size());
    }

    @Test
    void getPendingItems_shouldReturnOnlyPending() {
        UUID storyProfileId = UUID.randomUUID();
        List<TopicQueue> pending = List.of(
                TopicQueue.builder().id(UUID.randomUUID()).status("PENDING").build()
        );

        when(topicQueueRepository.findByStoryProfileIdAndStatus(storyProfileId, "PENDING")).thenReturn(pending);

        List<TopicQueue> result = topicQueueService.getPendingItems(storyProfileId);
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }
}
