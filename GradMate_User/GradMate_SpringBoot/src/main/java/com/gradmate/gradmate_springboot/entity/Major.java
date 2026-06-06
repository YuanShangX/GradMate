package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 专业实体类
 */
@Data
@TableName("major")
public class Major {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 学校名称
     */
    private String schoolName;
    
    /**
     * 专业名称
     */
    private String majorName;
    
    /**
     * 所在省份
     */
    private String province;
    
    /**
     * 专业代码
     */
    private String majorCode;
    
    /**
     * 学位类型
     */
    private String degreeType;
    
    /**
     * 考试方式
     */
    private String examType;
    
    /**
     * 院系所
     */
    private String department;
    
    /**
     * 学习方式
     */
    private String studyMode;
    
    /**
     * 研究方向
     */
    private String researchDirection;
    
    /**
     * 退役计划
     */
    private String veteranProgram;
    
    /**
     * 少骨计划
     */
    private String shaoguProgram;
    
    /**
     * 指导教师
     */
    private String advisor;
    
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
}