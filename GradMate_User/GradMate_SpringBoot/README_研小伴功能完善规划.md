# 研小伴功能完善规划

> 规划时间：2026-02-19

---

## 当前状态分析

### 已实现功能 ✅

1. **基础对话功能**
   - AI对话接口 (`POST /api/ai/chat`)
   - 会话历史管理（内存存储）
   - 清除历史功能 (`DELETE /api/ai/history`)
   - 会话信息查询 (`GET /api/ai/session`)

2. **AI工具集成**
   - 院校推荐工具 (`analyzeTargetUniversity`)
   - 分数线查询工具 (`getScoreLine`)
   - 考研资讯工具 (`getGraduationNews`)
   - 用户目标查询工具 (`getUserTarget`)

3. **前端界面**
   - 聊天界面
   - 快捷问题
   - 消息历史显示
   - 加载动画

### 存在问题 ⚠️

1. **会话持久化缺失**
   - 当前使用内存存储，服务重启后会话丢失
   - 无法跨设备同步会话历史

2. **AI工具未实际调用**
   - GradMateTools 定义了工具，但未集成到 Agent
   - AI无法主动调用数据库查询工具

3. **用户体验不足**
   - 缺少打字机效果
   - 缺少消息时间戳
   - 缺少消息状态（发送中、已读等）
   - 缺少错误重试机制

4. **功能单一**
   - 缺少语音输入
   - 缺少图片识别
   - 缺少文件上传（如成绩单分析）
   - 缺少个性化推荐

---

## 完善方案

### 阶段1：核心功能增强（优先级：高）

#### 1.1 会话持久化

**目标**：将会话历史保存到数据库，支持跨设备同步

**实现方案**：

1. 创建会话表
```sql
CREATE TABLE chat_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    title VARCHAR(200) COMMENT '会话标题',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_session_id (session_id)
) COMMENT='AI聊天会话表';

CREATE TABLE chat_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: user/assistant',
    content TEXT NOT NULL COMMENT '消息内容',
    tokens INT COMMENT 'Token数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id)
) COMMENT='AI聊天消息表';
```

2. 创建 ChatSessionService 和 ChatMessageService
3. 修改 GradMateAgentService 使用数据库存储

#### 1.2 集成 LangChain4j Agent

**目标**：让AI能够主动调用工具查询数据

**实现方案**：

```java
@Service
public class GradMateAgentService {
    
    @Autowired
    private GradMateTools tools;
    
    // 创建AI Agent
    private Assistant createAssistant() {
        return AiServices.builder(Assistant.class)
            .chatLanguageModel(chatModel)
            .chatMemory(chatMemory)
            .tools(tools)  // 注入工具
            .build();
    }
    
    public String chat(Long userId, String message) {
        Assistant assistant = createAssistant();
        return assistant.chat(message);
    }
}
```

**优势**：
- AI可以根据用户问题自动选择合适的工具
- 支持多轮对话和上下文理解
- 工具调用结果自动整合到回复中

#### 1.3 优化前端体验

**打字机效果**：
```javascript
// 逐字显示AI回复
function typeWriter(text, callback) {
    let index = 0;
    const interval = setInterval(() => {
        if (index < text.length) {
            callback(text.substring(0, index + 1));
            index++;
        } else {
            clearInterval(interval);
        }
    }, 50);
}
```

**消息时间戳**：
```vue
<view class="message-time">{{ formatTime(msg.createTime) }}</view>
```

**错误重试**：
```javascript
async sendMessage(retryCount = 0) {
    try {
        // 发送消息
    } catch (error) {
        if (retryCount < 3) {
            await this.sendMessage(retryCount + 1);
        } else {
            // 显示重试按钮
        }
    }
}
```

---

### 阶段2：智能推荐功能（优先级：中）

#### 2.1 个性化院校推荐

**基于用户画像推荐**：

```java
@Tool("根据用户的详细信息智能推荐院校")
public String smartRecommendSchools(
    @P("用户ID") Long userId,
    @P("本科院校层次") String schoolLevel,
    @P("本科专业") String major,
    @P("成绩水平") String scoreLevel
) {
    // 1. 获取用户目标
    User user = userMapper.selectById(userId);
    
    // 2. 构建推荐算法
    // - 985背景 → 推荐985/211
    // - 211背景 → 推荐211/双一流
    // - 普通本科 → 推荐双一流/普通院校
    
    // 3. 考虑专业匹配度
    // 4. 考虑地理位置偏好
    // 5. 考虑历年录取数据
    
    return "推荐结果...";
}
```

#### 2.2 学习计划生成

**根据考试时间生成复习计划**：

```java
@Tool("为用户生成个性化的考研复习计划")
public String generateStudyPlan(
    @P("用户ID") Long userId,
    @P("距离考试天数") int daysToExam,
    @P("薄弱科目") String weakSubjects
) {
    // 1. 计算时间分配
    // 2. 生成每日任务
    // 3. 设置里程碑
    // 4. 提供复习建议
    
    return "学习计划...";
}
```

#### 2.3 情绪疏导功能

**识别用户情绪并提供支持**：

