package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 学习统计响应DTO
 */
@Data
public class StudyStatsResponse {
    
    /**
     * 连续打卡天数
     */
    private Integer continuousDays;
    
    /**
     * 本月打卡天数
     */
    private Integer monthlyDays;
    
    /**
     * 累计学习时长（分钟）
     */
    private Integer totalDuration;
    
    /**
     * 累计学习时长（小时，格式化显示）
     */
    private String totalHours;
    
    /**
     * 今日是否已打卡
     */
    private Boolean todayChecked;
    
    /**
     * 今日任务完成情况
     */
    private String todayTasks;
    
    /**
     * 距离考研剩余天数
     */
    private Long daysLeft;
}