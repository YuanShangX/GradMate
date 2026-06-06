package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.entity.UserFavorite;
import com.gradmate.gradmate_springboot.mapper.UserFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 收藏服务类
 */
@Service
public class FavoriteService {
    
    @Autowired
    private UserFavoriteMapper favoriteMapper;
    
    /**
     * 添加收藏（使用学校名称和专业代码）
     */
    public boolean addFavoriteBySchoolAndMajor(Long userId, String schoolName, String majorCode, String majorName) {
        // 使用学校名称和专业代码的哈希值作为targetId
        String uniqueKey = schoolName + "-" + majorCode;
        Long targetId = (long) uniqueKey.hashCode();
        String targetName = schoolName + " - " + majorName;
        
        // 检查是否已收藏
        if (isFavorited(userId, "major", targetId)) {
            return false;
        }
        
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setType("major");
        favorite.setTargetId(targetId);
        favorite.setTargetName(targetName);
        
        // 存储元数据，包含学校名称和专业代码
        String metadata = String.format("{\"schoolName\":\"%s\",\"majorCode\":\"%s\",\"majorName\":\"%s\"}", 
                                       schoolName, majorCode, majorName);
        favorite.setMetadata(metadata);
        
        return favoriteMapper.insert(favorite) > 0;
    }
    
    /**
     * 取消收藏（使用学校名称和专业代码）
     */
    public boolean removeFavoriteBySchoolAndMajor(Long userId, String schoolName, String majorCode) {
        String uniqueKey = schoolName + "-" + majorCode;
        Long targetId = (long) uniqueKey.hashCode();
        
        return removeFavorite(userId, "major", targetId);
    }
    
    /**
     * 检查是否已收藏（使用学校名称和专业代码）
     */
    public boolean isFavoritedBySchoolAndMajor(Long userId, String schoolName, String majorCode) {
        String uniqueKey = schoolName + "-" + majorCode;
        Long targetId = (long) uniqueKey.hashCode();
        
        return isFavorited(userId, "major", targetId);
    }
    
    /**
     * 添加收藏
     */
    public boolean addFavorite(Long userId, String type, Long targetId, String targetName) {
        // 检查是否已收藏
        if (isFavorited(userId, type, targetId)) {
            return false;
        }
        
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setType(type);
        favorite.setTargetId(targetId);
        favorite.setTargetName(targetName);
        
        return favoriteMapper.insert(favorite) > 0;
    }
    
    /**
     * 取消收藏
     */
    public boolean removeFavorite(Long userId, String type, Long targetId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("type", type)
               .eq("target_id", targetId);
        
        return favoriteMapper.delete(wrapper) > 0;
    }
    
    /**
     * 查询收藏列表(分页)
     */
    public Page<UserFavorite> listFavorites(Long userId, String type, int pageNum, int pageSize) {
        Page<UserFavorite> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        
        wrapper.orderByDesc("create_time");
        
        return favoriteMapper.selectPage(page, wrapper);
    }
    
    /**
     * 判断是否已收藏
     */
    public boolean isFavorited(Long userId, String type, Long targetId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("type", type)
               .eq("target_id", targetId);
        
        return favoriteMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 获取收藏总数
     */
    public long getFavoriteCount(Long userId) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        return favoriteMapper.selectCount(wrapper);
    }
    
    /**
     * 获取指定类型的收藏数量
     */
    public long getFavoriteCountByType(Long userId, String type) {
        QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("type", type);
        
        return favoriteMapper.selectCount(wrapper);
    }
}
