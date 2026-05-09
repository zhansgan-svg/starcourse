package com.starcourse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "topic_candidate")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TopicCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "teacher_profile_id", nullable = false)
    private UUID teacherProfileId;

    private String title;

    private String hook;

    @Column(columnDefinition = "TEXT")
    private String outline;

    @Column(name = "full_script", columnDefinition = "TEXT")
    private String fullScript;

    @Column(name = "shooting_tips", columnDefinition = "TEXT")
    private String shootingTips;

    @Column(columnDefinition = "text")
    private String storyboard;

    @Column(name = "narrative_context", columnDefinition = "TEXT")
    private String narrativeContext;

    @Column(name = "pull_reason")
    private String pullReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicCandidateStatus status;

    @Column(nullable = false)
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
