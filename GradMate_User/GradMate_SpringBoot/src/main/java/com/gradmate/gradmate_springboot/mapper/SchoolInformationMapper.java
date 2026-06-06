package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.SchoolInformation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 招生通知 Mapper
 */
@Mapper
public interface SchoolInformationMapper extends BaseMapper<SchoolInformation> {
}
