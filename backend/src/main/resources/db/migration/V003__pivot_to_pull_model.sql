-- V003: Pivot from batch push to pull model
-- Adds narrative context to topic_candidate and creates story_profile + topic_queue tables

-- Add narrative fields to topic_candidate
ALTER TABLE topic_candidate
    ADD COLUMN narrative_context TEXT,
    ADD COLUMN pull_reason VARCHAR(255);

-- Create story_profile table
CREATE TABLE story_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_profile_id UUID NOT NULL REFERENCES teacher_profile(id) ON DELETE CASCADE,
    current_narrative TEXT,
    teaching_goal TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_story_profile_teacher ON story_profile(teacher_profile_id);

-- Create topic_queue table
CREATE TABLE topic_queue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    story_profile_id UUID NOT NULL REFERENCES story_profile(id) ON DELETE CASCADE,
    topic_candidate_id UUID REFERENCES topic_candidate(id) ON DELETE SET NULL,
    priority INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_topic_queue_story ON topic_queue(story_profile_id);
CREATE INDEX idx_topic_queue_status ON topic_queue(status);
