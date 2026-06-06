package com.gradmate.gradmate_adminsystem_springboot.config;

import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import com.gradmate.gradmate_adminsystem_springboot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ApiException("请先登录管理员系统");
        }
        Map<String, Object> payload = jwtUtil.parse(auth.substring(7));
        request.setAttribute("adminId", ((Number) payload.get("adminId")).longValue());
        request.setAttribute("adminUsername", payload.get("username"));
        request.setAttribute("adminRole", payload.get("role"));
        return true;
    }
}
