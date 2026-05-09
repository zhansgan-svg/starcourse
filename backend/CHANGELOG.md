# Changelog

All notable changes to this project will be documented in this file.

## [0.2.0] - 2026-05-09

### 架构变更

- **从批量推送改为按需拉取**（ADR-0002）：核心流程从一次性生成 5 个脚本改为按需拉取单个脚本
- **引入叙事模型**（ADR-0003）：新增 StoryProfile 和 TopicQueue 实体，支持教师通过叙事描述引导脚本生成

### 新增

- **实体**
  - `StoryProfile`：叙事档案，记录教师当前教学叙事和目标
  - `TopicQueue`：话题队列，管理待生成脚本的优先级和状态

- **字段**
  - `TopicCandidate.narrativeContext`：生成脚本时的叙事上下文
  - `TopicCandidate.pullReason`：拉取该脚本的原因

- **DTO**
  - `StoryProfileDTO`：叙事档案数据传输对象
  - `TopicQueueDTO`：话题队列数据传输对象
  - `NarrativeRequestDTO`：拉取脚本的请求体
  - `ScriptPullResponseDTO`：单脚本拉取响应
  - `TeacherProfileSummaryDTO`：教师档案轻量摘要
  - `RewriteRequestDTO`：结构化改写请求

- **服务**
  - `StoryProfileService`：叙事档案的 CRUD 和更新
  - `TopicQueueService`：话题队列的添加、消费、状态管理

- **控制器**
  - `StoryProfileController`：叙事档案的 REST API
    - `POST /api/story-profile`：创建叙事档案
    - `GET /api/story-profile/{id}`：获取叙事档案
    - `PUT /api/story-profile/{id}/narrative`：更新叙事
    - `GET /api/story-profile/{id}/narrative`：获取当前叙事

- **数据库迁移**
  - `V003__pivot_to_pull_model.sql`：新增 story_profile、topic_queue 表，topic_candidate 增加 narrative_context 和 pull_reason 字段

- **测试**
  - `StoryProfileServiceTest`：叙事档案服务测试
  - `TopicQueueServiceTest`：话题队列服务测试

### 变更

- **API**
  - `POST /api/scripts/generate/{id}?count=5` → `POST /api/scripts/pull`（接受 NarrativeRequestDTO）
  - `ScriptGenerationService.generateScripts()` → `generateSingleScript()`

- **脚本生成逻辑**
  - 每次只生成 1 个脚本，基于叙事上下文精准匹配
  - 支持 LLM 返回单个 JSON 对象或 JSON 数组（取第一个）
  - prompt 模板新增 `{{narrativeContext}}` 占位符

### 文档

- 新增 `docs/adr/ADR-0002-push-to-pull-pivot.md`
- 新增 `docs/adr/ADR-0003-narrative-model.md`

## [0.1.0] - 初始版本

### 核心功能

- 问卷提交 → AI 人设定位
- 批量生成 5 个教学短视频脚本
- 文案工坊改写功能
- Playbook 行业素材库（24 维度，23 真实素材）
