package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.School;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学校Mapper接口
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
}
