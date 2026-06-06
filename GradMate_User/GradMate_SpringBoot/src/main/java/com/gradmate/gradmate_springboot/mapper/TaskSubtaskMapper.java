package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.TaskSubtask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 子任务 Mapper
 */
@Mapper
public interface TaskSubtaskMapper extends BaseMapper<TaskSubtask> {
}
