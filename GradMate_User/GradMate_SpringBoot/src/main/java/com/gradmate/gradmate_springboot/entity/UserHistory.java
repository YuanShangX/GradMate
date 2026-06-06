package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户浏览历史实体类
 */
@Data
@TableName("user_history")
public class UserHistory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 浏览类型：school/major/brochure/adjust/information
     */
    private String type;
    
    /**
     * 目标ID
     */
    private Long targetId;
    
    /**
     * 目标名称
     */
    private String targetName;
    
    /**
     * 元数据(JSON格式，存储额外信息)
     */
    private String metadata;
    
    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;
}
