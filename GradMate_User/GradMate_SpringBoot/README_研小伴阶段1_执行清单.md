# 研小伴功能完善 - 阶段1执行清单

> 开发时间：2026-02-19

---

## 已完成内容 ✅

### 1. 数据库设计
- ✅ 创建 `chat_session` 表（会话表）
- ✅ 创建 `chat_message` 表（消息表）
- ✅ SQL文件：`src/main/resources/sql/chat_tables.sql`

### 2. 实体类
- ✅ `ChatSession.java` - 会话实体
- ✅ `ChatMessage.java` - 消息实体

### 3. Mapper层
- ✅ `ChatSessionMapper.java` - 会话Mapper
- ✅ `ChatMessageMapper.java` - 消息Mapper

### 4. Service层
- ✅ `ChatService.java` - 聊天服务
  - 会话管理（创建、查询）
  - 消息持久化（保存、查询）
  - 历史清除
  - 自动生成会话标题
  - Token估算

### 5. 集成改造
- ✅ 修改 `GradMateAgentService.java`
  - 移除内存存储（ChatMemory）
  - 集成数据库持久化
  - 保持API接口不变

---

## 待执行步骤 📋

### 步骤1：执行SQL创建表

```bash
# 连接MySQL数据库
mysql -u root -pA123456 gradmate

# 执行SQL文件
source GradMate_SpringBoot/src/main/resources/sql/chat_tables.sql;

# 验证表已创建
SHOW TABLES LIKE 'chat%';
DESC chat_session;
DESC chat_message;
```

### 步骤2：重启Spring Boot应用

```bash
# 停止当前运行的应用
# 重新运行 GradMateSpringBootApplication

# 查看启动日志，确认无错误
```

### 步骤3：测试基础功能

**测试1：发送消息**
```bash
curl -X POST http://localhost:9090/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "你好，研小伴！"
  }'
```

**预期结果**：
- 返回AI回复
- 数据库中创建会话记录
- 数据库中保存用户消息和AI回复

**测试2：查看数据库**
```sql
-- 查看会话
SELECT * FROM chat_session WHERE user_id = 1;

-- 查看消息
SELECT * FROM chat_message WHERE user_id = 1 ORDER BY create_time;
```

**测试3：继续对话**
```bash
curl -X POST http://localhost:9090/api/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "我想了解考研院校推荐"
  }'
```

**预期结果**：
- AI能够记住上下文
- 消息数量增加
- 会话标题自动更新

**测试4：清除历史**
```bash
curl -X DELETE "http://localhost:9090/api/ai/history?userId=1"
```

**预期结果**：
- 会话和消息被删除
- 下次对话创建新会话

### 步骤4：前端测试

1. 打开研小伴页面
2. 发送消息："你好"
3. 查看AI回复
4. 继续对话，验证上下文记忆
5. 点击清除历史按钮
6. 刷新页面，验证历史已清除

---

## 功能特性

### 会话管理
- ✅ 自动创建会话
- ✅ 会话ID唯一标识
- ✅ 自动生成会话标题（取第一条消息前20字）
- ✅ 记录消息数量
- ✅ 记录创建和更新时间

### 消息管理
- ✅ 保存用户消息
- ✅ 保存AI回复
- ✅ 记录消息角色（user/assistant）
- ✅ 估算Token数量
- ✅ 记录创建时间

### 历史管理
- ✅ 获取最近N条消息
- ✅ 清除用户所有历史
- ✅ 消息按时间排序

---

## 数据库表结构

### chat_session 表
```sql
id              BIGINT          主键，自增
user_id         BIGINT          用户ID
session_id      VARCHAR(64)     会话唯一标识（UUID）
title           VARCHAR(200)    会话标题
message_count   INT             消息数量
create_time     DATETIME        创建时间
update_time     DATETIME        更新时间
```

### chat_message 表
```sql
id              BIGINT          主键，自增
session_id      VARCHAR(64)     会话ID
user_id         BIGINT          用户ID
role            VARCHAR(20)     角色（user/assistant/system）
content         TEXT            消息内容
tokens          INT             Token数量
tool_calls      TEXT            工具调用记录（JSON）
create_time     DATETIME        创建时间
```

---

## API接口

### 1. 发送消息
```
POST /api/ai/chat
Content-Type: application/json

{
  "userId": 1,
  "message": "你好"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": "你好！我是研小伴..."
}
```

### 2. 清除历史
```
DELETE /api/ai/history?userId=1

Response:
{
  "code": 200,
  "message": "success",
  "data": "会话历史已清除"
}
```

### 3. 获取会话信息
```
GET /api/ai/session?userId=1

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "messageCount": 10,
    "hasHistory": true
  }
}
```

---

## 优势对比

### 之前（内存存储）
- ❌ 服务重启丢失历史
- ❌ 无法跨设备同步
- ❌ 无法查看历史会话
- ❌ 无法统计使用数据

### 现在（数据库存储）
- ✅ 持久化存储，永不丢失
- ✅ 支持跨设备同步
- ✅ 可查看历史会话列表
- ✅ 可统计Token使用量
- ✅ 可分析用户行为
- ✅ 可导出对话记录

---

## 注意事项

1. **数据库连接**
   - 确保MySQL服务运行
   - 确认数据库名为 `gradmate`
   - 确认用户名密码正确

2. **表创建**
   - 必须先执行SQL创建表
   - 验证表结构正确

3. **重启应用**
   - 修改代码后必须重启
   - 查看启动日志确认无错误

4. **测试顺序**
   - 先测试后端API
   - 再测试前端界面
   - 最后测试完整流程

---

## 下一步计划

### 阶段1剩余任务
- [ ] 添加打字机效果（前端）
- [ ] 添加消息时间戳（前端）
- [ ] 添加错误重试机制（前端）
- [ ] 优化UI样式（前端）

### 阶段2：智能推荐
- [ ] 集成LangChain4j Agent
- [ ] 实现工具自动调用
- [ ] 个性化院校推荐
- [ ] 学习计划生成

---

## 状态

✅ 会话持久化已完成
🔄 待执行SQL和测试
⏳ 前端体验优化待开发
⏳ Agent集成待开发