```java
// 在系统提示词中添加情绪识别能力
private String systemPrompt = """
    你是研小伴，一个温暖、专业的考研AI助手。
    
    当用户表达焦虑、压力、迷茫等负面情绪时：
    1. 首先表示理解和共情
    2. 提供积极的心理疏导
    3. 给出具体的应对建议
    4. 鼓励用户坚持下去
    
    示例：
    用户："我感觉压力好大，不知道能不能考上..."
    回复："我理解你现在的压力，考研确实是一段艰难的旅程。但请记住，
    每个成功上岸的学长学姐都经历过这样的时刻。压力是正常的，它说明
    你在认真对待这件事。建议你：1) 制定合理的学习计划，避免过度焦虑
    2) 适当运动放松 3) 和朋友倾诉。你已经走到这里了，相信自己！"
    """;
```

---

### 阶段3：高级功能扩展（优先级：低）

#### 3.1 多模态输入

**语音输入**：
```javascript
// 使用uni-app的录音API
uni.getRecorderManager().start({
    duration: 60000,
    format: 'mp3'
});

// 调用语音识别API转文字
```

**图片识别**：
```javascript
// 上传成绩单、笔记等图片
// 使用OCR识别文字
// AI分析并给出建议
```

#### 3.2 知识库问答

**构建考研知识库**：

1. 收集常见问题和答案
2. 使用向量数据库存储（如Milvus）
3. 实现RAG（检索增强生成）

```java
@Tool("从知识库中检索相关信息")
public String searchKnowledgeBase(@P("查询问题") String query) {
    // 1. 向量化查询
    // 2. 检索相似文档
    // 3. 返回相关内容
    return "知识库内容...";
}
```

#### 3.3 学习数据分析

**分析用户学习数据**：

```java
@Tool("分析用户的学习数据并给出建议")
public String analyzeStudyData(@P("用户ID") Long userId) {
    // 1. 获取学习记录
    // 2. 分析学习时长、效率
    // 3. 识别薄弱环节
    // 4. 提供改进建议
    
    return "学习分析报告...";
}
```

---

## 技术选型

### 后端技术

1. **AI框架**：LangChain4j
   - 支持工具调用
   - 支持多种LLM
   - 内置记忆管理

2. **LLM服务**：火山引擎豆包
   - 已集成
   - 性能稳定
   - 成本可控

3. **数据库**：MySQL 8.0
   - 存储会话历史
   - 存储用户画像
   - 存储知识库

4. **缓存**：Redis（可选）
   - 缓存热门问题
   - 缓存用户会话
   - 提升响应速度

### 前端技术

1. **UI组件**：uni-app原生组件
2. **动画效果**：CSS3 + Vue transition
3. **状态管理**：Vue3 Composition API
4. **网络请求**：封装的request.js

---

## 开发计划

### 第1周：核心功能增强

- [ ] Day 1-2: 创建会话表和消息表
- [ ] Day 3-4: 实现会话持久化
- [ ] Day 5-6: 集成LangChain4j Agent
- [ ] Day 7: 测试工具调用功能

### 第2周：用户体验优化

- [ ] Day 1-2: 实现打字机效果
- [ ] Day 3-4: 添加消息时间戳和状态
- [ ] Day 5-6: 实现错误重试机制
- [ ] Day 7: 优化UI样式

### 第3周：智能推荐功能

- [ ] Day 1-3: 开发个性化推荐算法
- [ ] Day 4-5: 实现学习计划生成
- [ ] Day 6-7: 完善情绪疏导功能

### 第4周：测试和优化

- [ ] Day 1-3: 全面测试
- [ ] Day 4-5: 性能优化
- [ ] Day 6-7: 文档编写

---

## 预期效果

### 用户体验提升

1. **更智能**：AI能主动查询数据，给出精准建议
2. **更贴心**：理解用户情绪，提供心理支持
3. **更实用**：个性化推荐，生成学习计划
4. **更稳定**：会话持久化，不丢失历史

### 技术指标

1. **响应时间**：< 3秒
2. **准确率**：> 85%
3. **用户满意度**：> 4.5/5
4. **日活跃用户**：目标1000+

---

## 风险评估

### 技术风险

1. **AI成本**：大量对话可能产生高额费用
   - 解决方案：设置每日调用限额，优化提示词

2. **数据安全**：用户对话涉及隐私
   - 解决方案：加密存储，定期清理

3. **性能瓶颈**：高并发时响应慢
   - 解决方案：使用Redis缓存，异步处理

### 业务风险

1. **用户期望过高**：AI无法解决所有问题
   - 解决方案：明确AI能力边界，提供人工客服

2. **内容质量**：AI可能给出错误建议
   - 解决方案：添加免责声明，人工审核

---

## 总结

研小伴功能的完善将分三个阶段进行：

1. **阶段1（核心）**：会话持久化 + Agent集成 + 体验优化
2. **阶段2（智能）**：个性化推荐 + 学习计划 + 情绪疏导
3. **阶段3（高级）**：多模态输入 + 知识库 + 数据分析

建议优先完成阶段1，确保核心功能稳定可用，再逐步扩展高级功能。
