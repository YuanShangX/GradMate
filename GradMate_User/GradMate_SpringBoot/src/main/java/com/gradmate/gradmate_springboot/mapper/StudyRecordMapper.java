package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.StudyRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡记录 Mapper
 */
@Mapper
public interface StudyRecordMapper extends BaseMapper<StudyRecord> {
}