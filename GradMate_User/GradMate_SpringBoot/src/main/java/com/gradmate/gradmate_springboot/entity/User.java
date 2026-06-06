package com.gradmate.gradmate_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 登录账号
     */
    private String username;
    
    /**
     * 登录密码(明文)
     */
    private String password;
    
    /**
     * 微信openid(可选)
     */
    private String openid;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 目标院校
     */
    private String targetSchool;
    
    /**
     * 目标院校ID
     */
    private Long targetSchoolId;
    
    /**
     * 目标专业
     */
    private String targetMajor;
    
    /**
     * 目标专业ID
     */
    private Long targetMajorId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}