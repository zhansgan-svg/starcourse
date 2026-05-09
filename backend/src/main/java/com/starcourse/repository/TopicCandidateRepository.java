package com.starcourse.repository;

import com.starcourse.entity.TopicCandidate;
import com.starcourse.entity.TopicCandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicCandidateRepository extends JpaRepository<TopicCandidate, UUID> {
    List<TopicCandidate> findByTeacherProfileId(UUID teacherProfileId);
    List<TopicCandidate> findByTeacherProfileIdAndStatus(UUID teacherProfileId, TopicCandidateStatus status);
}
