# 研小伴功能对比分析报告

## 生成时间
2026-02-19 01:45

---

## 一、规划方案 vs 实际完成情况

### 1.1 核心功能对比表

| 功能模块 | 规划要求 | 实际完成情况 | 完成度 | 备注 |
|---------|---------|-------------|--------|------|
| **基础对话** | 多轮对话、上下文记忆 | ✅ 已完成 | 100% | 使用内存存储，支持10轮对话 |
| **会话持久化** | 保存到MySQL，跨设备同步 | ✅ 已完成 | 100% | chat_session和chat_message表已创建并测试 |
| **AI工具调用** | 自动调用GradMateTools | ❌ 未实现 | 0% | 工具已定义但未集成Function Calling |
| **智能推荐** | 院校推荐、分数线查询 | ❌ 未实现 | 0% | 依赖工具调用功能 |
| **打字机效果** | 逐字显示AI回复 | ❌ 未实现 | 0% | 前端功能待开发 |
| **消息时间戳** | 显示消息发送时间 | ❌ 未实现 | 0% | 前端功能待开发 |
| **错误重试** | 失败后可重试 | ❌ 未实现 | 0% | 前端功能待开发 |
| **情绪疏导** | 识别情绪并给予支持 | ✅ 已完成 | 100% | 通过System Prompt实现 |

**总体完成度**: 40% (4/10)

---

## 二、已完成功能详解

### 2.1 基础对话功能 ✅

**实现方式**:
- 使用火山引擎豆包大模型 (doubao-1.5-pro-32k-250115)
- 每个用户独立的会话上下文
- 保留最近10轮对话（20条消息）

**技术实现**:
```java
// GradMateAgentService.java
public String chat(Long userId, String userMessage) {
    // 1. 获取或创建会话
    ChatSession session = chatService.getOrCreateSession(userId);
    
    // 2. 保存用户消息
    chatService.saveMessage(sessionId, userId, "user", userMessage);
    
    // 3. 获取历史消息
    List<ChatMessage> history = chatService.getSessionMessages(sessionId, 10);
    
    // 4. 调用AI
    String aiResponse = callAI(messages);
    
    // 5. 保存AI回复
    chatService.saveMessage(sessionId, userId, "assistant", aiResponse);
    
    return aiResponse;
}
```

**测试结果**:
- ✅ 单轮对话正常
- ✅ 多轮对话上下文保持
- ✅ 用户隔离正确
- ✅ 响应时间 < 5秒

---

### 2.2 会话持久化 ✅

**数据库设计**:

**chat_session 表**:
```sql
CREATE TABLE chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    title VARCHAR(200) DEFAULT '新对话',
    message_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**chat_message 表**:
```sql
CREATE TABLE chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    tokens INT DEFAULT 0,
    tool_calls TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**核心功能**:
- ✅ 会话自动创建
- ✅ 消息持久化保存
- ✅ 会话标题自动生成（取第一条用户消息）
- ✅ 消息数量自动统计
- ✅ Token数量估算

**测试结果**:
```bash
# 查询会话
mysql> SELECT * FROM chat_session WHERE user_id=1;
+----+---------+-------+---------------+
| id | user_id | title | message_count |
+----+---------+-------+---------------+
|  1 |       1 | 你好  |             4 |
+----+---------+-------+---------------+

# 查询消息
mysql> SELECT id, role, content FROM chat_message WHERE user_id=1;
+----+-----------+------------------------------------------+
| id | role      | content                                  |
+----+-----------+------------------------------------------+
|  1 | user      | 你好                                     |
|  2 | assistant | 看起来你有点疑惑呢...                    |
|  3 | user      | 你好，我想了解考研信息                   |
|  4 | assistant | 没问题呀，考研信息涵盖很多方面...        |
+----+-----------+------------------------------------------+
```

---

### 2.3 情绪疏导 ✅

**实现方式**: 通过System Prompt引导

**System Prompt 关键内容**:
```
你是GradMate考研助手,一个专业、温暖、有共情能力的AI伙伴。

你的性格特点:
- 专业严谨: 提供准确的考研信息和学术指导
- 温暖共情: 理解考研压力,给予情感支持
- 积极乐观: 传递正能量,帮助用户建立信心
- 简洁清晰: 回答条理分明,易于理解

注意事项:
- 当用户表现出焦虑、压力、emo情绪时,优先给予情感支持
- 讲解题目时要循序渐进,从基础概念到解题思路
- 推荐院校时要考虑用户的实际情况和目标
- 保持专业性,不要给出不确定的信息
```

