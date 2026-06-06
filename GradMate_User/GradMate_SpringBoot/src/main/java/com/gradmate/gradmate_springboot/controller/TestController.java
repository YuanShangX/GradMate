package com.gradmate.gradmate_springboot.controller;

import com.gradmate.gradmate_springboot.entity.School;
import com.gradmate.gradmate_springboot.mapper.SchoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private SchoolMapper schoolMapper;

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "GradMate后端服务运行正常！");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 查询学校总数
            Long count = schoolMapper.selectCount(null);
            // 查询前5条学校数据
            List<School> schools = schoolMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<School>().last("LIMIT 5")
            );
            
            result.put("code", 200);
            result.put("message", "数据库连接成功！");
            result.put("schoolCount", count);
            result.put("sampleSchools", schools);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据库连接失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 创建用户表
     */
    @GetMapping("/create-user-table")
    public Map<String, Object> createUserTable() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 执行创建用户表的SQL
            String sql = "CREATE TABLE IF NOT EXISTS `user` (" +
                    "`id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
                    "`openid` VARCHAR(100) UNIQUE COMMENT '微信openid'," +
                    "`nickname` VARCHAR(50) COMMENT '昵称'," +
                    "`avatar` VARCHAR(255) COMMENT '头像URL'," +
                    "`phone` VARCHAR(20) COMMENT '手机号（可选）'," +
                    "`target_school` VARCHAR(100) COMMENT '目标院校'," +
                    "`target_major` VARCHAR(100) COMMENT '目标专业'," +
                    "`create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "`update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "INDEX `idx_openid` (`openid`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'";
            
            // 这里需要使用JdbcTemplate来执行DDL语句
            result.put("code", 200);
            result.put("message", "请手动在数据库中执行SQL创建用户表");
            result.put("sql", sql);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "创建用户表失败：" + e.getMessage());
        }
        return result;
    }
}
