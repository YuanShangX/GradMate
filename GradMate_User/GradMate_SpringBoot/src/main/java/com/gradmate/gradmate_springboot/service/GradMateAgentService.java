package com.gradmate.gradmate_springboot.service;

import com.gradmate.gradmate_springboot.entity.ChatSession;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * GradMate AI助手服务
 * 处理用户与AI的对话交互
 */
@Slf4j
@Service
public class GradMateAgentService {
    
    @Autowired
    private ArkService arkService;
    
    @Autowired
    private GradMateTools tools;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private String modelName;
    
    @Autowired
    private Double temperature;
    
    @Autowired
    private Integer maxTokens;
    
    @Value("${ai.chat.system-prompt}")
    private String systemPrompt;
    
    /**
     * 处理用户消息
     * 
     * @param userId 用户ID
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String chat(Long userId, String userMessage) {
        return chat(userId, null, userMessage);
    }
    
    /**
     * 处理用户消息（支持指定会话）
     * 
     * @param userId 用户ID
     * @param sessionId 会话ID（可选，为null时自动获取或创建）
     * @param userMessage 用户消息
     * @return AI回复
     */
    public String chat(Long userId, String sessionId, String userMessage) {
        try {
            log.info("AI对话开始: userId={}, sessionId={}, message={}", userId, sessionId, userMessage);
            
            // 获取或创建会话
            ChatSession session;
            if (sessionId != null && !sessionId.isEmpty()) {
                session = chatService.getSessionById(sessionId);
                if (session == null) {
                    throw new RuntimeException("会话不存在: " + sessionId);
                }
            } else {
                session = chatService.getOrCreateSession(userId);
            }
            sessionId = session.getSessionId();
            
            // 保存用户消息
            chatService.saveMessage(sessionId, userId, "user", userMessage);
            
            // 检测意图并调用工具
            String enhancedMessage = detectIntentAndCallTools(userId, userMessage);
            
            // 获取历史消息
            List<com.gradmate.gradmate_springboot.entity.ChatMessage> history = chatService.getSessionMessages(sessionId, 10);
            
            // 构建消息列表（使用增强后的消息）
            List<ChatMessage> messages = buildMessages(history, enhancedMessage);
            
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
                Object contentObj = choice.getMessage().getContent();
                if (contentObj != null) {
                    response.append(contentObj.toString());
                }
            });
            
            String aiResponse = response.toString();
            
            // 保存AI回复
            chatService.saveMessage(sessionId, userId, "assistant", aiResponse);
            
            log.info("AI对话完成: userId={}, responseLength={}", userId, aiResponse.length());
            
