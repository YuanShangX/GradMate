package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.TaskTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务标签 Mapper
 */
@Mapper
public interface TaskTagMapper extends BaseMapper<TaskTag> {
}
