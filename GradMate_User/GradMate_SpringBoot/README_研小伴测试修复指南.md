# 研小伴功能测试与修复指南

> 测试时间：2026-02-19

---

## 测试前准备

### 1. 创建数据库表

```bash
# 连接MySQL
mysql -u root -pA123456 gradmate

# 执行SQL
source GradMate_SpringBoot/src/main/resources/sql/chat_tables.sql

# 验证表已创建
SHOW TABLES LIKE 'chat%';
```

**预期结果**：
```
+---------------------------+
| Tables_in_gradmate (chat%)|
+---------------------------+
| chat_message              |
| chat_session              |
+---------------------------+
```

### 2. 重启Spring Boot应用

- 停止当前运行的应用
- 重新运行 `GradMateSpringBootApplication`
- 查看启动日志，确认无错误

### 3. 检查端口

```bash
# 检查9090端口是否被占用
netstat -ano | findstr :9090

# 或使用PowerShell
Get-NetTCPConnection -LocalPort 9090
```

---

## 后端API测试

### 方法1：使用测试脚本（推荐）

```bash
# Windows PowerShell
cd GradMate_SpringBoot
bash test_chat_api.sh

# 或手动执行每个测试
```

### 方法2：使用curl命令

**测试1：发送消息**
```bash
curl -X POST http://localhost:9090/api/ai/chat ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\": 1, \"message\": \"你好，研小伴！\"}"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": "你好！我是研小伴，很高兴为你服务..."
}
```

**测试2：获取会话信息**
```bash
curl http://localhost:9090/api/ai/session?userId=1
```

**预期响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "messageCount": 2,
    "hasHistory": true
  }
}
```

**测试3：清除历史**
```bash
curl -X DELETE http://localhost:9090/api/ai/history?userId=1
```

**预期响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": "会话历史已清除"
}
```

### 方法3：使用Postman

1. 导入以下请求：
   - POST `http://localhost:9090/api/ai/chat`
   - GET `http://localhost:9090/api/ai/session?userId=1`
   - DELETE `http://localhost:9090/api/ai/history?userId=1`

2. 设置Headers：
   - `Content-Type: application/json`

3. 设置Body（POST请求）：
   ```json
   {
     "userId": 1,
     "message": "你好"
   }
   ```

---

## 数据库验证

### 执行验证脚本

```bash
mysql -u root -pA123456 gradmate < GradMate_SpringBoot/test_database.sql
```

### 手动查询

```sql
-- 查看会话
SELECT * FROM chat_session WHERE user_id = 1;

-- 查看消息
SELECT 
    id,
    role,
    LEFT(content, 50) as content,
    create_time
FROM chat_message 
WHERE user_id = 1 
ORDER BY create_time;

-- 统计信息
SELECT 
    COUNT(*) as session_count 
FROM chat_session;

SELECT 
    COUNT(*) as message_count 
FROM chat_message;
```

---

## 前端测试

### 1. 启动前端

```bash
# 在HBuilderX中运行到浏览器
# 或使用命令行
cd GradMate_Uniapp
npm run dev:h5
```

### 2. 测试步骤

1. **打开研小伴页面**
   - 点击底部导航栏的"研小伴"

2. **发送第一条消息**
   - 输入："你好"
   - 点击发送
   - 观察AI回复

3. **测试上下文记忆**
   - 输入："我刚才说了什么？"
   - 验证AI能否记住上一条消息

4. **测试快捷问题**
   - 点击快捷问题按钮
   - 验证消息自动填充并发送

5. **测试清除历史**
   - 点击右下角的清除按钮
   - 确认清除
   - 刷新页面，验证历史已清空

---

## 常见问题修复

### 问题1：表不存在

**错误信息**：
```
Table 'gradmate.chat_session' doesn't exist
```

**解决方法**：
```bash
mysql -u root -pA123456 gradmate < GradMate_SpringBoot/src/main/resources/sql/chat_tables.sql
```

### 问题2：连接被拒绝

**错误信息**：
```
Connection refused: connect
```

