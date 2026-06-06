package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 学校专业详情响应DTO
 */
@Data
public class SchoolMajorDetailResponse {
    
    /**
     * 学校logo
     */
    private String schoolLogo;
    
    /**
     * 学校名称
     */
    private String schoolName;
    
    /**
     * 专业代码
     */
    private String majorCode;
    
    /**
     * 专业名称
     */
    private String majorName;
    
    /**
     * 院系所
     */
    private String department;
    
    /**
     * 研究方向
     */
    private String researchDirection;
    
    /**
     * 学习方式
     */
    private String studyMode;
    
    /**
     * 考试方式
     */
    private String examType;
    
    /**
     * 拟招生人数
     */
    private String plannedEnrollment;
    
    /**
     * 考试科目1
     */
    private String examSubject1;
    
    /**
     * 考试科目2
     */
    private String examSubject2;
    
    /**
     * 考试科目3
     */
    private String examSubject3;
    
    /**
     * 考试科目4
     */
    private String examSubject4;
    
    /**
     * 指导教师
     */
    private String advisor;
}
