package com.gradmate.gradmate_adminsystem_springboot.controller;

import com.gradmate.gradmate_adminsystem_springboot.common.PageResult;
import com.gradmate.gradmate_adminsystem_springboot.common.Result;
import com.gradmate.gradmate_adminsystem_springboot.service.AdminOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminOperationLogController {
    private final AdminOperationLogService logService;

    public AdminOperationLogController(AdminOperationLogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public Result<PageResult> page(@RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "10") int pageSize,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String actionType,
                                   @RequestParam(required = false) String resourceKey,
                                   @RequestParam(required = false) String adminUsername) {
        return Result.ok(logService.page(pageNum, pageSize, keyword, actionType, resourceKey, adminUsername));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.ok(logService.detail(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        logService.delete(id, request);
        return Result.ok();
    }
}
