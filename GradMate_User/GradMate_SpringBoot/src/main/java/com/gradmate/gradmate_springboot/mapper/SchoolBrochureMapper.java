package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.SchoolBrochure;
import org.apache.ibatis.annotations.Mapper;

/**
 * 招生简章 Mapper
 */
@Mapper
public interface SchoolBrochureMapper extends BaseMapper<SchoolBrochure> {
}
