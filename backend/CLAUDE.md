# 星课 StarCourse

## 产品定位
微信小程序，帮三、四线城市独立教培老师在小红书上获客。
核心流程：问卷 → AI 人设定位 → 异步生成教学短视频脚本 → 文案工坊改写。

## 技术栈
- 后端：Spring Boot 3.5 + JDK 17 + JPA + PostgreSQL 16 (jsonb) + DeepSeek-v4-flash + JWT
- 前端：Taro 4.2 + React 18 + TypeScript
- 部署：docker buildx (amd64) + scp → 阿里云 ECS

## 工程铁律（6 条，commit 前自检）
1. 用户反馈 → feedback-log/
2. 架构决策 → ADR (Architecture Decision Record)
3. schema 和 API 变更 → 单一活文档，不散落
4. 版本管理 → iteration 分支
5. 发版 → CHANGELOG + git tag
6. 所有 LLM prompt 改动 → 必须跑 dry-run 回归

## 目录结构
- src/main/java/com/starcourse/
  - entity/          # JPA 实体
  - service/         # 业务逻辑
  - controller/      # REST API
  - record/          # DTO record POJO
  - prompt/          # DeepSeek prompt 模板
    - _shared/       # 共享片段 ({{include}})
- src/test/          # 单测，覆盖率 > 80%
- dry-runs/          # render.py + 全 case 化 fixture
- docs/
  - adr/             # ADR-0001.md, ADR-0002.md ...
  - feedback-log/
- migrations/        # SQL migration
- playbook/          # 行业素材 (24 dimension, 23 真实素材)

## 关键命令
- `./mvnw test`                — 跑单测
- `python dry-runs/render.py`  — dry-run 验证 prompt
- `docker buildx build --platform linux/amd64 -t starcourse .`
