package com.starcourse.record;

import com.starcourse.entity.TeacherProfile;

import java.util.UUID;

public record TeacherProfileSummaryDTO(
        UUID id,
        String nickname,
        String subject,
        String gradeLevel,
        String positioning
) {
    public static TeacherProfileSummaryDTO fromEntity(TeacherProfile entity) {
        return new TeacherProfileSummaryDTO(
                entity.getId(),
                entity.getNickname(),
                entity.getSubject(),
                entity.getGradeLevel(),
                entity.getPositioning()
        );
    }
}
