package com.starcourse.record;

import java.util.UUID;

public record NarrativeRequestDTO(
        UUID teacherProfileId,
        String narrative,
        String teachingGoal
) {}
