package com.starcourse.controller;

import com.starcourse.entity.TeacherProfile;
import com.starcourse.record.TeacherProfileDTO;
import com.starcourse.repository.TeacherProfileRepository;
import com.starcourse.service.QuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/questionnaire")
@RequiredArgsConstructor
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final TeacherProfileRepository profileRepository;

    @PostMapping("/submit")
    public ResponseEntity<TeacherProfileDTO> submit(@RequestBody TeacherProfileDTO dto) {
        TeacherProfile profile = dto.toEntity();
        TeacherProfile saved = questionnaireService.generateProfile(profile);
        return ResponseEntity.ok(TeacherProfileDTO.fromEntity(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherProfileDTO> getById(@PathVariable UUID id) {
        return profileRepository.findById(id)
                .map(TeacherProfileDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
