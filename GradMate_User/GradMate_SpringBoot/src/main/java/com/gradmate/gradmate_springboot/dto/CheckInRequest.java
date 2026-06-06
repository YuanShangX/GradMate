package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 打卡请求DTO
 */
@Data
public class CheckInRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 学习时长（分钟）
     */
    private Integer duration;
    
    /**
     * 打卡备注
     */
    private String note;
    
    /**
     * 心情：1-5星
     */
    private Integer mood;
    
    /**
     * 学习类型
     */
    private String studyType;
}