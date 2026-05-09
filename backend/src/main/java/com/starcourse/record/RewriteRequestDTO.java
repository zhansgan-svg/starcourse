package com.starcourse.record;

import java.util.UUID;

public record RewriteRequestDTO(
        UUID topicCandidateId,
        String direction
) {}
