package com.gradmate.gradmate_springboot.controller;

import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.dto.LoginRequest;
import com.gradmate.gradmate_springboot.entity.User;
import com.gradmate.gradmate_springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录（账号密码方式）
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            return Result.success("登录成功", user);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            String nickname = params.get("nickname");
            
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = username; // 默认使用用户名作为昵称
            }
            
            User user = userService.register(username, password, nickname);
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 微信登录（保留兼容）
     */
    @PostMapping("/login/wechat")
    public Result<User> loginByWechat(@RequestBody LoginRequest request) {
        try {
            String openid = request.getCode();
            if (openid == null || openid.trim().isEmpty()) {
                return Result.error("登录参数不能为空");
            }
            
            User user = userService.loginByWechat(openid, request.getNickname(), request.getAvatar());
            
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                user.setPhone(request.getPhone());
                userService.updateUser(user);
            }
            
            return Result.success("登录成功", user);
        } catch (Exception e) {
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUser(@RequestBody User user) {
        try {
            if (user.getId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            // 获取原用户信息
            User existUser = userService.getUserById(user.getId());
            if (existUser == null) {
                return Result.error("用户不存在");
            }
            
            // 只更新允许修改的字段
            if (user.getNickname() != null) {
                existUser.setNickname(user.getNickname());
            }
            if (user.getAvatar() != null) {
                existUser.setAvatar(user.getAvatar());
            }
            if (user.getPhone() != null) {
                existUser.setPhone(user.getPhone());
            }
            if (user.getTargetSchool() != null) {
                existUser.setTargetSchool(user.getTargetSchool());
            }
            if (user.getTargetSchoolId() != null) {
                existUser.setTargetSchoolId(user.getTargetSchoolId());
            }
            if (user.getTargetMajor() != null) {
                existUser.setTargetMajor(user.getTargetMajor());
            }
            if (user.getTargetMajorId() != null) {
                existUser.setTargetMajorId(user.getTargetMajorId());
            }
            
            boolean success = userService.updateUser(existUser);
            if (success) {
                return Result.success("更新成功", existUser);
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新用户信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新目标设定
     */
    @PutMapping("/target")
    public Result<String> updateTarget(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 更新目标院校
            if (params.containsKey("targetSchoolId")) {
                user.setTargetSchoolId(Long.valueOf(params.get("targetSchoolId").toString()));
            }
            if (params.containsKey("targetSchool")) {
                user.setTargetSchool(params.get("targetSchool").toString());
            }
            
            // 更新目标专业
            if (params.containsKey("targetMajorId")) {
                user.setTargetMajorId(Long.valueOf(params.get("targetMajorId").toString()));
            }
            if (params.containsKey("targetMajor")) {
                user.setTargetMajor(params.get("targetMajor").toString());
            }
            
            boolean success = userService.updateUser(user);
            if (success) {
                return Result.success("目标设定成功");
            } else {
                return Result.error("目标设定失败");
            }
        } catch (Exception e) {
            return Result.error("目标设定失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取目标设定
     */
    @GetMapping("/target/{userId}")
    public Result<Map<String, Object>> getTarget(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("targetSchoolId", user.getTargetSchoolId());
            result.put("targetSchool", user.getTargetSchool());
            result.put("targetMajorId", user.getTargetMajorId());
            result.put("targetMajor", user.getTargetMajor());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取目标设定失败：" + e.getMessage());
        }
    }


    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats/{userId}")
    public Result<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            Map<String, Object> stats = new HashMap<>();

            // 基础信息
            stats.put("userId", userId);
            stats.put("nickname", user.getNickname());

            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取目标详细信息
     */
    @GetMapping("/target/detail/{userId}")
    public Result<Map<String, Object>> getTargetDetail(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            Map<String, Object> result = new HashMap<>();

            // 基础目标信息
            if (user.getTargetSchoolId() != null || user.getTargetSchool() != null) {
                Map<String, Object> schoolInfo = new HashMap<>();
                schoolInfo.put("id", user.getTargetSchoolId());
                schoolInfo.put("name", user.getTargetSchool());
                result.put("targetSchool", schoolInfo);
            }

            if (user.getTargetMajorId() != null || user.getTargetMajor() != null) {
                Map<String, Object> majorInfo = new HashMap<>();
                majorInfo.put("id", user.getTargetMajorId());
                majorInfo.put("name", user.getTargetMajor());
                result.put("targetMajor", majorInfo);
            }

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取目标详情失败：" + e.getMessage());
        }
    }

}
