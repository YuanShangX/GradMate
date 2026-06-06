package com.gradmate.gradmate_springboot.controller;

import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.entity.ChatSession;
import com.gradmate.gradmate_springboot.service.ChatService;
import com.gradmate.gradmate_springboot.service.GradMateAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI控制器
 * 处理AI相关的HTTP请求
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {
    
    @Autowired
    private GradMateAgentService agentService;
    
    @Autowired
    private ChatService chatService;
    
    /**
     * AI对话接口
     * 
     * @param params 请求参数 { userId, message, sessionId(可选) }
     * @return AI回复
     */
    @PostMapping("/chat")
    public Result<String> chat(@RequestBody Map<String, Object> params) {
        try {
            // 获取参数
            Long userId = Long.valueOf(params.get("userId").toString());
            String message = params.get("message").toString();
            
            // 安全获取sessionId参数
            String sessionId = null;
            if (params.containsKey("sessionId") && params.get("sessionId") != null) {
                sessionId = params.get("sessionId").toString();
            }
            
            // 参数校验
            if (message == null || message.trim().isEmpty()) {
                return Result.error("消息不能为空");
            }
            
            if (message.length() > 1000) {
                return Result.error("消息长度不能超过1000字符");
            }
            
            // 调用AI服务
            String response = agentService.chat(userId, sessionId, message);
            
            return Result.success(response);
            
        } catch (NumberFormatException e) {
            return Result.error("用户ID格式错误");
        } catch (Exception e) {
            return Result.error("对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除会话历史
     * 
     * @param userId 用户ID
     * @return 操作结果
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
    
    /**
     * 获取会话信息
     * 
     * @param userId 用户ID
     * @return 会话信息
     */
    @GetMapping("/session")
    public Result<Map<String, Object>> getSession(@RequestParam Long userId) {
        try {
            int messageCount = agentService.getMessageCount(userId);
            
            Map<String, Object> session = new HashMap<>();
            session.put("userId", userId);
            session.put("messageCount", messageCount);
            session.put("hasHistory", messageCount > 0);
            
            return Result.success(session);
        } catch (Exception e) {
            return Result.error("获取会话信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新会话
     * 
     * @param params 请求参数 { userId }
     * @return 新会话信息
     */
    @PostMapping("/session/create")
    public Result<Map<String, Object>> createSession(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            ChatSession session = chatService.createNewSession(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", session.getSessionId());
            result.put("title", session.getTitle());
            result.put("createTime", session.getCreateTime());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("创建会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话列表
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    @GetMapping("/session/list")
    public Result<List<Map<String, Object>>> getSessionList(@RequestParam Long userId) {
        try {
            List<ChatSession> sessions = chatService.getUserSessions(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (ChatSession session : sessions) {
                Map<String, Object> item = new HashMap<>();
                item.put("sessionId", session.getSessionId());
                item.put("title", session.getTitle());
                item.put("messageCount", session.getMessageCount());
                item.put("createTime", session.getCreateTime());
                item.put("updateTime", session.getUpdateTime());
                result.add(item);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取会话列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除会话
     * 
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/session/{sessionId}")
    public Result<String> deleteSession(@PathVariable String sessionId) {
        try {
            chatService.deleteSession(sessionId);
            return Result.success("会话已删除");
        } catch (Exception e) {
            return Result.error("删除会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话消息列表
     * 
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @GetMapping("/session/{sessionId}/messages")
    public Result<List<Map<String, Object>>> getSessionMessages(@PathVariable String sessionId) {
        try {
            List<Map<String, Object>> messages = chatService.getSessionMessages(sessionId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error("获取消息列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新会话标题
     * 
     * @param sessionId 会话ID
     * @param params 请求参数 { title }
     * @return 操作结果
     */
    @PutMapping("/session/{sessionId}/title")
    public Result<String> updateSessionTitle(@PathVariable String sessionId, @RequestBody Map<String, Object> params) {
        try {
            String title = params.get("title").toString();
            if (title == null || title.trim().isEmpty()) {
                return Result.error("标题不能为空");
            }
            if (title.length() > 50) {
                return Result.error("标题长度不能超过50字符");
            }
            
            chatService.updateSessionTitle(sessionId, title);
            return Result.success("标题已更新");
        } catch (Exception e) {
            return Result.error("更新标题失败: " + e.getMessage());
        }
    }
}
