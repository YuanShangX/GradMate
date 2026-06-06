package com.gradmate.gradmate_springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gradmate.gradmate_springboot.entity.SchoolCharacter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学校特性Mapper接口
 */
@Mapper
public interface SchoolCharacterMapper extends BaseMapper<SchoolCharacter> {
}