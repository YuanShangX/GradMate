package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务提醒实体类
 */
@Data
@TableName("task_reminders")
public class TaskReminder {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 提醒时间
     */
    private LocalDateTime reminderTime;
    
    /**
     * 提醒类型：once/daily/weekly
     */
    private String reminderType;
    
    /**
     * 是否已发送：0未发送 1已发送
     */
    private Integer isSent;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
