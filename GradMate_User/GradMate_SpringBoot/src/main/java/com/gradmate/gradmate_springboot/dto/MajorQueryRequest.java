package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 专业查询请求DTO
 */
@Data
public class MajorQueryRequest {
    
    /**
     * 学校名称（模糊查询）
     */
    private String schoolName;
    
    /**
     * 专业名称（模糊查询）
     */
    private String majorName;
    
    /**
     * 所在省份
     */
    private String province;
    
    /**
     * 学位类型
     */
    private String degreeType;
    
    /**
     * 考试方式
     */
    private String examType;
    
    /**
     * 学习方式
     */
    private String studyMode;
    
    /**
     * 院系所
     */
    private String department;
    
    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}