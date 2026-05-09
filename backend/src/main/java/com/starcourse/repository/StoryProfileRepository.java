package com.starcourse.repository;

import com.starcourse.entity.StoryProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoryProfileRepository extends JpaRepository<StoryProfile, UUID> {
    Optional<StoryProfile> findByTeacherProfileId(UUID teacherProfileId);
}
