package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Data
@TableName("chat_message")
public class ChatMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String sessionId;
    
    private Long userId;
    
    private String role;
    
    private String content;
    
    private Integer tokens;
    
    private String toolCalls;
    
    private LocalDateTime createTime;
}
