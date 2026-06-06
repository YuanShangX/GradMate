package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日任务实体类
 */
@Data
@TableName("daily_task")
public class DailyTask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
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
     * 是否完成：0未完成 1已完成
     */
    private Integer isCompleted;
    
    /**
     * 优先级：1低 2中 3高
     */
    private Integer priority;
    
    /**
     * 卡片颜色主题
     */
    private String colorTheme;
    
    /**
     * 预计学习时长（分钟）
     */
    private Integer estimatedDuration;
    
    /**
     * 实际学习时长（分钟）
     */
    private Integer actualDuration;
    
    /**
     * 标签（逗号分隔）
     */
    private String tags;
    
    /**
     * 父任务ID（用于子任务）
     */
    private Long parentTaskId;
    
    /**
     * 重复类型：none/daily/weekly/monthly
     */
    private String repeatType;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 提醒时间
     */
    private LocalDateTime reminderTime;
    
    /**
     * 任务状态：todo/doing/done
     */
    private String status;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}