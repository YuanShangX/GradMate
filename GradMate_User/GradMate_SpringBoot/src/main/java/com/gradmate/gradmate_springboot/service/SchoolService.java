package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.dto.SchoolQueryRequest;
import com.gradmate.gradmate_springboot.entity.School;
import com.gradmate.gradmate_springboot.entity.SchoolCharacter;
import com.gradmate.gradmate_springboot.mapper.SchoolCharacterMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学校服务类
 */
@Service
public class SchoolService {

    @Autowired
    private SchoolMapper schoolMapper;
    
    @Autowired
    private SchoolCharacterMapper schoolCharacterMapper;

    /**
     * 分页查询学校列表（支持多条件筛选）
     */
    public Page<School> listSchools(SchoolQueryRequest request) {
        Page<School> page = new Page<>(request.getPageNum(), request.getPageSize());
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        
        // 学校名称模糊查询
        if (request.getSchoolName() != null && !request.getSchoolName().trim().isEmpty()) {
            queryWrapper.like("school_name", request.getSchoolName().trim());
        }
        
        // 所在地区筛选
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            queryWrapper.eq("location", request.getLocation().trim());
        }
        
        // 主管部门筛选
        if (request.getSubordinate() != null && !request.getSubordinate().trim().isEmpty()) {
            queryWrapper.eq("subordinate", request.getSubordinate().trim());
        }
        
        // 如果有标签筛选，需要关联查询
        if (request.getYjsy() != null || request.getSyl() != null || request.getZhx() != null) {
            // 先查询符合特性条件的学校ID
            QueryWrapper<SchoolCharacter> characterWrapper = new QueryWrapper<>();
            
            if (request.getSyl() != null) {
                characterWrapper.eq("syl", request.getSyl());
            }
            if (request.getYjsy() != null) {
                characterWrapper.eq("yjsy", request.getYjsy());
            }
            if (request.getZhx() != null) {
                characterWrapper.eq("zhx", request.getZhx());
            }
            
            List<SchoolCharacter> characters = schoolCharacterMapper.selectList(characterWrapper);
            if (characters.isEmpty()) {
                // 如果没有符合条件的学校特性，返回空结果
                return new Page<>(request.getPageNum(), request.getPageSize());
            }
            
            List<Long> schoolIds = characters.stream()
                    .map(SchoolCharacter::getSchoolId)
                    .collect(Collectors.toList());
            queryWrapper.in("id", schoolIds);
        }
        
        // 按学校名称排序
        queryWrapper.orderByAsc("school_name");
        
        Page<School> result = schoolMapper.selectPage(page, queryWrapper);
        
        // 为每个学校填充特性信息
        for (School school : result.getRecords()) {
            SchoolCharacter character = schoolCharacterMapper.selectById(school.getId());
            if (character != null) {
                school.setSyl(character.getSyl());
                school.setYjsy(character.getYjsy());
                school.setZhx(character.getZhx());
            }
        }
        
        return result;
    }

    /**
     * 获取学校详情（包含特性信息）
     */
    public Map<String, Object> getSchoolDetail(Long schoolId) {
        School school = schoolMapper.selectById(schoolId);
        if (school == null) {
            return null;
        }
        
        SchoolCharacter character = schoolCharacterMapper.selectById(schoolId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("school", school);
        result.put("character", character);
        // 注意：数据库中没有单独的985字段
        // yjsy字段（研究生院）用来表示211
        // syl字段表示双一流
        result.put("is985", false);
        result.put("is211", character != null && character.getYjsy() != null && character.getYjsy() == 1);
        result.put("isSyl", character != null && character.getSyl() != null && character.getSyl() == 1);
        result.put("isZhx", character != null && character.getZhx() != null && character.getZhx() == 1);
        
        return result;
    }

    /**
     * 获取所有省份列表（去重）
     */
    public List<String> getProvinces() {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT location");
        queryWrapper.isNotNull("location");
        queryWrapper.ne("location", "");
        queryWrapper.orderByAsc("location");
        
        List<School> schools = schoolMapper.selectList(queryWrapper);
        return schools.stream()
                .map(School::getLocation)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取所有主管部门列表（去重）
     */
    public List<String> getSubordinates() {
        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT subordinate");
        queryWrapper.isNotNull("subordinate");
        queryWrapper.ne("subordinate", "");
        queryWrapper.orderByAsc("subordinate");
        
        List<School> schools = schoolMapper.selectList(queryWrapper);
        return schools.stream()
                .map(School::getSubordinate)
                .distinct()
                .collect(Collectors.toList());
    }
}