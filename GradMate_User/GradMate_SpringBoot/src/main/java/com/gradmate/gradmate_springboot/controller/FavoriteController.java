package com.gradmate.gradmate_springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.entity.UserFavorite;
import com.gradmate.gradmate_springboot.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 添加专业收藏（使用学校名称和专业代码）
     */
    @PostMapping("/addMajor")
    public Result<String> addMajorFavorite(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String schoolName = params.get("schoolName").toString();
        String majorCode = params.get("majorCode").toString();
        String majorName = params.get("majorName").toString();
        
        boolean success = favoriteService.addFavoriteBySchoolAndMajor(userId, schoolName, majorCode, majorName);
        
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("已收藏或收藏失败");
        }
    }
    
    /**
     * 取消专业收藏（使用学校名称和专业代码）
     */
    @GetMapping("/removeMajor")
    public Result<String> removeMajorFavorite(@RequestParam Long userId,
                                              @RequestParam String schoolName,
                                              @RequestParam String majorCode) {
        boolean success = favoriteService.removeFavoriteBySchoolAndMajor(userId, schoolName, majorCode);
        
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }
    
    /**
     * 检查专业是否已收藏（使用学校名称和专业代码）
     */
    @GetMapping("/checkMajor")
    public Result<Boolean> checkMajorFavorite(@RequestParam Long userId,
                                              @RequestParam String schoolName,
                                              @RequestParam String majorCode) {
        boolean isFavorited = favoriteService.isFavoritedBySchoolAndMajor(userId, schoolName, majorCode);
        return Result.success(isFavorited);
    }
    
    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String type = params.get("type").toString();
        Long targetId = Long.valueOf(params.get("targetId").toString());
        String targetName = params.get("targetName").toString();
        
        boolean success = favoriteService.addFavorite(userId, type, targetId, targetName);
        
        if (success) {
            return Result.success("收藏成功");
        } else {
            return Result.error("已收藏或收藏失败");
        }
    }
    
    /**
     * 取消收藏
     */
    @GetMapping("/remove")
    public Result<String> removeFavorite(@RequestParam Long userId,
                                         @RequestParam String type,
                                         @RequestParam Long targetId) {
        boolean success = favoriteService.removeFavorite(userId, type, targetId);
        
        if (success) {
            return Result.success("取消收藏成功");
        } else {
            return Result.error("取消收藏失败");
        }
    }
    
    /**
     * 收藏列表
     */
    @GetMapping("/list")
    public Result<Page<UserFavorite>> listFavorites(@RequestParam Long userId,
                                                     @RequestParam(required = false) String type,
                                                     @RequestParam(defaultValue = "1") int pageNum,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserFavorite> page = favoriteService.listFavorites(userId, type, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId,
                                         @RequestParam String type,
                                         @RequestParam Long targetId) {
        boolean isFavorited = favoriteService.isFavorited(userId, type, targetId);
        return Result.success(isFavorited);
    }
    
    /**
     * 获取收藏总数
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getFavoriteCount(@RequestParam Long userId) {
        long totalCount = favoriteService.getFavoriteCount(userId);
        long schoolCount = favoriteService.getFavoriteCountByType(userId, "school");
        long majorCount = favoriteService.getFavoriteCountByType(userId, "major");
        long infoCount = favoriteService.getFavoriteCountByType(userId, "brochure") +
                        favoriteService.getFavoriteCountByType(userId, "adjust") +
                        favoriteService.getFavoriteCountByType(userId, "information");
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", totalCount);
        result.put("school", schoolCount);
        result.put("major", majorCount);
        result.put("info", infoCount);
        
        return Result.success(result);
    }
}
