package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 子任务实体类
 */
@Data
@TableName("task_subtasks")
public class TaskSubtask {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 父任务ID
     */
    private Long parentTaskId;
    
    /**
     * 子任务内容
     */
    private String subtaskContent;
    
    /**
     * 是否完成：0未完成 1已完成
     */
    private Integer isCompleted;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completeTime;
}
