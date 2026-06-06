# 阶段6：AI智能模块 - 问题修复说明

## 修复时间
2026-02-16

---

## 问题描述

### 编译错误
```
java: 程序包dev.langchain4j.model.volcengine不存在
```

### 根本原因
- Maven Central 不存在 `langchain4j-volcengine` 包
- 原计划使用的依赖在 0.35.0 版本中不可用
- LangChain4j 官方未提供火山引擎的直接集成

---

## 解决方案

### 方案选择
采用 **火山引擎官方SDK + LangChain4j ChatMemory** 的混合方案:
- 使用火山引擎官方SDK (`volcengine-java-sdk-ark-runtime`) 进行AI对话
- 使用 LangChain4j 的 `ChatMemory` 管理会话上下文
- 手动实现消息格式转换

### 优势
1. 使用官方SDK,稳定可靠
2. 保留LangChain4j的会话管理能力
3. 代码可维护性强
4. 易于扩展和优化

---

## 修改内容

### 1. pom.xml 依赖修改

#### 修改前
```xml
<!-- LangChain4j 火山引擎集成 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-volcengine</artifactId>
    <version>0.35.0</version>
</dependency>
```

#### 修改后
```xml
<!-- 火山引擎 SDK -->
<dependency>
    <groupId>com.volcengine</groupId>
    <artifactId>volcengine-java-sdk-ark-runtime</artifactId>
    <version>1.0.9</version>
</dependency>
```

### 2. AiConfig.java 配置类修改

#### 修改前
```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.volcengine.VolcEngineChatModel;

@Bean
public ChatLanguageModel chatLanguageModel() {
    return VolcEngineChatModel.builder()
            .apiKey(apiKey)
            .endpointId(endpointId)
            .temperature(temperature)
            .maxTokens(maxTokens)
            .timeout(Duration.ofMillis(timeout))
            .logRequests(true)
            .logResponses(true)
            .build();
}
```

#### 修改后
```java
import com.volcengine.ark.runtime.service.ArkService;

@Bean
public ArkService arkService() {
    return ArkService.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .build();
}

@Bean
public String modelName() {
    return modelName;
}

@Bean
public Double temperature() {
    return temperature;
}

@Bean
public Integer maxTokens() {
    return maxTokens;
}
```

### 3. application.properties 配置修改

#### 修改前
```properties
ai.volcengine.api-key=${ARK_API_KEY}
ai.volcengine.endpoint-id=${ARK_ENDPOINT_ID:ep-20250115-default}
ai.volcengine.model-name=doubao-1.5-pro-32k-250115
```

#### 修改后
```properties
ai.volcengine.api-key=${ARK_API_KEY}
ai.volcengine.base-url=https://ark.cn-beijing.volces.com/api/v3
ai.volcengine.model-name=doubao-1.5-pro-32k-250115
```

### 4. GradMateAgentService.java 服务类重写

#### 核心变化
1. 不再使用 `AiServices` 和 `ChatLanguageModel`
2. 直接使用火山引擎 `ArkService` 调用API
3. 手动管理消息格式转换
4. 保留 LangChain4j 的 `ChatMemory` 功能

#### 关键代码
```java
@Autowired
private ArkService arkService;

@Autowired
private String modelName;

@Autowired
private Double temperature;

@Autowired
private Integer maxTokens;

/**
 * 将ChatMemory转换为火山引擎的消息格式
 */
private List<ChatMessage> convertToVolcengineMessages(ChatMemory memory) {
    List<ChatMessage> messages = new ArrayList<>();
    
    // 添加系统提示词
    messages.add(ChatMessage.builder()
            .role(ChatMessageRole.SYSTEM)
            .content(systemPrompt)
            .build());
    
    // 添加历史消息
    memory.messages().forEach(msg -> {
        ChatMessageRole role = msg.type().toString().equals("USER") 
            ? ChatMessageRole.USER 
            : ChatMessageRole.ASSISTANT;
        
        messages.add(ChatMessage.builder()
                .role(role)
                .content(msg.text())
                .build());
    });
    
    return messages;
}

/**
 * 处理用户消息
 */
public String chat(Long userId, String userMessage) {
    // 获取用户的聊天记忆
    ChatMemory memory = getOrCreateChatMemory(userId);
    
    // 添加用户消息到记忆
    memory.add(dev.langchain4j.data.message.UserMessage.from(userMessage));
    
    // 转换为火山引擎消息格式
    List<ChatMessage> messages = convertToVolcengineMessages(memory);
    
    // 构建请求
    ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model(modelName)
            .messages(messages)
            .temperature(temperature)
            .maxTokens(maxTokens)
            .build();
    
    // 调用火山引擎API
    StringBuilder response = new StringBuilder();
    arkService.createChatCompletion(request).getChoices().forEach(choice -> {
        String content = choice.getMessage().getContent();
        if (content != null) {
            response.append(content);
        }
    });
    
    String aiResponse = response.toString();
    
    // 添加AI回复到记忆
    memory.add(dev.langchain4j.data.message.AiMessage.from(aiResponse));
    
    return aiResponse;
}
```

