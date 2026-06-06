package com.gradmate.gradmate_springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.dto.InfoResponse;
import com.gradmate.gradmate_springboot.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 资讯控制器
 */
@RestController
@RequestMapping("/api/info")
public class InfoController {
    
    @Autowired
    private InfoService infoService;
    
    /**
     * 获取招生简章列表
     * 
     * @param schoolId 学校ID（可选）
     * @param schoolName 学校名称（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 简章列表
     */
    @GetMapping("/brochure/list")
    public Result<Page<InfoResponse>> listBrochures(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String schoolName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<InfoResponse> page = infoService.listBrochures(schoolId, schoolName, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取招生简章详情
     * 
     * @param id 简章ID
     * @return 简章详情
     */
    @GetMapping("/brochure/{id}")
    public Result<InfoResponse> getBrochureDetail(@PathVariable Long id) {
        InfoResponse response = infoService.getBrochureDetail(id);
        if (response == null) {
            return Result.error("简章不存在");
        }
        return Result.success(response);
    }
    
    /**
     * 获取调剂公告列表
     * 
     * @param schoolId 学校ID（可选）
     * @param schoolName 学校名称（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 调剂公告列表
     */
    @GetMapping("/adjust/list")
    public Result<Page<InfoResponse>> listAdjusts(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String schoolName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<InfoResponse> page = infoService.listAdjusts(schoolId, schoolName, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取调剂公告详情
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/adjust/{id}")
    public Result<InfoResponse> getAdjustDetail(@PathVariable Long id) {
        InfoResponse response = infoService.getAdjustDetail(id);
        if (response == null) {
            return Result.error("公告不存在");
        }
        return Result.success(response);
    }
    
    /**
     * 获取招生通知列表
     * 
     * @param schoolId 学校ID（可选）
     * @param schoolName 学校名称（可选，模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 招生通知列表
     */
    @GetMapping("/information/list")
    public Result<Page<InfoResponse>> listInformations(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String schoolName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<InfoResponse> page = infoService.listInformations(schoolId, schoolName, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 获取招生通知详情
     * 
     * @param id 通知ID
     * @return 通知详情
     */
    @GetMapping("/information/{id}")
    public Result<InfoResponse> getInformationDetail(@PathVariable Long id) {
        InfoResponse response = infoService.getInformationDetail(id);
        if (response == null) {
            return Result.error("通知不存在");
        }
        return Result.success(response);
    }
    
    /**
     * 获取混合资讯列表（用于首页展示）
     * 混合展示招生简章、调剂公告、招生通知
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 混合资讯列表
     */
    @GetMapping("/mixed/list")
    public Result<Page<InfoResponse>> listMixedInfo(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Page<InfoResponse> page = infoService.listMixedInfo(pageNum, pageSize);
        return Result.success(page);
    }
}
