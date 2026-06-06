package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡记录实体类
 */
@Data
@TableName("study_record")
public class StudyRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 打卡日期
     */
    private LocalDate checkDate;
    
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
     * 专注次数
     */
    private Integer focusCount;
    
    /**
     * 暂停次数
     */
    private Integer pauseCount;
    
    /**
     * 学习类型
     */
    private String studyType;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}