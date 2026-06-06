package com.gradmate.gradmate_springboot.dto;

import com.gradmate.gradmate_springboot.entity.DailyTask;
import com.gradmate.gradmate_springboot.entity.TaskSubtask;
import lombok.Data;

import java.util.List;

/**
 * 任务详情响应DTO
 */
@Data
public class TaskDetailResponse {
    
    /**
     * 任务信息
     */
    private DailyTask task;
    
    /**
     * 子任务列表
     */
    private List<TaskSubtask> subtasks;
    
    /**
     * 标签列表
     */
    private List<String> tags;
}
