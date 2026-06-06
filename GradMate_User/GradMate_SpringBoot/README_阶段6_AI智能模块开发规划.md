# 阶段6：AI智能模块 - 开发规划

## 开发时间
2026-02-14

---

## 技术栈

### AI框架
- **LangChain4j** - 最新稳定版
- **火山引擎豆包模型** - `doubao-1.5-pro-32k-250115`
- **API Key** - 存储在环境变量 `ARK_API_KEY` 中

### 核心能力
1. **多轮对话** - 使用ChatMemory维护会话上下文
2. **工具调用** - Function Calling能力
3. **课业辅导** - 考研题目讲解
4. **情绪疏导** - 共情能力和心理安抚

---

## 模块目标

实现GradMate AI智囊功能:
- 考研问答(政策、备考、择校)
- 课业辅导(高数、英语、专业课)
- 情绪疏导(压力管理、心理支持)
- 智能推荐(院校、专业、资讯)

---

## 依赖配置

### pom.xml 新增依赖

```xml
<!-- LangChain4j 核心依赖 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.35.0</version>
</dependency>

<!-- LangChain4j 火山引擎集成 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-volcengine</artifactId>
    <version>0.35.0</version>
</dependency>

<!-- LangChain4j 内存存储 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
    <version>0.35.0</version>
</dependency>
```

---

## 配置文件

### application.yml 新增配置

```yaml
# AI配置
ai:
  volcengine:
    api-key: ${ARK_API_KEY}  # 从环境变量读取
    endpoint-id: ${ARK_ENDPOINT_ID:ep-20250115-xxxxx}  # 接入点ID
    model-name: doubao-1.5-pro-32k-250115
    temperature: 0.7
    max-tokens: 2000
    timeout: 60000  # 60秒超时
  
  # 会话配置
  chat:
    memory-size: 10  # 保留最近10轮对话
    system-prompt: |
      你是GradMate考研助手,一个专业、温暖、有共情能力的AI伙伴。
      
      你的核心能力:
      1. 考研咨询: 解答考研政策、备考策略、择校建议
      2. 课业辅导: 讲解高数、英语、专业课题目
      3. 情绪疏导: 识别用户情绪,给予鼓励和心理支持
      4. 信息推荐: 基于用户目标推荐院校、专业、资讯
      
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

---

## 后端开发任务

### 1. 配置类

#### AiConfig.java
```java
@Configuration
public class AiConfig {
    
    @Value("${ai.volcengine.api-key}")
    private String apiKey;
    
    @Value("${ai.volcengine.endpoint-id}")
    private String endpointId;
    
    @Value("${ai.volcengine.temperature}")
    private Double temperature;
    
    @Value("${ai.volcengine.max-tokens}")
    private Integer maxTokens;
    
    @Value("${ai.chat.memory-size}")
    private Integer memorySize;
    
    @Value("${ai.chat.system-prompt}")
    private String systemPrompt;
    
    /**
     * 配置火山引擎聊天模型
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return VolcEngineChatModel.builder()
            .apiKey(apiKey)
            .endpointId(endpointId)
            .temperature(temperature)
            .maxTokens(maxTokens)
            .build();
    }
    
    /**
     * 配置聊天记忆存储(每个用户独立)
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
            .maxMessages(memorySize)
            .build();
    }
}
```

### 2. 工具类(Tools)

#### GradMateTools.java
```java
@Component
public class GradMateTools {
    
    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private MajorService majorService;
    
    @Autowired
    private InfoService infoService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 分析目标院校
     * AI会在需要时自动调用此方法
     */
    @Tool("根据用户的本科背景和意向分析推荐目标院校")
    public String analyzeTargetUniversity(
        @P("用户本科背景描述,如学校层次、专业、成绩等") String profile
    ) {
        // 实现院校推荐逻辑
        // 1. 解析用户背景
        // 2. 查询匹配的院校
        // 3. 返回推荐结果
        
        return "基于您的背景,推荐以下院校: ...";
    }
    
    /**
     * 获取分数线
     */
    @Tool("获取指定院校专业的历年分数线")
    public String getScoreLine(
        @P("院校名称") String university,
        @P("专业名称") String major
    ) {
        // 实现分数线查询逻辑
        // 1. 查询major表
        // 2. 返回分数线信息
        
        return "该院校专业近三年分数线: ...";
    }
    
