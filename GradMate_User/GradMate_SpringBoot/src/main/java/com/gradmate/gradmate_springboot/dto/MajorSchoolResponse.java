package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 专业对应的学校响应DTO
 */
@Data
public class MajorSchoolResponse {
    
    /**
     * 学校名称
     */
    private String schoolName;
    
    /**
     * 学校logo
     */
    private String logo;
    
    /**
     * 所在地区
     */
    private String location;
    
    /**
     * 院系
     */
    private String department;
    
    /**
     * 学习方式
     */
    private String studyMode;
    
    /**
     * 考试方式
     */
    private String examType;
    
    /**
     * 计划招生人数
     */
    private String plannedEnrollment;
    
    /**
     * 是否985
     */
    private Boolean is985;
    
    /**
     * 是否211
     */
    private Boolean is211;
    
    /**
     * 是否双一流
     */
    private Boolean isSyl;
    
    /**
     * 是否自划线
     */
    private Boolean isZhx;
    
    /**
     * 关注数（模拟数据）
     */
    private Integer followCount;
    
    /**
     * 浏览数（模拟数据）
     */
    private Integer viewCount;
    
    /**
     * 分数线（模拟数据）
     */
    private String admissionScore;
}
