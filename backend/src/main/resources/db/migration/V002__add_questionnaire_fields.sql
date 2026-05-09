-- Add questionnaire-related fields to teacher_profile
ALTER TABLE teacher_profile ADD COLUMN shootable_content JSONB;
ALTER TABLE teacher_profile ADD COLUMN frequency VARCHAR(50);
ALTER TABLE teacher_profile ADD COLUMN avatar_suggestion VARCHAR(512);
