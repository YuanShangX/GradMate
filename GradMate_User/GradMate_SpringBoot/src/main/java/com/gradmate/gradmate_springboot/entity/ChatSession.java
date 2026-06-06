package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话实体
 */
@Data
@TableName("chat_session")
public class ChatSession {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String sessionId;
    
    private String title;
    
    private Integer messageCount;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
