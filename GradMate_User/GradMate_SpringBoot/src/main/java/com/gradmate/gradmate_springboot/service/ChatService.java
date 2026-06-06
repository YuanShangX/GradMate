package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gradmate.gradmate_springboot.entity.ChatMessage;
import com.gradmate.gradmate_springboot.entity.ChatSession;
import com.gradmate.gradmate_springboot.mapper.ChatMessageMapper;
import com.gradmate.gradmate_springboot.mapper.ChatSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 聊天服务
 * 负责会话和消息的持久化管理
 */
@Slf4j
@Service
public class ChatService {
    
    @Autowired
    private ChatSessionMapper sessionMapper;
    
    @Autowired
    private ChatMessageMapper messageMapper;
    
    /**
     * 获取或创建用户会话
     */
    public ChatSession getOrCreateSession(Long userId) {
        // 查找用户最近的会话
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("update_time")
               .last("LIMIT 1");
        
        ChatSession session = sessionMapper.selectOne(wrapper);
        
        if (session == null) {
            // 创建新会话
            session = new ChatSession();
            session.setUserId(userId);
            session.setSessionId(UUID.randomUUID().toString());
            session.setTitle("新对话");
            session.setMessageCount(0);
            session.setCreateTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
            
            sessionMapper.insert(session);
            log.info("创建新会话: userId={}, sessionId={}", userId, session.getSessionId());
        }
        
        return session;
    }
    
    /**
     * 保存消息
     */
    @Transactional
    public void saveMessage(String sessionId, Long userId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(content);
        message.setTokens(estimateTokens(content));
        message.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        
        // 更新会话
        updateSession(sessionId);
        
        log.info("保存消息: sessionId={}, role={}, length={}", sessionId, role, content.length());
    }
    
    /**
     * 获取会话历史消息
     */
    public List<ChatMessage> getSessionMessages(String sessionId, int limit) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId)
               .orderByDesc("create_time")
               .last("LIMIT " + limit);
        
        List<ChatMessage> messages = messageMapper.selectList(wrapper);
        
        // 反转顺序，使最早的消息在前
        java.util.Collections.reverse(messages);
        
        return messages;
    }
    
    /**
     * 获取会话历史消息（返回Map格式）
     */
    public List<Map<String, Object>> getSessionMessages(String sessionId) {
        List<ChatMessage> messages = getSessionMessages(sessionId, 100); // 默认获取最近100条
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (ChatMessage message : messages) {
            Map<String, Object> item = new HashMap<>();
            // 将assistant转换为ai，保持与前端一致
            String role = "assistant".equals(message.getRole()) ? "ai" : message.getRole();
            item.put("role", role);
            item.put("content", message.getContent());
            item.put("createTime", message.getCreateTime());
            result.add(item);
        }
        
        return result;
    }
    
    /**
     * 清除用户会话历史
     */
    @Transactional
    public void clearUserHistory(Long userId) {
        // 获取用户所有会话
        QueryWrapper<ChatSession> sessionWrapper = new QueryWrapper<>();
        sessionWrapper.eq("user_id", userId);
        List<ChatSession> sessions = sessionMapper.selectList(sessionWrapper);
        
        for (ChatSession session : sessions) {
            // 删除会话消息
            QueryWrapper<ChatMessage> messageWrapper = new QueryWrapper<>();
            messageWrapper.eq("session_id", session.getSessionId());
            messageMapper.delete(messageWrapper);
        }
        
        // 删除会话
        sessionMapper.delete(sessionWrapper);
        
        log.info("清除用户历史: userId={}, sessions={}", userId, sessions.size());
    }
    
    /**
     * 更新会话信息
     */
    private void updateSession(String sessionId) {
        ChatSession session = sessionMapper.selectOne(
            new QueryWrapper<ChatSession>().eq("session_id", sessionId)
        );
        
        if (session != null) {
            // 统计消息数量
            QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
            wrapper.eq("session_id", sessionId);
            long count = messageMapper.selectCount(wrapper);
            
            session.setMessageCount((int) count);
            session.setUpdateTime(LocalDateTime.now());
            
            // 自动生成标题（取第一条用户消息的前20个字符）
            if ("新对话".equals(session.getTitle()) && count > 0) {
                QueryWrapper<ChatMessage> firstMsgWrapper = new QueryWrapper<>();
                firstMsgWrapper.eq("session_id", sessionId)
                              .eq("role", "user")
                              .orderByAsc("create_time")
                              .last("LIMIT 1");
                
                ChatMessage firstMsg = messageMapper.selectOne(firstMsgWrapper);
                if (firstMsg != null) {
                    String title = firstMsg.getContent();
                    if (title.length() > 20) {
                        title = title.substring(0, 20) + "...";
                    }
                    session.setTitle(title);
                }
            }
            
            sessionMapper.updateById(session);
        }
    }
    
    /**
     * 估算Token数量（简单估算：中文1字=2token，英文1词=1token）
     */
    private int estimateTokens(String content) {
        if (content == null) return 0;
        
        int chineseCount = 0;
        int otherCount = 0;
        
        for (char c : content.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FA5) {
                chineseCount++;
            } else {
                otherCount++;
            }
        }
        
        return chineseCount * 2 + otherCount / 4;
    }
    
    /**
     * 获取用户会话列表
     */
    public List<ChatSession> getUserSessions(Long userId) {
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("update_time");
        
        return sessionMapper.selectList(wrapper);
    }


    /**
     * 创建新会话
     */
    public ChatSession createNewSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setSessionId(UUID.randomUUID().toString());
        session.setTitle("新对话");
        session.setMessageCount(0);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());

        sessionMapper.insert(session);
        log.info("创建新会话: userId={}, sessionId={}", userId, session.getSessionId());

        return session;
    }

    /**
     * 根据ID获取会话
     */
    public ChatSession getSessionById(String sessionId) {
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId);
        return sessionMapper.selectOne(wrapper);
    }

    /**
     * 删除指定会话
     */
    @Transactional
    public void deleteSession(String sessionId) {
        // 删除会话消息
        QueryWrapper<ChatMessage> messageWrapper = new QueryWrapper<>();
        messageWrapper.eq("session_id", sessionId);
        messageMapper.delete(messageWrapper);

        // 删除会话
        QueryWrapper<ChatSession> sessionWrapper = new QueryWrapper<>();
        sessionWrapper.eq("session_id", sessionId);
        sessionMapper.delete(sessionWrapper);

        log.info("删除会话: sessionId={}", sessionId);
    }

    /**
     * 更新会话标题
     */
    public void updateSessionTitle(String sessionId, String title) {
        ChatSession session = getSessionById(sessionId);
        if (session != null) {
            session.setTitle(title);
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
            log.info("更新会话标题: sessionId={}, title={}", sessionId, title);
        }
    }
}
