package com.gradmate.gradmate_springboot.dto;

import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    
    /**
     * 登录账号
     */
    private String username;
    
    /**
     * 登录密码
     */
    private String password;
    
    /**
     * 微信登录code（兼容旧版本）
     */
    private String code;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 手机号（可选）
     */
    private String phone;
}