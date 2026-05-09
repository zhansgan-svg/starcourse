package com.starcourse.service;

import com.starcourse.entity.StoryProfile;
import com.starcourse.repository.StoryProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryProfileService {

    private final StoryProfileRepository storyProfileRepository;

    @Transactional
    public StoryProfile createProfile(UUID teacherProfileId, String narrative, String teachingGoal) {
        StoryProfile profile = StoryProfile.builder()
                .teacherProfileId(teacherProfileId)
                .currentNarrative(narrative)
                .teachingGoal(teachingGoal)
                .build();
        StoryProfile saved = storyProfileRepository.save(profile);
        log.info("Created StoryProfile {} for teacher {}", saved.getId(), teacherProfileId);
        return saved;
    }

    @Transactional
    public StoryProfile updateNarrative(UUID teacherProfileId, String narrative) {
        StoryProfile profile = storyProfileRepository.findByTeacherProfileId(teacherProfileId)
                .orElseThrow(() -> new RuntimeException("StoryProfile not found for teacher: " + teacherProfileId));
        profile.setCurrentNarrative(narrative);
        StoryProfile saved = storyProfileRepository.save(profile);
        log.info("Updated narrative for StoryProfile {}", saved.getId());
        return saved;
    }

    public String getCurrentNarrative(UUID teacherProfileId) {
        return storyProfileRepository.findByTeacherProfileId(teacherProfileId)
                .map(StoryProfile::getCurrentNarrative)
                .orElse(null);
    }

    public StoryProfile getProfile(UUID teacherProfileId) {
        return storyProfileRepository.findByTeacherProfileId(teacherProfileId)
                .orElse(null);
    }

    public StoryProfile getOrCreateProfile(UUID teacherProfileId) {
        return storyProfileRepository.findByTeacherProfileId(teacherProfileId)
                .orElseGet(() -> createProfile(teacherProfileId, null, null));
    }
}
