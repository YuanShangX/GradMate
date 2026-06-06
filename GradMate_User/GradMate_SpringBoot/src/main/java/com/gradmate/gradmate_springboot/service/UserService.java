package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gradmate.gradmate_springboot.entity.User;
import com.gradmate.gradmate_springboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务类
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名查询用户
     */
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 用户注册
     */
    public User register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        User existUser = getUserByUsername(username);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 明文存储
        user.setNickname(nickname);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        return user;
    }

    /**
     * 用户登录（账号密码方式）
     */
    public User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    /**
     * 根据openid查询用户
     */
    public User getUserByOpenid(String openid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 创建新用户（微信登录方式，保留兼容）
     */
    public User createUser(String openid, String nickname, String avatar) {
        User user = new User();
        user.setOpenid(openid);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        return user;
    }

    /**
     * 微信登录（保留兼容，根据openid查询或创建用户）
     */
    public User loginByWechat(String openid, String nickname, String avatar) {
        User user = getUserByOpenid(openid);
        if (user == null) {
            // 用户不存在，创建新用户
            user = createUser(openid, nickname, avatar);
        } else {
            // 用户存在，更新昵称和头像
            user.setNickname(nickname);
            user.setAvatar(avatar);
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);
        }
        return user;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
}