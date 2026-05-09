-- Teacher profile table
CREATE TABLE teacher_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    open_id VARCHAR(255) UNIQUE NOT NULL,
    nickname VARCHAR(255),
    avatar_url VARCHAR(512),
    subject VARCHAR(100),
    grade_level VARCHAR(100),
    style VARCHAR(100),
    strengths JSONB,
    target_audience JSONB,
    pricing_range VARCHAR(100),
    differentiator VARCHAR(255),
    platform_preference VARCHAR(100) DEFAULT '小红书',
    positioning VARCHAR(512),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Topic candidate table (video script candidate)
CREATE TABLE topic_candidate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_profile_id UUID NOT NULL REFERENCES teacher_profile(id) ON DELETE CASCADE,
    title VARCHAR(255),
    hook TEXT,
    outline TEXT,
    full_script TEXT,
    shooting_tips TEXT,
    storyboard JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Script feedback table (rewrite feedback)
CREATE TABLE script_feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_candidate_id UUID NOT NULL REFERENCES topic_candidate(id) ON DELETE CASCADE,
    direction VARCHAR(255),
    original_content TEXT,
    rewritten_content TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Indexes for foreign keys
CREATE INDEX idx_topic_candidate_teacher_profile_id ON topic_candidate(teacher_profile_id);
CREATE INDEX idx_script_feedback_topic_candidate_id ON script_feedback(topic_candidate_id);

-- Index for common queries
CREATE INDEX idx_teacher_profile_open_id ON teacher_profile(open_id);
CREATE INDEX idx_topic_candidate_status ON topic_candidate(status);
