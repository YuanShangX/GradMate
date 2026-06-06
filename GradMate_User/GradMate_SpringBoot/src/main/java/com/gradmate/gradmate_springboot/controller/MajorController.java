package com.gradmate.gradmate_springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.dto.MajorQueryRequest;
import com.gradmate.gradmate_springboot.entity.Major;
import com.gradmate.gradmate_springboot.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业控制器
 */
@RestController
@RequestMapping("/api/major")
public class MajorController {

    @Autowired
    private MajorService majorService;

    /**
     * 分页搜索专业
     */
    @GetMapping("/search")
    public Result<Page<Major>> searchMajors(MajorQueryRequest request) {
        try {
            Page<Major> result = majorService.searchMajors(request);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取专业详情
     */
    @GetMapping("/{id}")
    public Result<Major> getMajorDetail(@PathVariable Integer id) {
        try {
            Major result = majorService.getMajorDetail(id);
            if (result == null) {
                return Result.error("专业不存在");
            }
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据学校名称查询专业列表
     */
    @GetMapping("/school/{schoolName}")
    public Result<Page<Major>> getMajorsBySchool(
            @PathVariable String schoolName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Major> result = majorService.getMajorsBySchool(schoolName, pageNum, pageSize);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有学位类型列表
     */
    @GetMapping("/degree-types")
    public Result<List<String>> getDegreeTypes() {
        try {
            List<String> degreeTypes = majorService.getDegreeTypes();
            return Result.success("查询成功", degreeTypes);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有考试方式列表
     */
    @GetMapping("/exam-types")
    public Result<List<String>> getExamTypes() {
        try {
            List<String> examTypes = majorService.getExamTypes();
            return Result.success("查询成功", examTypes);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有学习方式列表
     */
    @GetMapping("/study-modes")
    public Result<List<String>> getStudyModes() {
        try {
            List<String> studyModes = majorService.getStudyModes();
            return Result.success("查询成功", studyModes);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据学科门类获取一级学科和专业列表
     */
    @GetMapping("/disciplines")
    public Result<List<com.gradmate.gradmate_springboot.dto.DisciplineResponse>> getDisciplinesByCategory(
            @RequestParam String categoryCode) {
        try {
            List<com.gradmate.gradmate_springboot.dto.DisciplineResponse> result = 
                majorService.getDisciplinesByCategory(categoryCode);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 从专业类型表获取学科门类下的一级学科和专业列表（支持学硕/专硕筛选）
     */
    @GetMapping("/disciplines-v2")
    public Result<List<com.gradmate.gradmate_springboot.dto.DisciplineResponse>> getDisciplinesByCategoryV2(
            @RequestParam String categoryCode,
            @RequestParam(required = false) String degreeType) {
        try {
            List<com.gradmate.gradmate_springboot.dto.DisciplineResponse> result = 
                majorService.getDisciplinesByCategoryFromCategoryTable(categoryCode, degreeType);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据专业代码查询开设该专业的学校列表
     */
    @GetMapping("/schools")
    public Result<List<com.gradmate.gradmate_springboot.dto.MajorSchoolResponse>> getSchoolsByMajorCode(
            @RequestParam String majorCode,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String schoolType) {
        try {
            List<com.gradmate.gradmate_springboot.dto.MajorSchoolResponse> result = 
                majorService.getSchoolsByMajorCode(majorCode, location, schoolType);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询某个学校某个专业的详细招生信息
     */
    @GetMapping("/schoolMajorDetail")
    public Result<List<com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse>> getSchoolMajorDetail(
            @RequestParam String schoolName,
            @RequestParam(required = false) String majorCode,
            @RequestParam(required = false) String majorName) {
        try {
            List<com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse> result = 
                majorService.getSchoolMajorDetail(schoolName, majorCode, majorName);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}