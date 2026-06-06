package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调剂公告实体类
 */
@Data
@TableName("school_adjust")
public class SchoolAdjust {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 公告标题
     */
    private String title;
    
    /**
     * 公告链接
     */
    private String url;
    
    /**
     * 发布日期
     */
    private LocalDateTime publishDate;
}
