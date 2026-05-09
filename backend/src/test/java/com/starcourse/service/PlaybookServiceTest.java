package com.starcourse.service;

import com.starcourse.entity.TeacherProfile;
import com.starcourse.service.playbook.PlaybookMaterial;
import com.starcourse.service.playbook.PlaybookRegistry;
import com.starcourse.service.playbook.PlaybookService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaybookServiceTest {

    private final PlaybookService playbookService = new PlaybookService();

    @Test
    void getRelevantMaterials_mathTeacherGetsMathMaterials() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject("数学")
                .gradeLevel("小学")
                .build();

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);

        assertFalse(materials.isEmpty());
        assertTrue(materials.stream().allMatch(m -> m.subject().equals("数学")));
        assertEquals(6, materials.size());
    }

    @Test
    void getRelevantMaterials_englishTeacherGetsEnglishMaterials() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject("英语")
                .gradeLevel("初中")
                .build();

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);

        assertFalse(materials.isEmpty());
        assertTrue(materials.stream().allMatch(m -> m.subject().equals("英语")));
        assertEquals(6, materials.size());
    }

    @Test
    void getRelevantMaterials_codingTeacherGetsCodingMaterials() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject("编程")
                .build();

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);

        assertFalse(materials.isEmpty());
        assertTrue(materials.stream().allMatch(m -> m.subject().equals("编程")));
        assertEquals(6, materials.size());
    }

    @Test
    void getRelevantMaterials_artTeacherGetsArtMaterials() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject("美术")
                .build();

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);

        assertFalse(materials.isEmpty());
        assertTrue(materials.stream().allMatch(m -> m.subject().equals("美术")));
        assertEquals(5, materials.size());
    }

    @Test
    void getRelevantMaterials_unmatchedSubjectGetsEmptyList() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject("物理")
                .gradeLevel("高中")
                .build();

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);

        assertTrue(materials.isEmpty());
    }

    @Test
    void getRelevantMaterials_mathMiddleSchoolGetsNoMaterials() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject("数学")
                .gradeLevel("初中")
                .build();

        List<PlaybookMaterial> materials = playbookService.getRelevantMaterials(profile);

        assertTrue(materials.isEmpty());
    }

    @Test
    void getAllMaterials_returns23Materials() {
        List<PlaybookMaterial> all = playbookService.getAllMaterials();

        assertEquals(23, all.size());
    }

    @Test
    void registry_has24Dimensions() {
        List<PlaybookRegistry.Dimension> dimensions = PlaybookRegistry.getAllDimensions();

        assertEquals(24, dimensions.size());
    }

    @Test
    void registry_mathDimensionsHaveCorrectPredicate() {
        TeacherProfile mathProfile = TeacherProfile.builder()
                .subject("数学")
                .gradeLevel("小学")
                .build();

        List<PlaybookRegistry.Dimension> matched = PlaybookRegistry.matchDimensions(mathProfile);

        assertEquals(6, matched.size());
        assertTrue(matched.stream().allMatch(d -> d.subject().equals("数学")));
    }

    @Test
    void registry_nullSubjectMatchesNothing() {
        TeacherProfile profile = TeacherProfile.builder()
                .subject(null)
                .build();

        List<PlaybookRegistry.Dimension> matched = PlaybookRegistry.matchDimensions(profile);

        assertTrue(matched.isEmpty());
    }
}