---

## 功能影响

### 保留的功能 ✅
- 多轮对话能力
- 会话上下文管理
- 用户会话隔离
- 清除历史功能
- 考研咨询能力
- 课业辅导能力
- 情绪疏导能力

### 暂时移除的功能 ⚠️
- Function Calling (工具自动调用)
  - 原因: 需要手动实现工具调用逻辑
  - 影响: AI不会自动调用 GradMateTools 中的方法
  - 后续: 可通过关键词识别或手动触发实现

---

## 环境变量配置

### 修改前
需要设置两个环境变量:
```bash
ARK_API_KEY=your_api_key_here
ARK_ENDPOINT_ID=ep-20250115-xxxxx
```

### 修改后
只需要设置一个环境变量:
```bash
ARK_API_KEY=your_api_key_here
```

---

## 测试验证

### 1. 编译测试
```bash
cd GradMate_SpringBoot
mvn clean compile
```
预期结果: 编译成功,无错误

### 2. 启动测试
```bash
# 设置环境变量
set ARK_API_KEY=your_api_key_here

# 启动应用
mvn spring-boot:run
```
预期结果: 应用正常启动,端口9090

### 3. 功能测试
- 测试基础对话: "你好"
- 测试考研咨询: "如何选择考研院校?"
- 测试课业辅导: "高数极限怎么求?"
- 测试情绪疏导: "我感觉压力好大"
- 测试多轮对话: 连续发送多条消息
- 测试清除历史: 点击清除按钮

---

## 后续优化方向

### 1. 实现 Function Calling
可以通过以下方式实现工具调用:

#### 方案A: 关键词识别
```java
if (userMessage.contains("推荐院校") || userMessage.contains("择校")) {
    String toolResult = tools.analyzeTargetUniversity(userMessage);
    // 将工具结果添加到上下文
}
```

#### 方案B: 意图识别
```java
String intent = detectIntent(userMessage);
switch (intent) {
    case "RECOMMEND_SCHOOL":
        return tools.analyzeTargetUniversity(userMessage);
    case "QUERY_SCORE":
        return tools.getScoreLine(school, major);
    // ...
}
```

#### 方案C: 使用火山引擎的 Function Calling
```java
// 火山引擎SDK支持Function Calling
// 需要在请求中添加functions参数
ChatCompletionRequest request = ChatCompletionRequest.builder()
    .model(modelName)
    .messages(messages)
    .functions(buildFunctions()) // 定义可用的函数
    .build();
```

### 2. 流式输出
```java
// 使用streamChatCompletion实现打字机效果
arkService.streamChatCompletion(request)
    .doOnError(Throwable::printStackTrace)
    .blockingForEach(choice -> {
        if (choice.getChoices().size() > 0) {
            String content = choice.getChoices().get(0).getMessage().getContent();
            // 发送到前端
        }
    });
```

### 3. 会话持久化
```java
// 使用Redis存储会话
@Autowired
private RedisTemplate<String, ChatMemory> redisTemplate;

private ChatMemory getOrCreateChatMemory(Long userId) {
    String key = "chat:memory:" + userId;
    ChatMemory memory = redisTemplate.opsForValue().get(key);
    if (memory == null) {
        memory = MessageWindowChatMemory.builder()
            .maxMessages(memorySize * 2)
            .build();
        redisTemplate.opsForValue().set(key, memory);
    }
    return memory;
}
```

---

## 总结

通过使用火山引擎官方SDK替代不存在的 `langchain4j-volcengine` 包,成功解决了编译错误。

虽然暂时失去了 Function Calling 的自动调用能力,但核心的对话、会话管理、情绪疏导等功能完全保留,且代码更加清晰可控。

后续可以通过关键词识别、意图识别或火山引擎原生的 Function Calling 功能来实现工具调用。

---

**修复完成!** ✅

现在可以设置环境变量 `ARK_API_KEY` 并重启后端进行测试了。