            return aiResponse;
            
        } catch (Exception e) {
            log.error("AI对话失败: userId={}, error={}", userId, e.getMessage(), e);
            return "抱歉,我现在有点累了,请稍后再试~ 如果问题持续,请联系管理员。";
        }
    }
    
    /**
     * 检测用户意图并调用相应的工具
     * 
     * @param userId 用户ID
     * @param userMessage 用户消息
     * @return 增强后的消息（包含工具调用结果）
     */
    private String detectIntentAndCallTools(Long userId, String userMessage) {
        String message = userMessage.toLowerCase();
        StringBuilder toolResults = new StringBuilder();
        
        // 意图1: 院校推荐
        if (containsAny(message, "推荐院校", "推荐学校", "推荐大学", "择校", "选择院校", "选择学校", 
                "哪些院校", "哪些学校", "哪些大学", "什么院校", "什么学校")) {
            log.info("检测到院校推荐意图: userId={}", userId);
            try {
                String result = tools.analyzeTargetUniversity(userMessage);
                toolResults.append("\n\n[系统查询结果 - 院校推荐]\n").append(result);
            } catch (Exception e) {
                log.error("调用院校推荐工具失败: {}", e.getMessage());
            }
        }
        
        // 意图2: 分数线查询
        if (containsAny(message, "分数线", "录取分数", "复试分数", "初试分数", "国家线", "院校线")) {
            log.info("检测到分数线查询意图: userId={}", userId);
            try {
                // 尝试提取院校和专业名称
                String[] extracted = extractSchoolAndMajor(userMessage);
                if (extracted[0] != null && extracted[1] != null) {
                    String result = tools.getScoreLine(extracted[0], extracted[1]);
                    toolResults.append("\n\n[系统查询结果 - 分数线信息]\n").append(result);
                } else {
                    toolResults.append("\n\n[系统提示]\n请提供具体的院校名称和专业名称，例如：\"清华大学计算机专业的分数线\"");
                }
            } catch (Exception e) {
                log.error("调用分数线查询工具失败: {}", e.getMessage());
            }
        }
        
        // 意图3: 资讯查询
        if (containsAny(message, "最新资讯", "考研资讯", "招生简章", "调剂信息", "调剂公告", 
                "招生信息", "招生通知", "最新消息", "最新通知")) {
            log.info("检测到资讯查询意图: userId={}", userId);
            try {
                String result = tools.getGraduationNews();
                toolResults.append("\n\n[系统查询结果 - 最新资讯]\n").append(result);
            } catch (Exception e) {
                log.error("调用资讯查询工具失败: {}", e.getMessage());
            }
        }
        
        // 意图4: 用户目标查询
        if (containsAny(message, "我的目标", "目标院校", "目标专业", "我想考", "我的目标是")) {
            log.info("检测到用户目标查询意图: userId={}", userId);
            try {
                String result = tools.getUserTarget(userId);
                toolResults.append("\n\n[系统查询结果 - 用户目标]\n").append(result);
            } catch (Exception e) {
                log.error("调用用户目标查询工具失败: {}", e.getMessage());
            }
        }
        
        // 如果有工具调用结果，将其附加到原消息后
        if (toolResults.length() > 0) {
            return userMessage + toolResults.toString() + 
                   "\n\n请基于以上查询结果，用专业、温暖的语气回答用户的问题。";
        }
        
        return userMessage;
    }
    
    /**
     * 检查消息是否包含任意关键词
     */
    private boolean containsAny(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 从消息中提取院校和专业名称
     * 简单实现：查找常见院校名称和专业名称
     * 
     * @param message 用户消息
     * @return [院校名称, 专业名称]
     */
    private String[] extractSchoolAndMajor(String message) {
        String[] result = new String[2];
        
        // 常见院校关键词
        String[] schools = {"清华", "北大", "复旦", "交大", "浙大", "南大", "中科大", "哈工大", 
                           "西交", "同济", "北航", "北理", "人大", "师大", "武大", "华科", "中山"};
        
        // 常见专业关键词
        String[] majors = {"计算机", "软件工程", "电子", "通信", "自动化", "机械", "土木", "化学", 
                          "物理", "数学", "经济", "金融", "管理", "法学", "新闻", "英语", "医学"};
        
        // 提取院校
        for (String school : schools) {
            if (message.contains(school)) {
                result[0] = school;
                break;
            }
        }
        
        // 提取专业
        for (String major : majors) {
            if (message.contains(major)) {
                result[1] = major;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * 构建消息列表
     */
    private List<ChatMessage> buildMessages(List<com.gradmate.gradmate_springboot.entity.ChatMessage> history) {
        return buildMessages(history, null);
    }
    
    /**
     * 构建消息列表（支持增强消息）
     * 
     * @param history 历史消息
     * @param enhancedLastMessage 增强后的最后一条消息（如果有）
     */
    private List<ChatMessage> buildMessages(List<com.gradmate.gradmate_springboot.entity.ChatMessage> history, 
                                           String enhancedLastMessage) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // 添加系统提示词
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content(systemPrompt)
                .build());
        
        // 添加历史消息
        for (int i = 0; i < history.size(); i++) {
            com.gradmate.gradmate_springboot.entity.ChatMessage msg = history.get(i);
            ChatMessageRole role = "user".equals(msg.getRole()) 
                ? ChatMessageRole.USER 
                : ChatMessageRole.ASSISTANT;
            
            // 如果是最后一条用户消息且有增强版本，使用增强版本
            String content = msg.getContent();
            if (enhancedLastMessage != null && i == history.size() - 1 && "user".equals(msg.getRole())) {
                content = enhancedLastMessage;
            }
            
            messages.add(ChatMessage.builder()
                    .role(role)
                    .content(content)
                    .build());
        }
        
        return messages;
    }
    
    /**
     * 清除用户会话历史
     * 
     * @param userId 用户ID
     */
    public void clearHistory(Long userId) {
        try {
            chatService.clearUserHistory(userId);
            log.info("清除会话历史: userId={}", userId);
        } catch (Exception e) {
            log.error("清除会话历史失败: userId={}, error={}", userId, e.getMessage());
        }
    }
    
    /**
     * 获取用户会话消息数量
     * 
     * @param userId 用户ID
     * @return 消息数量
     */
    public int getMessageCount(Long userId) {
        try {
            ChatSession session = chatService.getOrCreateSession(userId);
            return session.getMessageCount();
        } catch (Exception e) {
            log.error("获取消息数量失败: userId={}, error={}", userId, e.getMessage());
            return 0;
        }
    }
}
