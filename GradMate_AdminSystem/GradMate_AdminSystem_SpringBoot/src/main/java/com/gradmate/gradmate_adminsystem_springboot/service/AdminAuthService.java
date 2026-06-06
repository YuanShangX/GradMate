package com.gradmate.gradmate_adminsystem_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import com.gradmate.gradmate_adminsystem_springboot.entity.AdminUser;
import com.gradmate.gradmate_adminsystem_springboot.mapper.AdminUserMapper;
import com.gradmate.gradmate_adminsystem_springboot.util.JwtUtil;
import com.gradmate.gradmate_adminsystem_springboot.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AdminAuthService {
    private final AdminUserMapper adminUserMapper;
    private final JwtUtil jwtUtil;

    public AdminAuthService(AdminUserMapper adminUserMapper, JwtUtil jwtUtil) {
        this.adminUserMapper = adminUserMapper;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, Object> login(String username, String password) {
        AdminUser admin = adminUserMapper.selectOne(new QueryWrapper<AdminUser>().eq("username", username));
        if (admin == null || !PasswordUtil.matches(password, admin.getPassword())) {
            throw new ApiException("用户名或密码错误");
        }
        if (admin.getStatus() != null && admin.getStatus() == 0) {
            throw new ApiException("管理员账号已禁用");
        }
        admin.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(admin);
        String token = jwtUtil.createToken(admin.getId(), admin.getUsername(), admin.getRole());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", token);
        data.put("expiresIn", jwtUtil.getExpireMinutes() * 60);
        data.put("admin", toSafeMap(admin));
        return data;
    }

    public Map<String, Object> profile(Long adminId) {
        AdminUser admin = adminUserMapper.selectById(adminId);
        if (admin == null) {
            throw new ApiException("管理员不存在");
        }
        return toSafeMap(admin);
    }

    private Map<String, Object> toSafeMap(AdminUser admin) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", admin.getId());
        data.put("username", admin.getUsername());
        data.put("nickname", admin.getNickname());
        data.put("role", admin.getRole());
        data.put("status", admin.getStatus());
        data.put("lastLoginTime", admin.getLastLoginTime());
        return data;
    }
}
