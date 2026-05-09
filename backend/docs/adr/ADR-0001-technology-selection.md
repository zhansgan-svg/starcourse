# ADR-0001: 技术选型决策

## 状态

已接受

## 背景

星课(StarCourse)是一款微信小程序，帮三、四线城市独立教培老师在小红书上获客。核心流程：问卷 → AI 人设定位 → 异步生成教学短视频脚本 → 文案工坊改写。

需要选择合适的技术栈来支撑：
- 微信小程序后端 API
- 用户认证与授权
- AI 内容生成（调用 DeepSeek API）
- 数据持久化（用户数据、问卷数据、脚本数据）

## 决策

### 后端技术栈

| 组件 | 选择 | 理由 |
|------|------|------|
| **框架** | Spring Boot 3.5 | 成熟稳定，生态丰富，国内社区活跃，适合快速开发 |
| **JDK** | JDK 17 | LTS 版本，支持最新语言特性（record、sealed class、text block），Spring Boot 3.x 最低要求 |
| **ORM** | Spring Data JPA | 简化数据库操作，Repository 模式减少样板代码，支持 PostgreSQL jsonb |
| **数据库** | PostgreSQL 16 | 支持 jsonb 存储灵活的问卷/脚本数据，性能优秀，开源免费 |
| **AI 模型** | DeepSeek-v4-flash | 国产大模型，中文理解能力强，响应速度快，成本可控 |
| **认证** | JWT (jjwt) | 无状态认证，适合小程序场景，减轻服务端 session 存储压力 |

### 前端技术栈

| 组件 | 选择 | 理由 |
|------|------|------|
| **跨端框架** | Taro 4.2 | 一套代码编译到微信小程序，国内团队维护，文档中文友好 |
| **UI 框架** | React 18 | 组件化开发，Hooks 简化状态管理，Taro 原生支持 |
| **语言** | TypeScript | 类型安全，IDE 智能提示，减少运行时错误 |

## 后果

### 正面

- **开发效率高**：Spring Boot + JPA 快速搭建 CRUD，Taro + React 快速迭代 UI
- **技术成熟度**：所有技术栈都是经过生产验证的成熟方案
- **团队学习成本低**：中文文档丰富，社区活跃，遇到问题容易找到解决方案
- **扩展性强**：PostgreSQL jsonb 灵活存储，Spring Security 方便扩展认证逻辑
- **成本可控**：全部开源技术，DeepSeek API 定价合理

### 负面

- **Spring Boot 3.x 学习曲线**：相比 2.x 有破坏性变更（Jakarta EE 迁移）
- **JPA 性能陷阱**：复杂查询可能需要手写 SQL 或使用 MyBatis
- **Taro 限制**：部分原生小程序 API 需要条件编译

### 风险

- **DeepSeek API 可用性**：需要做好降级方案（本地缓存、重试机制）
- **PostgreSQL 运维**：需要定期备份，监控连接池状态

## 相关文档

- [Spring Boot 3.x 迁移指南](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Taro 官方文档](https://taro-docs.jd.com/)
- [DeepSeek API 文档](https://platform.deepseek.com/)
