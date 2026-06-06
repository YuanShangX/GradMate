package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 院校查询请求DTO
 */
@Data
public class SchoolQueryRequest {
    
    /**
     * 学校名称（模糊查询）
     */
    private String schoolName;
    
    /**
     * 所在省份
     */
    private String location;
    
    /**
     * 主管部门
     */
    private String subordinate;
    
    /**
     * 是否985（1是，0否）
     */
    private Integer is985;
    
    /**
     * 是否211/研究生院（1是，0否）
     */
    private Integer yjsy;
    
    /**
     * 是否双一流（1是，0否）
     */
    private Integer syl;
    
    /**
     * 是否自划线（1是，0否）
     */
    private Integer zhx;
    
    /**
     * 页码（从1开始）
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}