# 多会话管理功能开发说明

## 问题分析

编译失败的原因：
1. ChatService.java 中方法重复定义（getUserSessions, createNewSession, getSessionById, deleteSession 被定义了两次）
2. 实体类已经有 @Data 注解，应该能正常生成 getter/setter

## 解决方案

需要修复 ChatService.java 文件，删除重复的方法定义。

## 已完成的工作

### 后端修改

1. **ChatService.java** - 添加了会话管理方法：
   - `createNewSession(userId)` - 创建新会话
   - `getSessionById(sessionId)` - 根据ID获取会话
   - `deleteSession(sessionId)` - 删除指定会话
   - `updateSessionTitle(sessionId, title)` - 更新会话标题

2. **GradMateAgentService.java** - 修改chat方法支持sessionId参数：
   - 添加重载方法 `chat(userId, sessionId, message)`
   - 原方法 `chat(userId, message)` 调用新方法

3. **AiController.java** - 添加会话管理接口：
   - `POST /api/ai/session/create` - 创建新会话
   - `GET /api/ai/session/list` - 获取会话列表
   - `DELETE /api/ai/session/{sessionId}` - 删除会话
   - `PUT /api/ai/session/{sessionId}/title` - 更新会话标题
   - 修改 `POST /api/ai/chat` 支持可选的 sessionId 参数

### 前端修改

1. **chat.vue** - 完整重写，添加多会话管理UI：
   - 左侧会话列表侧边栏
   - 新建会话按钮
   - 会话切换功能
   - 会话重命名功能
   - 会话删除功能
   - 顶部栏显示当前会话标题
   - 菜单按钮切换侧边栏

## 下一步操作

1. 修复 ChatService.java 中的重复方法定义
2. 重新编译项目
3. 启动后端服务
4. 测试多会话管理功能

## API接口说明

### 1. 创建新会话
```
POST /api/ai/session/create
Request: { userId: 1 }
Response: {
  code: 200,
  data: {
    sessionId: "uuid",
    title: "新对话",
    createTime: "2024-02-19T20:00:00"
  }
}
```

### 2. 获取会话列表
```
GET /api/ai/session/list?userId=1
Response: {
  code: 200,
  data: [
    {
      sessionId: "uuid",
      title: "如何选择考研院校?",
      messageCount: 5,
      createTime: "2024-02-19T20:00:00",
      updateTime: "2024-02-19T20:05:00"
    }
  ]
}
```

### 3. 删除会话
```
DELETE /api/ai/session/{sessionId}
Response: {
  code: 200,
  data: "会话已删除"
}
```

### 4. 更新会话标题
```
PUT /api/ai/session/{sessionId}/title
Request: { title: "新标题" }
Response: {
  code: 200,
  data: "标题已更新"
}
```

### 5. AI对话（支持指定会话）
```
POST /api/ai/chat
Request: {
  userId: 1,
  sessionId: "uuid",  // 可选，不传则使用最近会话
  message: "你好"
}
Response: {
  code: 200,
  data: "AI回复内容"
}
```

## 功能特性

1. **多会话管理**：用户可以创建多个独立的对话会话
2. **会话列表**：显示所有会话，按更新时间倒序排列
3. **会话切换**：点击会话可切换到该会话
4. **会话重命名**：支持自定义会话标题
5. **会话删除**：删除不需要的会话
6. **自动标题**：新会话自动使用第一条用户消息作为标题
7. **消息计数**：显示每个会话的消息数量
8. **持久化**：所有会话和消息都保存在数据库中

## UI设计

- 左侧侧边栏：会话列表（可滑出/收起）
- 顶部栏：菜单按钮 + 当前会话标题
- 主内容区：消息列表 + 输入框
- 遮罩层：点击关闭侧边栏
- 重命名对话框：模态对话框

## 注意事项

1. 会话ID使用UUID生成，确保唯一性
2. 删除会话时会同时删除该会话的所有消息
3. 如果删除当前会话，自动切换到第一个会话或创建新会话
4. 会话标题最长50字符
5. 消息内容最长1000字符
