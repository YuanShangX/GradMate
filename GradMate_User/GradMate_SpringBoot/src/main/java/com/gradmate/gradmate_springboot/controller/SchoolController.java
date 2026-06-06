package com.gradmate.gradmate_springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.dto.SchoolQueryRequest;
import com.gradmate.gradmate_springboot.entity.School;
import com.gradmate.gradmate_springboot.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学校控制器
 */
@RestController
@RequestMapping("/api/school")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    /**
     * 分页查询学校列表
     */
    @GetMapping("/list")
    public Result<Page<School>> listSchools(SchoolQueryRequest request) {
        try {
            Page<School> result = schoolService.listSchools(request);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取学校详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getSchoolDetail(@PathVariable Long id) {
        try {
            Map<String, Object> result = schoolService.getSchoolDetail(id);
            if (result == null) {
                return Result.error("学校不存在");
            }
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有省份列表
     */
    @GetMapping("/provinces")
    public Result<List<String>> getProvinces() {
        try {
            List<String> provinces = schoolService.getProvinces();
            return Result.success("查询成功", provinces);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有主管部门列表
     */
    @GetMapping("/subordinates")
    public Result<List<String>> getSubordinates() {
        try {
            List<String> subordinates = schoolService.getSubordinates();
            return Result.success("查询成功", subordinates);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}