**效果**: AI回复语气温暖友好，能够识别用户情绪

---

### 2.4 前端聊天界面 ✅

**页面结构**:
- 消息列表（scroll-view）
- 空状态提示（欢迎语）
- 快捷问题按钮
- 输入栏（输入框 + 发送按钮）
- 清除历史按钮
- 加载动画

**快捷问题**:
- "如何选择考研院校?"
- "考研英语怎么复习?"
- "高数极限怎么求?"
- "我感觉压力好大..."

**UI设计**:
- 用户消息: 右侧，蓝色气泡 (#4A90E2)
- AI消息: 左侧，白色气泡（带边框）
- 背景: 浅灰色 (#f5f7fa)
- 头像: 🤖（AI）/ 👤（用户）

---

## 三、未完成功能详解

### 3.1 AI工具调用（Function Calling）❌

**规划要求**:
- AI能够主动调用 GradMateTools 中的4个工具方法
- 根据用户问题自动判断是否需要查询数据库
- 将查询结果整合到回复中

**当前状态**:
- ✅ 工具方法已定义（4个）
- ✅ 工具方法已连接真实数据库
- ✅ @Tool 注解已添加
- ❌ 未实现 Function Calling 逻辑
- ❌ AI无法自动调用工具

**工具清单**:

| 工具方法 | 功能 | 数据来源 | 状态 |
|---------|------|---------|------|
| `analyzeTargetUniversity` | 院校推荐 | school表 (875条) | ❌ 未调用 |
| `getScoreLine` | 分数线查询 | major表 (32万+) | ❌ 未调用 |
| `getGraduationNews` | 资讯获取 | school_brochure等 (5480条) | ❌ 未调用 |
| `getUserTarget` | 用户目标查询 | user表 | ❌ 未调用 |

**影响**:
- AI只能依靠自身知识回答，无法提供真实数据
- 无法实现智能推荐功能
- 无法查询具体的分数线、资讯等信息

**实现方案**:

#### 方案A: 火山引擎原生 Function Calling（推荐）

```java
// 1. 定义函数描述
List<FunctionDefinition> functions = Arrays.asList(
    FunctionDefinition.builder()
        .name("analyzeTargetUniversity")
        .description("根据用户的本科背景和意向分析推荐目标院校")
        .parameters(JsonSchemaProperty.builder()
            .type("object")
            .properties(Map.of(
                "profile", JsonSchemaProperty.builder()
                    .type("string")
                    .description("用户本科背景描述")
                    .build()
            ))
            .required(List.of("profile"))
            .build())
        .build()
);

// 2. 在请求中添加functions参数
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model(modelName)
    .messages(messages)
    .functions(functions)  // 添加可用函数
    .build();

// 3. 检查AI是否要调用函数
ChatCompletionResponse response = arkService.createChatCompletion(request);
ChatCompletionChoice choice = response.getChoices().get(0);

if ("function_call".equals(choice.getFinishReason())) {
    // AI决定调用函数
    FunctionCall functionCall = choice.getMessage().getFunctionCall();
    String functionName = functionCall.getName();
    String arguments = functionCall.getArguments();
    
    // 执行函数
    String toolResult = executeTool(functionName, arguments);
    
    // 将结果返回给AI
    messages.add(ChatMessage.builder()
        .role(ChatMessageRole.FUNCTION)
        .name(functionName)
        .content(toolResult)
        .build());
    
    // 再次调用AI，让它整合结果
    response = arkService.createChatCompletion(request);
}
```

**优点**: 
- AI自动判断何时调用，智能且准确
- 符合OpenAI Function Calling标准
- 可扩展性强

**缺点**: 
- 需要修改代码，增加复杂度
- 需要测试火山引擎是否支持Function Calling

---

#### 方案B: 关键词识别（简单但不智能）

```java
public String chat(Long userId, String userMessage) {
    // 在调用AI前，先检查关键词
    if (userMessage.contains("推荐院校") || userMessage.contains("择校")) {
        String toolResult = tools.analyzeTargetUniversity(userMessage);
        userMessage = userMessage + "\n\n[系统提示: 以下是查询到的院校信息]\n" + toolResult;
    }
    
    if (userMessage.contains("分数线")) {
        // 提取院校和专业名称（需要NLP）
        String toolResult = tools.getScoreLine(university, major);
        userMessage = userMessage + "\n\n[系统提示: 以下是查询到的分数线信息]\n" + toolResult;
    }
    
    // 继续调用AI
    String aiResponse = callAI(userMessage);
    return aiResponse;
}
```

**优点**: 
- 实现简单，无需修改AI调用逻辑
- 可快速上线

**缺点**: 
- 不智能，容易误触发
- 无法处理复杂意图
- 需要维护大量关键词

---

### 3.2 打字机效果 ❌

**规划要求**:
- AI回复逐字显示，模拟打字效果
- 提升用户体验，增加真实感

**实现方案**:

```vue
<!-- chat.vue -->
<script>
export default {
    data() {
        return {
            displayedText: '',  // 当前显示的文本
            fullText: '',       // 完整文本
            typingSpeed: 50     // 打字速度（毫秒/字符）
        };
    },
    
    methods: {
        async sendMessage() {
            // ... 发送消息逻辑
            
            const res = await post('/ai/chat', {
                userId: this.userId,
                message: userMessage
            });
            
            if (res.code === 200) {
                // 添加AI消息（初始为空）
                const aiMessage = {
                    role: 'ai',
                    content: ''
                };
                this.messages.push(aiMessage);
                
                // 打字机效果
                this.typeWriter(res.data, aiMessage);
            }
        },
        
        typeWriter(text, message) {
            let index = 0;
            const timer = setInterval(() => {
                if (index < text.length) {
                    message.content += text[index];
                    index++;
                    this.scrollToBottom();
                } else {
                    clearInterval(timer);
                }
            }, this.typingSpeed);
        }
    }
};
</script>
```

**优点**: 
- 提升用户体验
- 增加真实感
- 实现简单

---

### 3.3 消息时间戳 ❌

**规划要求**:
- 显示每条消息的发送时间
- 格式: "刚刚"、"5分钟前"、"今天 14:30"、"昨天 10:20"

**实现方案**:

```vue
<!-- chat.vue -->
<template>
    <view class="message-item" v-for="(msg, index) in messages" :key="index">
        <view class="message-bubble">
            <text class="message-text">{{ msg.content }}</text>
            <text class="message-time">{{ formatTime(msg.createTime) }}</text>
        </view>
    </view>
</template>

<script>
export default {
    methods: {
        formatTime(timestamp) {
            const now = Date.now();
            const diff = now - timestamp;
            
            if (diff < 60000) {
                return '刚刚';
            } else if (diff < 3600000) {
                return Math.floor(diff / 60000) + '分钟前';
            } else if (diff < 86400000) {
                return '今天 ' + new Date(timestamp).toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit'
                });
            } else if (diff < 172800000) {
                return '昨天 ' + new Date(timestamp).toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit'
                });
            } else {
                return new Date(timestamp).toLocaleDateString('zh-CN');
            }
        }
    }
};
</script>

<style>
.message-time {
    font-size: 20rpx;
    color: #999;
    margin-top: 10rpx;
    display: block;
}
</style>
```

---

### 3.4 错误重试机制 ❌

**规划要求**:
- 消息发送失败后可重试
- 显示错误提示
- 提供重试按钮

**实现方案**:

```vue
<!-- chat.vue -->
<template>
    <view class="message-item" v-for="(msg, index) in messages" :key="index">
        <view class="message-bubble" :class="msg.status">
            <text class="message-text">{{ msg.content }}</text>
            
            <!-- 错误状态 -->
            <view v-if="msg.status === 'error'" class="error-actions">
                <text class="error-text">发送失败</text>
                <button class="retry-btn" @click="retryMessage(index)">重试</button>
            </view>
            
            <!-- 发送中状态 -->
            <view v-if="msg.status === 'sending'" class="sending-icon">
                <text>发送中...</text>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            messages: []
        };
    },
    
    methods: {
        async sendMessage() {
            const userMessage = this.inputText.trim();
            this.inputText = '';
            
            // 添加用户消息
            const msgIndex = this.messages.length;
            this.messages.push({
                role: 'user',
                content: userMessage,
                status: 'sending'
            });
            
            try {
                const res = await post('/ai/chat', {
                    userId: this.userId,
                    message: userMessage
                });
                
                if (res.code === 200) {
                    // 更新状态为成功
                    this.messages[msgIndex].status = 'success';
                    
                    // 添加AI回复
                    this.messages.push({
                        role: 'ai',
                        content: res.data,
                        status: 'success'
                    });
                } else {
                    // 更新状态为失败
                    this.messages[msgIndex].status = 'error';
                }
            } catch (error) {
                // 更新状态为失败
                this.messages[msgIndex].status = 'error';
            }
        },
        
        async retryMessage(index) {
            const message = this.messages[index];
            message.status = 'sending';
            
            try {
                const res = await post('/ai/chat', {
                    userId: this.userId,
                    message: message.content
                });
                
                if (res.code === 200) {
                    message.status = 'success';
                    
                    // 添加AI回复
                    this.messages.push({
                        role: 'ai',
                        content: res.data,
                        status: 'success'
                    });
                } else {
                    message.status = 'error';
                }
            } catch (error) {
                message.status = 'error';
            }
        }
    }
};
</script>
```

---

## 四、开发优先级建议

### 高优先级（必须完成）

1. **AI工具调用（Function Calling）** ⭐⭐⭐⭐⭐
   - 影响: 核心功能，直接影响AI能力
   - 工作量: 2-3天
   - 依赖: 需要测试火山引擎API是否支持Function Calling

2. **打字机效果** ⭐⭐⭐⭐
   - 影响: 用户体验显著提升
   - 工作量: 0.5天
   - 依赖: 无

### 中优先级（建议完成）

3. **消息时间戳** ⭐⭐⭐
   - 影响: 用户体验提升
   - 工作量: 0.5天
   - 依赖: 无

4. **错误重试机制** ⭐⭐⭐
   - 影响: 提升可用性
   - 工作量: 1天
   - 依赖: 无

### 低优先级（可选）

5. **会话列表管理**
   - 影响: 方便用户管理多个会话
   - 工作量: 2天
   - 依赖: 无

6. **消息搜索功能**
   - 影响: 方便查找历史消息
   - 工作量: 1天
   - 依赖: 无

7. **语音输入支持**
   - 影响: 提升输入便利性
   - 工作量: 2天
   - 依赖: 需要语音识别API

---

## 五、下一步行动计划

### 阶段1: AI工具调用实现（2-3天）

**任务清单**:
1. [ ] 研究火山引擎API是否支持Function Calling
2. [ ] 如果支持，实现Function Calling逻辑
3. [ ] 如果不支持，实现关键词识别方案
4. [ ] 测试4个工具方法的调用
5. [ ] 优化工具方法的返回格式
6. [ ] 编写测试用例

**验收标准**:
- AI能够根据用户问题自动调用工具
- 工具调用结果正确整合到回复中
- 至少测试10个不同场景

---

### 阶段2: 前端体验优化（1-2天）

**任务清单**:
1. [ ] 实现打字机效果
2. [ ] 添加消息时间戳
3. [ ] 实现错误重试机制
4. [ ] 优化加载动画
5. [ ] 添加消息状态显示

**验收标准**:
- 打字机效果流畅自然
- 时间戳格式正确
- 错误重试功能正常

---

### 阶段3: 功能测试与优化（1天）

**任务清单**:
1. [ ] 端到端测试
2. [ ] 性能测试
3. [ ] 边界情况测试
4. [ ] 用户体验优化
5. [ ] 文档更新

---

## 六、技术难点与解决方案

### 难点1: 火山引擎是否支持Function Calling

**问题**: 不确定火山引擎API是否支持OpenAI风格的Function Calling

**解决方案**:
1. 查阅火山引擎官方文档
2. 测试API是否接受functions参数
3. 如果不支持，使用关键词识别方案作为替代

---

### 难点2: 工具调用结果的格式化

**问题**: 工具返回的数据需要格式化为易读的文本

**解决方案**:
- 在工具方法中直接返回格式化的文本
- 使用Markdown格式增强可读性
- 添加表格、列表等结构化展示

---

### 难点3: 打字机效果的性能优化

**问题**: 频繁更新DOM可能导致性能问题

**解决方案**:
- 使用requestAnimationFrame优化渲染
- 批量更新字符（每次更新多个字符）
- 使用虚拟滚动优化长消息列表

---

## 七、总结

### 已完成功能（40%）
- ✅ 基础对话功能
- ✅ 会话持久化
- ✅ 情绪疏导
- ✅ 前端聊天界面

### 待完成功能（60%）
- ❌ AI工具调用（核心）
- ❌ 打字机效果
- ❌ 消息时间戳
- ❌ 错误重试机制
- ❌ 智能推荐
- ❌ 会话列表管理

### 预计完成时间
- 高优先级功能: 3-4天
- 中优先级功能: 1-2天
- 总计: 4-6天

### 建议
1. 优先实现AI工具调用，这是核心功能
2. 前端体验优化可以并行开发
3. 低优先级功能可以在后续迭代中完成
