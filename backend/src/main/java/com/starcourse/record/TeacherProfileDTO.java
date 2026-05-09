package com.starcourse.record;

import com.starcourse.entity.TeacherProfile;

import java.time.LocalDateTime;
import java.util.UUID;

public record TeacherProfileDTO(
        UUID id,
        String openId,
        String nickname,
        String avatarUrl,
        String avatarSuggestion,
        String subject,
        String gradeLevel,
        String style,
        String strengths,
        String shootableContent,
        String frequency,
        String targetAudience,
        String pricingRange,
        String differentiator,
        String platformPreference,
        String positioning,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TeacherProfileDTO fromEntity(TeacherProfile entity) {
        return new TeacherProfileDTO(
                entity.getId(),
                entity.getOpenId(),
                entity.getNickname(),
                entity.getAvatarUrl(),
                entity.getAvatarSuggestion(),
                entity.getSubject(),
                entity.getGradeLevel(),
                entity.getStyle(),
                entity.getStrengths(),
                entity.getShootableContent(),
                entity.getFrequency(),
                entity.getTargetAudience(),
                entity.getPricingRange(),
                entity.getDifferentiator(),
                entity.getPlatformPreference(),
                entity.getPositioning(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TeacherProfile toEntity() {
        TeacherProfile entity = new TeacherProfile();
        entity.setId(id);
        entity.setOpenId(openId);
        entity.setNickname(nickname);
        entity.setAvatarUrl(avatarUrl);
        entity.setAvatarSuggestion(avatarSuggestion);
        entity.setSubject(subject);
        entity.setGradeLevel(gradeLevel);
        entity.setStyle(style);
        entity.setStrengths(strengths);
        entity.setShootableContent(shootableContent);
        entity.setFrequency(frequency);
        entity.setTargetAudience(targetAudience);
        entity.setPricingRange(pricingRange);
        entity.setDifferentiator(differentiator);
        entity.setPlatformPreference(platformPreference);
        entity.setPositioning(positioning);
        return entity;
    }
}
