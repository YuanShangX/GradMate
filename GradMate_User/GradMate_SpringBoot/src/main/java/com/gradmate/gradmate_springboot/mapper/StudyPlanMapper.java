package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.StudyPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习计划 Mapper
 */
@Mapper
public interface StudyPlanMapper extends BaseMapper<StudyPlan> {
}