package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.UserHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户浏览历史Mapper接口
 */
@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {
}
