package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 聊天会话Mapper
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
}
