package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.MajorCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专业类型Mapper
 */
@Mapper
public interface MajorCategoryMapper extends BaseMapper<MajorCategory> {
}
