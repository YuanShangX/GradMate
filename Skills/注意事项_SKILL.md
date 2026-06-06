我有一个已存在的数据库叫 gradmate，里面有以下这些表（以下表禁止删除）：
major（主表，大约32万多行，存放详细的研究生招生专业信息）
school（大约875行，存放高校基本信息）
school_adjust（大约1919行，招生调剂/调整公告）
school_brochure（大约1888行，招生简章/专业目录）
school_information（大约1673行，其他招生通知/信息）
school_character（学校特性标记表）

MySQL数据库连接信息：
spring.datasource.url=jdbc:mysql://localhost:3306/gradmate
spring.datasource.username=root
spring.datasource.password=A123456

Java版本：jdk17;
前后端分离项目：
后端：SpringBoot+MybatisPlus+SQL;
前端：Uniapp+Vue3;
不涉及到密码加密解密；

AI开发注意事项：
> **【技术栈与基座模型】**
> - 框架：SpringBoot + Java 17
> - AI 框架：LangChain4j（请使用最新稳定版）
> - 火山引擎 API Key 存储在环境变量ARK_API_KEY中
> - 接入模型：火山引擎 `doubao-1-5-pro-32k-250115`。请预留火山引擎的接入点 ID (`endpoint-id`) 配置项，不要在代码里硬编码模型名称。
> 
> **【业务背景】**
> 我正在开发一个名为 GradMate 的微信小程序后端，这是一个考研信息与学习伴侣平台。我需要你帮我写一个 `GradMateAgentService` 的完整逻辑代码。
> 
> **【核心功能需求】**
> 1. **基础对话与上下文：** 接收前端传来的 `userId` 和 `userMessage`。必须使用 LangChain4j 的 `ChatMemory`（如滑动窗口策略）为每个用户维护独立的会话上下文，保留多轮对话能力。
> 2. **智能工具调用 (Function Calling)：** 请利用 `@Tool` 注解帮我定义以下几个外部工具方法的模拟占位符，让 AI 在需要时自动调用：
>    - `analyzeTargetUniversity(String profile)`: 根据用户的本科背景和意向分析目标院校。
>    - `getScoreLine(String university, String major)`: 获取目标院校专业的分数线。
>    - `getGraduationNews()`: 获取最新的考研资讯。
> 3. **课业辅导能力：** AI 需要具备讲解考研题目的能力（比如当用户询问高等数学的极限、单调性，或者 MySQL 的 InnoDB 锁机制、事务隔离级别时，能够进行专业解答）。
> 4. **情绪疏导能力：** AI 需要具备极高的共情能力，在检测到用户处于“emo”、焦虑或压力大的状态时，能够主动给予鼓励和心理安抚。
> 
> **【输出要求】**
> 请提供：1. 完整的 pom.xml 依赖；2. application.yml 配置模板；3. 包含 Tools 和 ChatMemory 配置的完整 Java 代码实现。请添加详细的中文注释。


火山引擎官方案例：
curl https://ark.cn-beijing.volces.com/api/v3/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ARK_API_KEY" \
  -d '{
    "model": "doubao-1-5-pro-32k-250115",
    "messages": [
      {"role": "system","content": "你是人工智能助手."},
      {"role": "user","content": "你好"}
    ]
  }'


  <dependency>
  <groupId>com.volcengine</groupId>
  <artifactId>volcengine-java-sdk-ark-runtime</artifactId>
  <version>LATEST</version>
</dependency>

package com.volcengine.ark.runtime;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// 请确保您已将 API Key 存储在环境变量 ARK_API_KEY 中
// 初始化Ark客户端，从环境变量中读取您的API Key
public class ChatCompletionsExample {
    // 从环境变量中获取您的 API Key。此为默认方式，您可根据需要进行修改
    static String apiKey = System.getenv("ARK_API_KEY");
    // 此为默认路径，您可根据业务所在地域进行配置
    static String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
    static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
    static Dispatcher dispatcher = new Dispatcher();
    static ArkService service = ArkService.builder().dispatcher(dispatcher).connectionPool(connectionPool).baseUrl(baseUrl).apiKey(apiKey).build();

    public static void main(String[] args) {
        System.out.println("\n----- standard request -----");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是人工智能助手.").build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("你好").build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                // 指定您创建的方舟推理接入点 ID，此处已帮您修改为您的推理接入点 ID
                .model("doubao-1-5-pro-32k-250115")
                .messages(messages)
                .build();

        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));

        System.out.println("\n----- streaming request -----");
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是人工智能助手.").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("你好").build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);

        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                // 指定您创建的方舟推理接入点 ID，此处已帮您修改为您的推理接入点 ID
                .model("doubao-1-5-pro-32k-250115")
                .messages(messages)
                .build();

        service.streamChatCompletion(streamChatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(
                        choice -> {
                            if (choice.getChoices().size() > 0) {
                                System.out.print(choice.getChoices().get(0).getMessage().getContent());
                            }
                        }
                );

        service.shutdownExecutor();
    }

}