**解决方法**：
1. 检查Spring Boot是否启动
2. 检查端口9090是否正确
3. 检查防火墙设置

### 问题3：AI回复为空

**可能原因**：
- 火山引擎API配置错误
- API密钥过期
- 网络连接问题

**解决方法**：
1. 检查 `application.yml` 中的API配置
2. 查看后端日志中的错误信息
3. 测试API连接

### 问题4：前端无法发送消息

**检查项**：
1. 浏览器控制台是否有错误
2. Network面板查看请求状态
3. 检查BASE_URL是否正确（`http://localhost:9090/api`）

**解决方法**：
```javascript
// 在chat.vue中添加调试日志
console.log('发送消息:', userMessage);
console.log('API响应:', res);
```

### 问题5：会话标题不更新

**原因**：
- 消息保存后会话更新逻辑有问题

**解决方法**：
检查 `ChatService.updateSession()` 方法是否正确执行

---

## 性能测试

### 1. 响应时间测试

```bash
# 测试10次请求的平均响应时间
for i in {1..10}; do
  time curl -X POST http://localhost:9090/api/ai/chat \
    -H "Content-Type: application/json" \
    -d "{\"userId\": 1, \"message\": \"测试$i\"}"
done
```

**预期**：平均响应时间 < 3秒

### 2. 并发测试

```bash
# 使用Apache Bench测试
ab -n 100 -c 10 -p test_data.json -T application/json \
  http://localhost:9090/api/ai/chat
```

**预期**：成功率 > 95%

### 3. 数据库性能

```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query%';

-- 分析查询性能
EXPLAIN SELECT * FROM chat_message WHERE session_id = 'xxx';
```

---

## 代码检查清单

### 后端检查

- [ ] ChatSession实体类字段完整
- [ ] ChatMessage实体类字段完整
- [ ] ChatSessionMapper继承BaseMapper
- [ ] ChatMessageMapper继承BaseMapper
- [ ] ChatService注入Mapper正确
- [ ] GradMateAgentService注入ChatService正确
- [ ] AiController方法签名正确
- [ ] 异常处理完善

### 前端检查

- [ ] request.js BASE_URL正确
- [ ] chat.vue导入request.js正确
- [ ] 消息发送逻辑正确
- [ ] 消息显示逻辑正确
- [ ] 清除历史逻辑正确
- [ ] 错误处理完善

### 数据库检查

- [ ] 表已创建
- [ ] 索引已创建
- [ ] 字段类型正确
- [ ] 字符集为utf8mb4

---

## 测试报告模板

### 测试环境
- 操作系统：Windows
- MySQL版本：8.0.34
- Spring Boot版本：3.x
- 前端框架：uni-app

### 测试结果

| 测试项 | 状态 | 备注 |
|--------|------|------|
| 数据库表创建 | ✅ | 表结构正确 |
| 发送消息 | ✅ | 响应正常 |
| 上下文记忆 | ✅ | 能记住历史 |
| 清除历史 | ✅ | 清除成功 |
| 会话标题生成 | ✅ | 自动生成 |
| Token统计 | ✅ | 估算准确 |
| 前端显示 | ✅ | UI正常 |
| 错误处理 | ✅ | 提示友好 |

### 发现的问题

1. 问题描述
2. 重现步骤
3. 预期结果
4. 实际结果
5. 修复方案

---

## 下一步优化

### 功能优化
- [ ] 添加打字机效果
- [ ] 添加消息时间戳
- [ ] 添加消息状态（发送中、已发送、失败）
- [ ] 添加重试机制
- [ ] 添加会话列表

### 性能优化
- [ ] 添加Redis缓存
- [ ] 优化数据库查询
- [ ] 添加消息分页
- [ ] 压缩历史消息

### 安全优化
- [ ] 添加用户认证
- [ ] 添加消息加密
- [ ] 添加敏感词过滤
- [ ] 添加频率限制

---

## 总结

完成以上测试后，研小伴的会话持久化功能应该能够正常工作。如果遇到问题，请按照本指南进行排查和修复。