    /**
     * 获取考研资讯
     */
    @Tool("获取最新的考研资讯和政策")
    public String getGraduationNews() {
        // 实现资讯查询逻辑
        // 1. 查询最新的简章、调剂、通知
        // 2. 返回资讯摘要
        
        return "最新考研资讯: ...";
    }
    
    /**
     * 查询用户目标
     */
    @Tool("获取用户设定的目标院校和专业")
    public String getUserTarget(@P("用户ID") Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return String.format("用户目标院校: %s, 目标专业: %s", 
                user.getTargetSchool(), user.getTargetMajor());
        }
        return "用户尚未设定目标";
    }
}
```

### 3. AI服务类

#### GradMateAgentService.java
```java
@Service
public class GradMateAgentService {
    
    @Autowired
    private ChatLanguageModel chatModel;
    
    @Autowired
    private ChatMemoryProvider memoryProvider;
    
    @Autowired
    private GradMateTools tools;
    
    @Value("${ai.chat.system-prompt}")
    private String systemPrompt;
    
    // 存储每个用户的AI助手实例
    private final Map<Long, AiServices> userAgents = new ConcurrentHashMap<>();
    
    /**
     * 获取或创建用户的AI助手
     */
    private AiServices getOrCreateAgent(Long userId) {
        return userAgents.computeIfAbsent(userId, id -> {
            return AiServices.builder(AiServices.class)
                .chatLanguageModel(chatModel)
                .chatMemory(memoryProvider.get(String.valueOf(id)))
                .tools(tools)
                .build();
        });
    }
    
    /**
     * 处理用户消息
     */
    public String chat(Long userId, String userMessage) {
        try {
            // 获取用户的AI助手
            AiServices agent = getOrCreateAgent(userId);
            
            // 发送消息并获取回复
            String response = agent.chat(userMessage);
            
            return response;
        } catch (Exception e) {
            log.error("AI对话失败: userId={}, error={}", userId, e.getMessage());
            return "抱歉,我现在有点累了,请稍后再试~";
        }
    }
    
    /**
     * 清除用户会话历史
     */
    public void clearHistory(Long userId) {
        userAgents.remove(userId);
    }
}
```

### 4. AI控制器

#### AiController.java
```java
@RestController
@RequestMapping("/api/ai")
public class AiController {
    
    @Autowired
    private GradMateAgentService agentService;
    
    /**
     * AI对话接口
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String message = params.get("message").toString();
            
            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息不能为空");
            }
            
            String response = agentService.chat(userId, message);
            return Result.success(response);
            
        } catch (Exception e) {
            return Result.error("对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除会话历史
     */
    @DeleteMapping("/history")
    public Result<String> clearHistory(@RequestParam Long userId) {
        try {
            agentService.clearHistory(userId);
            return Result.success("会话历史已清除");
        } catch (Exception e) {
            return Result.error("清除失败: " + e.getMessage());
        }
    }
}
```

---

## 前端开发任务

### 1. AI聊天页面

#### pages/ai/chat.vue

**页面结构**:
```
- 顶部导航栏
- 消息列表区域
  - AI消息(左侧,灰色气泡)
  - 用户消息(右侧,蓝色气泡)
  - 加载动画(AI思考中)
- 底部输入栏
  - 输入框
  - 发送按钮
  - 快捷问题按钮
```

**快捷问题**:
- "如何选择考研院校?"
- "考研英语怎么复习?"
- "高数极限怎么求?"
- "我感觉压力好大..."

**交互逻辑**:
- 输入消息 → 发送 → 显示用户消息 → 显示加载动画 → 调用API → 显示AI回复
- 支持打字机效果(逐字显示)
- 支持复制消息
- 支持清除历史

### 2. 首页AI入口

在首页添加AI助手入口卡片:
```vue
<view class="ai-card" @click="goToAi">
  <view class="ai-icon">🤖</view>
  <view class="ai-info">
    <text class="ai-title">AI智囊</text>
    <text class="ai-desc">考研问答 · 课业辅导 · 情绪疏导</text>
  </view>
