package com.starcourse.service;

import com.starcourse.entity.StoryProfile;
import com.starcourse.repository.StoryProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoryProfileServiceTest {

    @Mock
    private StoryProfileRepository storyProfileRepository;

    @InjectMocks
    private StoryProfileService storyProfileService;

    @Test
    void createProfile_shouldSaveAndReturn() {
        UUID teacherId = UUID.randomUUID();
        StoryProfile profile = StoryProfile.builder()
                .teacherProfileId(teacherId)
                .currentNarrative("想讲口算技巧")
                .teachingGoal("提升口算速度")
                .build();

        when(storyProfileRepository.save(any())).thenAnswer(i -> {
            StoryProfile p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        StoryProfile result = storyProfileService.createProfile(teacherId, "想讲口算技巧", "提升口算速度");

        assertNotNull(result.getId());
        assertEquals(teacherId, result.getTeacherProfileId());
        assertEquals("想讲口算技巧", result.getCurrentNarrative());
        assertEquals("提升口算速度", result.getTeachingGoal());
        verify(storyProfileRepository).save(any());
    }

    @Test
    void updateNarrative_shouldUpdateExistingProfile() {
        UUID teacherId = UUID.randomUUID();
        StoryProfile existing = StoryProfile.builder()
                .id(UUID.randomUUID())
                .teacherProfileId(teacherId)
                .currentNarrative("旧叙事")
                .build();

        when(storyProfileRepository.findByTeacherProfileId(teacherId)).thenReturn(Optional.of(existing));
        when(storyProfileRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        StoryProfile result = storyProfileService.updateNarrative(teacherId, "新叙事");

        assertEquals("新叙事", result.getCurrentNarrative());
        verify(storyProfileRepository).save(existing);
    }

    @Test
    void updateNarrative_shouldThrowWhenNotFound() {
        UUID teacherId = UUID.randomUUID();
        when(storyProfileRepository.findByTeacherProfileId(teacherId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                storyProfileService.updateNarrative(teacherId, "新叙事"));
    }

    @Test
    void getCurrentNarrative_shouldReturnNarrative() {
        UUID teacherId = UUID.randomUUID();
        StoryProfile profile = StoryProfile.builder()
                .teacherProfileId(teacherId)
                .currentNarrative("当前叙事")
                .build();

        when(storyProfileRepository.findByTeacherProfileId(teacherId)).thenReturn(Optional.of(profile));

        String result = storyProfileService.getCurrentNarrative(teacherId);
        assertEquals("当前叙事", result);
    }

    @Test
    void getCurrentNarrative_shouldReturnNullWhenNotFound() {
        UUID teacherId = UUID.randomUUID();
        when(storyProfileRepository.findByTeacherProfileId(teacherId)).thenReturn(Optional.empty());

        String result = storyProfileService.getCurrentNarrative(teacherId);
        assertNull(result);
    }

    @Test
    void getOrCreateProfile_shouldReturnExisting() {
        UUID teacherId = UUID.randomUUID();
        StoryProfile existing = StoryProfile.builder()
                .id(UUID.randomUUID())
                .teacherProfileId(teacherId)
                .currentNarrative("已有叙事")
                .build();

        when(storyProfileRepository.findByTeacherProfileId(teacherId)).thenReturn(Optional.of(existing));

        StoryProfile result = storyProfileService.getOrCreateProfile(teacherId);
        assertEquals("已有叙事", result.getCurrentNarrative());
        verify(storyProfileRepository, never()).save(any());
    }

    @Test
    void getOrCreateProfile_shouldCreateWhenNotFound() {
        UUID teacherId = UUID.randomUUID();
        when(storyProfileRepository.findByTeacherProfileId(teacherId)).thenReturn(Optional.empty());
        when(storyProfileRepository.save(any())).thenAnswer(i -> {
            StoryProfile p = i.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        StoryProfile result = storyProfileService.getOrCreateProfile(teacherId);
        assertNotNull(result);
        assertEquals(teacherId, result.getTeacherProfileId());
        verify(storyProfileRepository).save(any());
    }
}
