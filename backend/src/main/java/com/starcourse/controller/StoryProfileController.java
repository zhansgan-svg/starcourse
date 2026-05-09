package com.starcourse.controller;

import com.starcourse.entity.StoryProfile;
import com.starcourse.record.StoryProfileDTO;
import com.starcourse.service.StoryProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/story-profile")
@RequiredArgsConstructor
public class StoryProfileController {

    private final StoryProfileService storyProfileService;

    @PostMapping
    public ResponseEntity<StoryProfileDTO> create(@RequestBody StoryProfileDTO dto) {
        StoryProfile profile = storyProfileService.createProfile(
                dto.teacherProfileId(),
                dto.currentNarrative(),
                dto.teachingGoal()
        );
        return ResponseEntity.ok(StoryProfileDTO.fromEntity(profile));
    }

    @GetMapping("/{teacherProfileId}")
    public ResponseEntity<StoryProfileDTO> get(@PathVariable UUID teacherProfileId) {
        StoryProfile profile = storyProfileService.getProfile(teacherProfileId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(StoryProfileDTO.fromEntity(profile));
    }

    @PutMapping("/{teacherProfileId}/narrative")
    public ResponseEntity<StoryProfileDTO> updateNarrative(
            @PathVariable UUID teacherProfileId,
            @RequestBody Map<String, String> body) {
        String narrative = body.get("narrative");
        if (narrative == null || narrative.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        StoryProfile profile = storyProfileService.updateNarrative(teacherProfileId, narrative);
        return ResponseEntity.ok(StoryProfileDTO.fromEntity(profile));
    }

    @GetMapping("/{teacherProfileId}/narrative")
    public ResponseEntity<Map<String, String>> getNarrative(@PathVariable UUID teacherProfileId) {
        String narrative = storyProfileService.getCurrentNarrative(teacherProfileId);
        if (narrative == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("narrative", narrative));
    }
}
