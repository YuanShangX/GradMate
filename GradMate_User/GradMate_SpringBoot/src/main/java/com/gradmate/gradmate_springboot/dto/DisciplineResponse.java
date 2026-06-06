package com.gradmate.gradmate_springboot.dto;

import lombok.Data;
import java.util.List;

/**
 * 学科响应DTO
 */
@Data
public class DisciplineResponse {
    
    /**
     * 一级学科代码（4位）
     */
    private String code;
    
    /**
     * 一级学科名称
     */
    private String name;
    
    /**
     * 该一级学科下的具体专业列表
     */
    private List<MajorInfo> majors;
    
    @Data
    public static class MajorInfo {
        /**
         * 专业代码（6位）
         */
        private String code;
        
        /**
         * 专业名称
         */
        private String name;
    }
}
