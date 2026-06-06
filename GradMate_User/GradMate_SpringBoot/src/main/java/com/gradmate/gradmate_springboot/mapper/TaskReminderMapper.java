package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.TaskReminder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务提醒 Mapper
 */
@Mapper
public interface TaskReminderMapper extends BaseMapper<TaskReminder> {
}
