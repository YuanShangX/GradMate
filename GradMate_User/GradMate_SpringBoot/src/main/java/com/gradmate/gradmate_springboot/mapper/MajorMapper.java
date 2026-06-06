package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.Major;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专业Mapper接口
 */
@Mapper
public interface MajorMapper extends BaseMapper<Major> {
}