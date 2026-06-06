package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 任务请求DTO
 */
@Data
public class TaskRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务内容
     */
    private String taskContent;
    
    /**
     * 任务日期
     */
    private LocalDate taskDate;
    
    /**
     * 优先级：1低 2中 3高
     */
    private Integer priority;
    
    /**
     * 预计学习时长（分钟）
     */
    private Integer estimatedDuration;
    
    /**
     * 标签（逗号分隔）
     */
    private String tags;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 重复类型：none, daily, weekly, monthly
     */
    private String repeatType;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
}