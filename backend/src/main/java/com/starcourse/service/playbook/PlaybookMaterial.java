package com.starcourse.service.playbook;

import java.util.List;

public record PlaybookMaterial(
        String dimensionId,
        String subject,
        String title,
        String exampleContent,
        List<String> tags
) {}
