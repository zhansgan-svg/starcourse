# 星课 StarCourse

微信小程序，帮三、四线城市独立教培老师在小红书获客。

## 产品流程

1. **问卷采集** — 老师填 10 题问卷（学科、年级、教学风格、优势、可拍摄内容、发布频率、目标客群、定价区间、平台偏好）
2. **AI 人设定位** — AI 30 秒生成：专属昵称、头像建议、一句话定位
3. **视频脚本生成** — 后台异步生成教学短视频脚本（标题、钩子、口播、拍摄要点）（后端接口待联调）
4. **文案工坊** — 一键改写文案（更口语化/加家长痛点/强调效果对比）（后端接口待联调）

## 技术栈

- **后端**：Spring Boot 3.5 + JDK 17 + JPA + PostgreSQL 16 (jsonb) + kimi + JWT
- **前端**：Taro 4.2 + React 18 + TypeScript

## 项目结构

```
starcourse/
├── backend/    # Spring Boot 后端
│   ├── src/main/java/com/starcourse/
│   │   ├── controller/    # REST API
│   │   ├── service/       # 业务逻辑
│   │   ├── entity/        # JPA 实体
│   │   ├── record/        # DTO
│   │   └── prompt/        # LLM prompt 模板
│   └── src/test/          # 单元测试
└── frontend/   # Taro 前端
    └── src/pages/         # 页面组件
```

## 本地运行

### 后端

```bash
cd backend
# 配置 PostgreSQL 连接和 LLM API Key
./mvnw spring-boot:run
```

### 前端

```bash
cd frontend
npm install
npx taro build --type weapp
# 用微信开发者工具打开 dist/ 目录
```

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/questionnaire/submit | 提交问卷，生成 AI 人设 |
| POST | /api/scripts/pull | 生成视频脚本 |(后端接口待联调)
| GET  | /api/scripts/list/{id} | 获取脚本列表 |
| POST | /api/rewrite | 文案改写 |(后端接口待联调)
| GET  | /api/rewrite/history/{id} | 改写历史 |（还在开发中）
