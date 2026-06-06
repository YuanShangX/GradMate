package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资讯响应DTO（包含学校名称）
 */
@Data
public class InfoResponse {
    
    /**
     * 资讯ID
     */
    private Long id;
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 学校名称
     */
    private String schoolName;
    
    /**
     * 资讯标题
     */
    private String title;
    
    /**
     * 资讯链接
     */
    private String url;
    
    /**
     * 发布日期
     */
    private LocalDateTime publishDate;
    
    /**
     * 资讯类型（brochure/adjust/information）
     */
    private String type;
    
    /**
     * 资讯标签（用于前端显示，如"招生简章"、"调剂公告"、"招生通知"）
     */
    private String tag;
}
