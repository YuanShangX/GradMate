package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 招生简章实体类
 */
@Data
@TableName("school_brochure")
public class SchoolBrochure {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 简章标题
     */
    private String title;
    
    /**
     * 简章链接
     */
    private String url;
    
    /**
     * 发布日期
     */
    private LocalDateTime publishDate;
}