</view>
```

---

## 工具方法实现细节

### 1. analyzeTargetUniversity 实现

```java
public String analyzeTargetUniversity(String profile) {
    // 1. 解析用户背景(可以用简单的关键词匹配)
    boolean is985 = profile.contains("985");
    boolean is211 = profile.contains("211");
    
    // 2. 查询匹配的院校
    QueryWrapper<School> wrapper = new QueryWrapper<>();
    if (!is985 && !is211) {
        // 普通本科,推荐211或普通一本
        wrapper.eq("is_211", 1).or().eq("subordinate", "省属");
    }
    
    List<School> schools = schoolMapper.selectList(wrapper);
    
    // 3. 返回推荐结果
    StringBuilder result = new StringBuilder("基于您的背景,推荐以下院校:\n");
    schools.stream().limit(5).forEach(school -> {
        result.append("- ").append(school.getSchoolName())
              .append(" (").append(school.getProvince()).append(")\n");
    });
    
    return result.toString();
}
```

### 2. getScoreLine 实现

```java
public String getScoreLine(String university, String major) {
    // 查询专业信息
    QueryWrapper<Major> wrapper = new QueryWrapper<>();
    wrapper.like("school_name", university)
           .like("major_name", major)
           .orderByDesc("year")
           .last("LIMIT 3");
    
    List<Major> majors = majorMapper.selectList(wrapper);
    
    if (majors.isEmpty()) {
        return "未找到该院校专业的分数线信息";
    }
    
    StringBuilder result = new StringBuilder();
    result.append(university).append(" - ").append(major).append(" 近年分数线:\n");
    
    for (Major m : majors) {
        result.append(m.getYear()).append("年: ")
              .append("总分 ").append(m.getScore() != null ? m.getScore() : "未公布")
              .append("\n");
    }
    
    return result.toString();
}
```

### 3. getGraduationNews 实现

```java
public String getGraduationNews() {
    // 查询最新资讯
    List<SchoolBrochure> brochures = brochureMapper.selectList(
        new QueryWrapper<SchoolBrochure>()
            .orderByDesc("publish_date")
            .last("LIMIT 3")
    );
    
    StringBuilder result = new StringBuilder("最新考研资讯:\n\n");
    
    for (SchoolBrochure b : brochures) {
        result.append("📢 ").append(b.getTitle()).append("\n")
              .append("   发布时间: ").append(b.getPublishDate()).append("\n\n");
    }
    
    return result.toString();
}
```

---

## 情绪识别与疏导

### 情绪关键词检测

在System Prompt中添加情绪识别指令:

```
当用户消息包含以下关键词时,优先给予情感支持:
- 焦虑相关: "焦虑"、"紧张"、"害怕"、"担心"
- 压力相关: "压力大"、"累"、"疲惫"、"坚持不下去"
- 情绪低落: "emo"、"难过"、"想放弃"、"没信心"

回复模板:
1. 共情理解: "我理解你现在的感受..."
2. 情感支持: "考研确实不容易,但你已经很棒了..."
3. 具体建议: "不妨试试这些方法..."
4. 鼓励打气: "相信自己,你一定可以的!"
```

---

## 开发流程

### 第一步: 环境准备
1. 添加LangChain4j依赖到pom.xml
2. 配置application.yml
3. 设置环境变量ARK_API_KEY

### 第二步: 后端开发
1. 创建AiConfig配置类
2. 创建GradMateTools工具类
3. 实现工具方法(院校推荐、分数线查询、资讯获取)
4. 创建GradMateAgentService服务类
5. 创建AiController控制器
6. 测试API接口

### 第三步: 前端开发
1. 创建AI聊天页面
2. 实现消息列表和输入框
3. 添加快捷问题按钮
4. 实现打字机效果
5. 在首页添加AI入口

### 第四步: 测试优化
1. 测试多轮对话
2. 测试工具调用
3. 测试情绪疏导
4. 优化响应速度

---

## 验收标准

### 功能验收
- [ ] AI能正确回答考研相关问题
- [ ] 多轮对话上下文保持正确
- [ ] 工具调用功能正常(院校推荐、分数线查询)
- [ ] 情绪疏导能力有效
- [ ] 课业辅导回答专业

### 性能验收
- [ ] 响应时间 < 5秒
- [ ] 会话历史正确保存
- [ ] 并发用户隔离正确

### 用户体验验收
- [ ] 回答准确专业
- [ ] 语气温暖友好
- [ ] 界面简洁易用

---

## 时间估算

- 环境配置: 0.5天
- 后端开发: 2天
- 前端开发: 1.5天
- 测试优化: 1天

**总计**: 约5天

---

**开始开发!** 🤖
