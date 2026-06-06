package com.gradmate.gradmate_adminsystem_springboot.controller;

import com.gradmate.gradmate_adminsystem_springboot.common.Result;
import com.gradmate.gradmate_adminsystem_springboot.dto.LoginRequest;
import com.gradmate.gradmate_adminsystem_springboot.service.AdminAuthService;
import com.gradmate.gradmate_adminsystem_springboot.service.AdminOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AuthController {
    private final AdminAuthService authService;
    private final AdminOperationLogService logService;

    public AuthController(AdminAuthService authService, AdminOperationLogService logService) {
        this.authService = authService;
        this.logService = logService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Map<String, Object> data = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            Object adminObj = data.get("admin");
            if (adminObj instanceof Map<?, ?> admin) {
                request.setAttribute("adminId", admin.get("id"));
                request.setAttribute("adminUsername", admin.get("username"));
            }
            logService.record(request, "auth", "LOGIN", null, "SUCCESS", "登录成功");
            return Result.ok(data);
        } catch (RuntimeException e) {
            request.setAttribute("adminUsername", loginRequest.getUsername());
            logService.record(request, "auth", "LOGIN", null, "FAIL", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/profile")
    public Result<Map<String, Object>> profile(HttpServletRequest request) {
        return Result.ok(authService.profile((Long) request.getAttribute("adminId")));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        logService.record(request, "auth", "LOGOUT", null, "SUCCESS", "退出登录");
        return Result.ok();
    }
}
