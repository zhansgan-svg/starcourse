package com.starcourse.service.playbook;

import com.starcourse.entity.TeacherProfile;

import java.util.*;
import java.util.function.Predicate;

public class PlaybookRegistry {

    public record Dimension(String id, String subject, String name, Predicate<TeacherProfile> predicate) {}

    private static final List<Dimension> DIMENSIONS = new ArrayList<>();

    static {
        // 小学数学 (6)
        register("math-oral-calc", "数学", "口算技巧",
                p -> matchesSubject(p, "数学") && matchesGrade(p, "小学"));
        register("math-word-problem", "数学", "应用题思路",
                p -> matchesSubject(p, "数学") && matchesGrade(p, "小学"));
        register("math-geometry", "数学", "几何直观",
                p -> matchesSubject(p, "数学") && matchesGrade(p, "小学"));
        register("math-number-sense", "数学", "数感培养",
                p -> matchesSubject(p, "数学") && matchesGrade(p, "小学"));
        register("math-common-mistakes", "数学", "易错题解析",
                p -> matchesSubject(p, "数学") && matchesGrade(p, "小学"));
        register("math-exam-score", "数学", "考试提分",
                p -> matchesSubject(p, "数学") && matchesGrade(p, "小学"));

        // 初中英语 (6)
        register("eng-grammar-rhyme", "英语", "语法口诀",
                p -> matchesSubject(p, "英语") && matchesGrade(p, "初中"));
        register("eng-reading", "英语", "阅读技巧",
                p -> matchesSubject(p, "英语") && matchesGrade(p, "初中"));
        register("eng-writing-template", "英语", "作文模板",
                p -> matchesSubject(p, "英语") && matchesGrade(p, "初中"));
        register("eng-listening", "英语", "听力训练",
                p -> matchesSubject(p, "英语") && matchesGrade(p, "初中"));
        register("eng-vocabulary", "英语", "词汇记忆",
                p -> matchesSubject(p, "英语") && matchesGrade(p, "初中"));
        register("eng-cloze", "英语", "完形填空",
                p -> matchesSubject(p, "英语") && matchesGrade(p, "初中"));

        // 少儿编程 (6)
        register("code-scratch", "编程", "Scratch入门",
                p -> matchesSubject(p, "编程"));
        register("code-logic", "编程", "逻辑思维",
                p -> matchesSubject(p, "编程"));
        register("code-showcase", "编程", "作品展示",
                p -> matchesSubject(p, "编程"));
        register("code-algorithm", "编程", "算法启蒙",
                p -> matchesSubject(p, "编程"));
        register("code-project", "编程", "项目实战",
                p -> matchesSubject(p, "编程"));
        register("code-competition", "编程", "竞赛备战",
                p -> matchesSubject(p, "编程"));

        // 艺考美术 (6)
        register("art-sketch", "美术", "素描基础",
                p -> matchesSubject(p, "美术"));
        register("art-color", "美术", "色彩搭配",
                p -> matchesSubject(p, "美术"));
        register("art-speed-draw", "美术", "速写技巧",
                p -> matchesSubject(p, "美术"));
        register("art-review", "美术", "作品点评",
                p -> matchesSubject(p, "美术"));
        register("art-exam-rush", "美术", "考试冲刺",
                p -> matchesSubject(p, "美术"));
        register("art-tools", "美术", "工具选择",
                p -> matchesSubject(p, "美术"));
    }

    private static void register(String id, String subject, String name, Predicate<TeacherProfile> predicate) {
        DIMENSIONS.add(new Dimension(id, subject, name, predicate));
    }

    public static List<Dimension> getAllDimensions() {
        return Collections.unmodifiableList(DIMENSIONS);
    }

    public static List<Dimension> matchDimensions(TeacherProfile profile) {
        return DIMENSIONS.stream()
                .filter(d -> d.predicate().test(profile))
                .toList();
    }

    private static boolean matchesSubject(TeacherProfile profile, String subject) {
        String s = profile.getSubject();
        return s != null && s.contains(subject);
    }

    private static boolean matchesGrade(TeacherProfile profile, String grade) {
        String g = profile.getGradeLevel();
        return g != null && g.contains(grade);
    }
}
