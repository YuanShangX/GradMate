package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专业类型实体类（独立表）
 */
@Data
@TableName("major_category")
public class MajorCategory {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 学科门类代码（2位）
     */
    private String categoryCode;
    
    /**
     * 学科门类名称
     */
    private String categoryName;
    
    /**
     * 一级学科代码（4位）
     */
    private String disciplineCode;
    
    /**
     * 一级学科名称
     */
    private String disciplineName;
    
    /**
     * 专业代码（6位或更多）
     */
    private String majorCode;
    
    /**
     * 专业名称
     */
    private String majorName;
    
    /**
     * 学位类型：academic-学硕，professional-专硕
     */
    private String degreeType;
    
    /**
     * 排序字段
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
