package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 子任务请求DTO
 */
@Data
public class SubtaskRequest {
    
    /**
     * 子任务内容
     */
    private String subtaskContent;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
}
