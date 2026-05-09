package com.starcourse.repository;

import com.starcourse.entity.ScriptFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScriptFeedbackRepository extends JpaRepository<ScriptFeedback, UUID> {
    List<ScriptFeedback> findByTopicCandidateIdOrderByCreatedAtDesc(UUID topicCandidateId);
}
