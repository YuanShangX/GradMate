package com.gradmate.gradmate_springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.entity.UserHistory;
import com.gradmate.gradmate_springboot.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 浏览历史控制器
 */
@RestController
@RequestMapping("/api/history")
public class HistoryController {
    
    @Autowired
    private HistoryService historyService;
    
    /**
     * 添加浏览记录
     */
    @PostMapping("/add")
    public Result<String> addHistory(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String type = params.get("type").toString();
        Long targetId = Long.valueOf(params.get("targetId").toString());
        String targetName = params.get("targetName").toString();
        String metadata = params.containsKey("metadata") ? params.get("metadata").toString() : null;
        
        boolean success = historyService.addHistory(userId, type, targetId, targetName, metadata);
        
        if (success) {
            return Result.success("添加成功");
        } else {
            return Result.error("添加失败");
        }
    }
    
    /**
     * 浏览历史列表
     */
    @GetMapping("/list")
    public Result<Page<UserHistory>> listHistory(@RequestParam Long userId,
                                                  @RequestParam(required = false) String type,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserHistory> page = historyService.listHistory(userId, type, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 清空浏览历史
     */
    @GetMapping("/clear")
    public Result<String> clearHistory(@RequestParam Long userId) {
        boolean success = historyService.clearHistory(userId);
        
        if (success) {
            return Result.success("清空成功");
        } else {
            return Result.error("清空失败");
        }
    }
    
    /**
     * 删除单条历史记录
     */
    @GetMapping("/delete/{id}")
    public Result<String> deleteHistory(@PathVariable Long id) {
        boolean success = historyService.deleteHistory(id);
        
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 获取历史记录总数
     */
    @GetMapping("/count")
    public Result<Long> getHistoryCount(@RequestParam Long userId) {
        long count = historyService.getHistoryCount(userId);
        return Result.success(count);
    }
}
