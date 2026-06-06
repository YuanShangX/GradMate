package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 标签请求DTO
 */
@Data
public class TaskTagRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 标签名称
     */
    private String tagName;
    
    /**
     * 标签颜色
     */
    private String tagColor;
    
    /**
     * 图标
     */
    private String icon;
}
