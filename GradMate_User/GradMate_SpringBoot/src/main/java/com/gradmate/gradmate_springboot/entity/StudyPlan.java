package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学习计划实体类
 */
@Data
@TableName("study_plan")
public class StudyPlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 计划标题
     */
    private String title;
    
    /**
     * 计划内容
     */
    private String content;
    
    /**
     * 目标日期（考研日期）
     */
    private LocalDate targetDate;
    
    /**
     * 状态：0进行中 1已完成 2已放弃
     */
    private Integer status;
    
    /**
     * 背景图URL
     */
    private String backgroundImage;
    
    /**
     * 开始日期
     */
    private LocalDate startDate;
    
    /**
     * 总天数
     */
    private Integer totalDays;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}