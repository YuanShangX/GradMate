package com.gradmate.gradmate_adminsystem_springboot.controller;

import com.gradmate.gradmate_adminsystem_springboot.common.Result;
import com.gradmate.gradmate_adminsystem_springboot.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.ok(dashboardService.stats());
    }
}
