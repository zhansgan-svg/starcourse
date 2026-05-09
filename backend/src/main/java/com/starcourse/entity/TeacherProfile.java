package com.starcourse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "teacher_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "open_id", unique = true, nullable = false)
    private String openId;

    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String subject;

    @Column(name = "grade_level")
    private String gradeLevel;

    private String style;

    @Column(columnDefinition = "text")
    private String strengths;

    @Column(name = "shootable_content", columnDefinition = "text")
    private String shootableContent;

    private String frequency;

    @Column(name = "avatar_suggestion")
    private String avatarSuggestion;

    @Column(name = "target_audience", columnDefinition = "text")
    private String targetAudience;

    @Column(name = "pricing_range")
    private String pricingRange;

    private String differentiator;

    @Column(name = "platform_preference")
    private String platformPreference;

    private String positioning;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
