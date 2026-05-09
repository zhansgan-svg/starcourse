# ADR-0003: 叙事模型

## 状态

已接受

## 背景

v0.1.0 中，用户通过问卷一次性提供所有信息（科目、年级、风格等），系统据此生成脚本。

随着"按需拉取"模式的引入（见 ADR-0002），需要一种方式让老师表达"下一步想教什么"。问卷是静态的，但教学方向是动态的——老师可能今天想讲口算技巧，明天想讲应用题。

## 决策

引入"叙事模型"（Narrative Model），让老师通过描述自己的教学故事来引导脚本生成：

### 核心概念

1. **StoryProfile（叙事档案）**：每个老师有一个叙事档案，记录当前的教学叙事和目标
   - `current_narrative`：当前教学叙事（如"最近班上孩子口算总是出错，我想做个视频讲讲速算技巧"）
   - `teaching_goal`：教学目标（如"帮孩子提升口算速度"）

2. **NarrativeRequest（叙事请求）**：每次拉取脚本时，老师可以描述当前想讲的主题
   - 支持简单文本描述
   - 系统结合 TeacherProfile + StoryProfile + NarrativeRequest 生成脚本

3. **TopicQueue（话题队列）**：管理待生成的脚本队列
   - 支持优先级排序
   - 支持状态流转（PENDING → GENERATING → COMPLETED → ARCHIVED）

### 数据流

```
用户描述叙事 → 创建 NarrativeRequest
    → 查询 StoryProfile 获取上下文
    → 结合 TeacherProfile + PlaybookMaterials
    → 调用 DeepSeek 生成单个脚本
    → 保存到 TopicCandidate
    → 更新 TopicQueue 状态
```

## 后果

### 正面

- **表达更自由**：老师可以用自然语言描述想教什么，而非受限于固定选项
- **上下文连续**：StoryProfile 保持教学叙事的连续性，后续脚本可以基于之前的探索方向
- **个性化更强**：同一个老师在不同阶段可以探索不同主题
- **数据积累**：叙事数据可以用于分析老师的教学习惯和偏好

### 负面

- **NLP 依赖**：需要 LLM 理解老师的自然语言描述
- **数据模型复杂度**：新增 2 张表，关系更复杂
- **冷启动问题**：新老师没有叙事历史，需要基于问卷数据生成初始叙事

### 风险

- **叙事质量**：老师的描述可能过于模糊（如"想做个数学视频"），影响生成质量
- **上下文漂移**：如果叙事更新太频繁，可能导致脚本方向不一致

## 实现细节

### 新增实体

- `StoryProfile`：叙事档案，与 TeacherProfile 一对一关联
- `TopicQueue`：话题队列，与 StoryProfile 和 TopicCandidate 关联

### 新增字段

- `TopicCandidate.narrativeContext`：生成该脚本时的叙事上下文
- `TopicCandidate.pullReason`：拉取该脚本的原因

### 服务变更

- `StoryProfileService`：管理叙事档案的 CRUD 和更新
- `TopicQueueService`：管理话题队列的添加、消费、状态更新
- `ScriptGenerationService`：从批量生成改为基于叙事的单次生成

## 相关文档

- [ADR-0002: 从批量推送改为按需拉取](ADR-0002-push-to-pull-pivot.md)
- [V003 迁移脚本](../../migrations/V003__pivot_to_pull_model.sql)
