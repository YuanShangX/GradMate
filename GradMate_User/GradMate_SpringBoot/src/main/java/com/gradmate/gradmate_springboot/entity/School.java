package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学校实体类
 */
@Data
@TableName("school")
public class School {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 学校名称
     */
    private String schoolName;
    
    /**
     * 学校logo
     */
    private String logo;
    
    /**
     * 学校官网链接
     */
    private String link;
    
    /**
     * 所在地区
     */
    private String location;
    
    /**
     * 主管部门
     */
    private String subordinate;
    
    /**
     * 是否双一流（1是，0否）- 不在数据库表中
     */
    @TableField(exist = false)
    private Integer syl;
    
    /**
     * 是否研究生院/211（1是，0否）- 不在数据库表中
     */
    @TableField(exist = false)
    private Integer yjsy;
    
    /**
     * 是否自划线（1是，0否）- 不在数据库表中
     */
    @TableField(exist = false)
    private Integer zhx;
    
    /**
     * 关注数 - 不在数据库表中
     */
    @TableField(exist = false)
    private Integer followCount;
    
    /**
     * 浏览数 - 不在数据库表中
     */
    @TableField(exist = false)
    private Integer viewCount;
    
    /**
     * 招生数 - 不在数据库表中
     */
    @TableField(exist = false)
    private Integer enrollmentCount;
    
    /**
     * 专业数 - 不在数据库表中
     */
    @TableField(exist = false)
    private Integer majorCount;
}
