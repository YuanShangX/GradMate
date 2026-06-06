package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.entity.UserHistory;
import com.gradmate.gradmate_springboot.mapper.UserHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 浏览历史服务类
 */
@Service
public class HistoryService {
    
    @Autowired
    private UserHistoryMapper historyMapper;
    
    /**
     * 添加浏览记录
     */
    public boolean addHistory(Long userId, String type, Long targetId, String targetName) {
        return addHistory(userId, type, targetId, targetName, null);
    }
    
    /**
     * 添加浏览记录（带元数据）
     */
    public boolean addHistory(Long userId, String type, Long targetId, String targetName, String metadata) {
        UserHistory history = new UserHistory();
        history.setUserId(userId);
        history.setType(type);
        history.setTargetId(targetId);
        history.setTargetName(targetName);
        history.setMetadata(metadata);
        
        return historyMapper.insert(history) > 0;
    }
    
    /**
     * 查询浏览历史(分页)
     */
    public Page<UserHistory> listHistory(Long userId, String type, int pageNum, int pageSize) {
        Page<UserHistory> page = new Page<>(pageNum, pageSize);
        QueryWrapper<UserHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        
        wrapper.orderByDesc("view_time");
        
        return historyMapper.selectPage(page, wrapper);
    }
    
    /**
     * 清空浏览历史
     */
    public boolean clearHistory(Long userId) {
        QueryWrapper<UserHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        return historyMapper.delete(wrapper) > 0;
    }
    
    /**
     * 删除单条历史记录
     */
    public boolean deleteHistory(Long id) {
        return historyMapper.deleteById(id) > 0;
    }
    
    /**
     * 获取历史记录总数
     */
    public long getHistoryCount(Long userId) {
        QueryWrapper<UserHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        return historyMapper.selectCount(wrapper);
    }
}
