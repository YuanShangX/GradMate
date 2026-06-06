package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学校特性实体类
 */
@Data
@TableName("school_character")
public class SchoolCharacter {
    
    @TableId
    private Long schoolId;
    
    /**
     * 是否985（1是，0否）
     */
    private Integer syl;
    
    /**
     * 是否研究生院（1是，0否）
     */
    private Integer yjsy;
    
    /**
     * 是否自划线（1是，0否）
     */
    private Integer zhx;
}