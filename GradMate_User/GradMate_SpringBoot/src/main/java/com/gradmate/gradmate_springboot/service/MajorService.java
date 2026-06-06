package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.dto.MajorQueryRequest;
import com.gradmate.gradmate_springboot.dto.MajorSchoolResponse;
import com.gradmate.gradmate_springboot.entity.Major;
import com.gradmate.gradmate_springboot.entity.MajorCategory;
import com.gradmate.gradmate_springboot.entity.School;
import com.gradmate.gradmate_springboot.entity.SchoolCharacter;
import com.gradmate.gradmate_springboot.mapper.MajorMapper;
import com.gradmate.gradmate_springboot.mapper.MajorCategoryMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolCharacterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 专业服务类
 */
@Service
public class MajorService {

    @Autowired
    private MajorMapper majorMapper;
    
    @Autowired
    private MajorCategoryMapper majorCategoryMapper;
    
    @Autowired
    private SchoolMapper schoolMapper;
    
    @Autowired
    private SchoolCharacterMapper schoolCharacterMapper;

    /**
     * 分页查询专业列表（支持多条件搜索）
     */
    public Page<Major> searchMajors(MajorQueryRequest request) {
        Page<Major> page = new Page<>(request.getPageNum(), request.getPageSize());
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        
        // 学校名称模糊查询
        if (request.getSchoolName() != null && !request.getSchoolName().trim().isEmpty()) {
            queryWrapper.like("school_name", request.getSchoolName().trim());
        }
        
        // 专业名称模糊查询
        if (request.getMajorName() != null && !request.getMajorName().trim().isEmpty()) {
            queryWrapper.like("major_name", request.getMajorName().trim());
        }
        
        // 省份筛选
        if (request.getProvince() != null && !request.getProvince().trim().isEmpty()) {
            queryWrapper.eq("province", request.getProvince().trim());
        }
        
        // 学位类型筛选
        if (request.getDegreeType() != null && !request.getDegreeType().trim().isEmpty()) {
            queryWrapper.eq("degree_type", request.getDegreeType().trim());
        }
        
        // 考试方式筛选
        if (request.getExamType() != null && !request.getExamType().trim().isEmpty()) {
            queryWrapper.eq("exam_type", request.getExamType().trim());
        }
        
        // 学习方式筛选
        if (request.getStudyMode() != null && !request.getStudyMode().trim().isEmpty()) {
            queryWrapper.eq("study_mode", request.getStudyMode().trim());
        }
        
        // 院系所筛选
        if (request.getDepartment() != null && !request.getDepartment().trim().isEmpty()) {
            queryWrapper.like("department", request.getDepartment().trim());
        }
        
        // 按学校名称、专业名称排序
        queryWrapper.orderByAsc("school_name", "major_name");
        
        return majorMapper.selectPage(page, queryWrapper);
    }

    /**
     * 获取专业详情
     */
    public Major getMajorDetail(Integer majorId) {
        return majorMapper.selectById(majorId);
    }

    /**
     * 根据学校名称查询专业列表
     */
    public Page<Major> getMajorsBySchool(String schoolName, Integer pageNum, Integer pageSize) {
        Page<Major> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_name", schoolName);
        queryWrapper.orderByAsc("major_name");
        
        return majorMapper.selectPage(page, queryWrapper);
    }

    /**
     * 获取所有学位类型列表（去重）
     */
    public List<String> getDegreeTypes() {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT degree_type");
        queryWrapper.isNotNull("degree_type");
        queryWrapper.ne("degree_type", "");
        queryWrapper.orderByAsc("degree_type");
        
        List<Major> majors = majorMapper.selectList(queryWrapper);
        return majors.stream()
                .map(Major::getDegreeType)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取所有考试方式列表（去重）
     */
    public List<String> getExamTypes() {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT exam_type");
        queryWrapper.isNotNull("exam_type");
        queryWrapper.ne("exam_type", "");
        queryWrapper.orderByAsc("exam_type");
        
        List<Major> majors = majorMapper.selectList(queryWrapper);
        return majors.stream()
                .map(Major::getExamType)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取所有学习方式列表（去重）
     */
    public List<String> getStudyModes() {
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT study_mode");
        queryWrapper.isNotNull("study_mode");
        queryWrapper.ne("study_mode", "");
        queryWrapper.orderByAsc("study_mode");
        
        List<Major> majors = majorMapper.selectList(queryWrapper);
        return majors.stream()
                .map(Major::getStudyMode)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 根据学科门类获取一级学科和专业列表
     */
    public List<com.gradmate.gradmate_springboot.dto.DisciplineResponse> getDisciplinesByCategory(String categoryCode) {
        // 查询该学科门类下的所有专业（按专业代码排序）
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT major_code", "major_name");
        queryWrapper.isNotNull("major_code");
        queryWrapper.ne("major_code", "");
        queryWrapper.likeRight("major_code", categoryCode); // 专业代码以学科门类代码开头
        queryWrapper.orderByAsc("major_code", "major_name");
        
        List<Major> majors = majorMapper.selectList(queryWrapper);
        
        // 按一级学科（前4位）分组
        java.util.Map<String, List<Major>> disciplineMap = new java.util.LinkedHashMap<>();
        for (Major major : majors) {
            String code = major.getMajorCode();
            if (code != null && code.length() >= 4) {
                String disciplineCode = code.substring(0, 4);
                disciplineMap.computeIfAbsent(disciplineCode, k -> new java.util.ArrayList<>()).add(major);
            }
        }
        
        // 构建返回结果
        List<com.gradmate.gradmate_springboot.dto.DisciplineResponse> result = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, List<Major>> entry : disciplineMap.entrySet()) {
            String disciplineCode = entry.getKey();
            List<Major> disciplineMajors = entry.getValue();
            
            com.gradmate.gradmate_springboot.dto.DisciplineResponse discipline = 
                new com.gradmate.gradmate_springboot.dto.DisciplineResponse();
            discipline.setCode(disciplineCode);
            
            // 找到一级学科名称（代码长度为4或6位且以00结尾的）
            String disciplineName = "";
            for (Major m : disciplineMajors) {
                String code = m.getMajorCode();
                if (code.equals(disciplineCode + "00") || code.equals(disciplineCode)) {
                    disciplineName = m.getMajorName();
                    break;
                }
            }
            discipline.setName(disciplineName);
            
            // 添加该一级学科下的所有专业（包括代码相同但名称不同的）
            List<com.gradmate.gradmate_springboot.dto.DisciplineResponse.MajorInfo> majorInfos = 
                new java.util.ArrayList<>();
            for (Major m : disciplineMajors) {
                String code = m.getMajorCode();
                // 排除一级学科本身（4位代码或6位以00结尾）
                if (code.length() > 4 && !code.equals(disciplineCode + "00")) {
                    com.gradmate.gradmate_springboot.dto.DisciplineResponse.MajorInfo info = 
                        new com.gradmate.gradmate_springboot.dto.DisciplineResponse.MajorInfo();
                    info.setCode(code);
                    info.setName(m.getMajorName());
                    majorInfos.add(info);
                }
            }
            discipline.setMajors(majorInfos);
            
            // 只添加有具体专业的一级学科
            if (!majorInfos.isEmpty()) {
                result.add(discipline);
            }
        }
        
        return result;
    }
    
    /**
     * 从专业类型表获取学科门类下的一级学科和专业列表（支持学硕/专硕筛选）
     */
    public List<com.gradmate.gradmate_springboot.dto.DisciplineResponse> getDisciplinesByCategoryFromCategoryTable(
            String categoryCode, String degreeType) {
        
        // 查询该学科门类下的所有专业
        QueryWrapper<MajorCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_code", categoryCode);
        
        // 如果指定了学位类型，则筛选
        if (degreeType != null && !degreeType.trim().isEmpty()) {
            queryWrapper.eq("degree_type", degreeType);
        }
        
        queryWrapper.orderByAsc("discipline_code", "sort_order");
        
        List<MajorCategory> categories = majorCategoryMapper.selectList(queryWrapper);
        
        // 按一级学科分组
        java.util.Map<String, List<MajorCategory>> disciplineMap = new java.util.LinkedHashMap<>();
        for (MajorCategory category : categories) {
            String disciplineCode = category.getDisciplineCode();
            disciplineMap.computeIfAbsent(disciplineCode, k -> new java.util.ArrayList<>()).add(category);
        }
        
        // 构建返回结果
        List<com.gradmate.gradmate_springboot.dto.DisciplineResponse> result = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, List<MajorCategory>> entry : disciplineMap.entrySet()) {
            String disciplineCode = entry.getKey();
            List<MajorCategory> disciplineCategories = entry.getValue();
            
            if (disciplineCategories.isEmpty()) {
                continue;
            }
            
            com.gradmate.gradmate_springboot.dto.DisciplineResponse discipline = 
                new com.gradmate.gradmate_springboot.dto.DisciplineResponse();
            discipline.setCode(disciplineCode);
            discipline.setName(disciplineCategories.get(0).getDisciplineName());
            
            // 添加该一级学科下的所有专业
            List<com.gradmate.gradmate_springboot.dto.DisciplineResponse.MajorInfo> majorInfos = 
                new java.util.ArrayList<>();
            for (MajorCategory mc : disciplineCategories) {
                com.gradmate.gradmate_springboot.dto.DisciplineResponse.MajorInfo info = 
                    new com.gradmate.gradmate_springboot.dto.DisciplineResponse.MajorInfo();
                info.setCode(mc.getMajorCode());
                info.setName(mc.getMajorName());
                majorInfos.add(info);
            }
            discipline.setMajors(majorInfos);
            
            result.add(discipline);
        }
        
        return result;
    }
    
    /**
     * 根据专业代码查询开设该专业的学校列表
     */
    public List<MajorSchoolResponse> getSchoolsByMajorCode(String majorCode, String location, String schoolType) {
        // 查询该专业代码对应的所有学校
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("major_code", majorCode);
        
        // 地区筛选
        if (location != null && !location.trim().isEmpty()) {
            queryWrapper.eq("province", location);
        }
        
        queryWrapper.orderByAsc("school_name");
        
        List<Major> majors = majorMapper.selectList(queryWrapper);
        
        // 按学校名称去重，保留第一条记录
        Map<String, Major> schoolMajorMap = new java.util.LinkedHashMap<>();
        for (Major major : majors) {
            String schoolName = major.getSchoolName();
            if (!schoolMajorMap.containsKey(schoolName)) {
                schoolMajorMap.put(schoolName, major);
            }
        }
        
        // 查询所有学校的详细信息
        List<String> schoolNames = new java.util.ArrayList<>(schoolMajorMap.keySet());
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.in("school_name", schoolNames);
        List<School> schools = schoolMapper.selectList(schoolQueryWrapper);
        Map<String, School> schoolMap = schools.stream()
                .collect(Collectors.toMap(School::getSchoolName, s -> s, (s1, s2) -> s1));
        
        // 查询所有学校的特性信息（通过school_id关联）
        // 先获取所有学校的ID
        List<Long> schoolIds = schools.stream()
                .map(School::getId)
                .collect(Collectors.toList());
        
        Map<Long, SchoolCharacter> characterMap = new java.util.HashMap<>();
        if (!schoolIds.isEmpty()) {
            QueryWrapper<SchoolCharacter> characterQueryWrapper = new QueryWrapper<>();
            characterQueryWrapper.in("school_id", schoolIds);
            List<SchoolCharacter> characters = schoolCharacterMapper.selectList(characterQueryWrapper);
            characterMap = characters.stream()
                    .collect(Collectors.toMap(SchoolCharacter::getSchoolId, c -> c, (c1, c2) -> c1));
        }
        
        // 构建返回结果
        List<MajorSchoolResponse> result = new java.util.ArrayList<>();
        for (Map.Entry<String, Major> entry : schoolMajorMap.entrySet()) {
            String schoolName = entry.getKey();
            Major major = entry.getValue();
            School school = schoolMap.get(schoolName);
            SchoolCharacter character = school != null ? characterMap.get(school.getId()) : null;
            
            MajorSchoolResponse response = new MajorSchoolResponse();
            response.setSchoolName(schoolName);
            response.setDepartment(major.getDepartment());
            response.setStudyMode(major.getStudyMode());
            response.setExamType(major.getExamType());
            response.setPlannedEnrollment(major.getPlannedEnrollment());
            
            // 设置学校信息
            if (school != null) {
                response.setLogo(school.getLogo());
                response.setLocation(school.getLocation());
            } else {
                response.setLocation(major.getProvince());
            }
            
            // 设置学校特性
            if (character != null) {
                response.setIs985(character.getYjsy() != null && character.getYjsy() == 1);
                response.setIs211(character.getYjsy() != null && character.getYjsy() == 1);
                response.setIsSyl(character.getSyl() != null && character.getSyl() == 1);
                response.setIsZhx(character.getZhx() != null && character.getZhx() == 1);
            } else {
                response.setIs985(false);
                response.setIs211(false);
                response.setIsSyl(false);
                response.setIsZhx(false);
            }
            
            // 模拟数据
            response.setFollowCount((int)(Math.random() * 200) + 20);
            response.setViewCount((int)(Math.random() * 5000) + 1000);
            response.setAdmissionScore(String.valueOf((int)(Math.random() * 100) + 300));
            
            result.add(response);
        }
        
        // 根据学校类型筛选
        if (schoolType != null && !schoolType.trim().isEmpty()) {
            result = result.stream().filter(r -> {
                switch (schoolType) {
                    case "985":
                        return r.getIs985();
                    case "211":
                        return r.getIs211();
                    case "syl":
                        return r.getIsSyl();
                    case "zhx":
                        return r.getIsZhx();
                    default:
                        return true;
                }
            }).collect(Collectors.toList());
        }
        
        return result;
    }


    /**
     * 查询某个学校某个专业的详细招生信息
     * @param schoolName 学校名称（必填）
     * @param majorCode 专业代码（可选，优先使用）
     * @param majorName 专业名称（可选，当 majorCode 为空时使用）
     */
    public List<com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse> getSchoolMajorDetail(
            String schoolName, String majorCode, String majorName) {
        // 查询专业信息
        QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_name", schoolName);
        
        // 优先使用 majorCode 精确匹配
        if (majorCode != null && !majorCode.trim().isEmpty()) {
            queryWrapper.eq("major_code", majorCode);
        } 
        // 如果没有 majorCode，使用 majorName 模糊匹配
        else if (majorName != null && !majorName.trim().isEmpty()) {
            queryWrapper.like("major_name", majorName);
        }
        
        queryWrapper.orderByAsc("department", "research_direction");
        
        List<Major> majors = majorMapper.selectList(queryWrapper);
        
        // 查询学校logo
        String schoolLogo = null;
        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
        schoolQueryWrapper.eq("school_name", schoolName);
        School school = schoolMapper.selectOne(schoolQueryWrapper);
        if (school != null) {
            schoolLogo = school.getLogo();
        }
        
        // 构建返回结果
        List<com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse> result = new java.util.ArrayList<>();
        for (Major major : majors) {
            com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse response = 
                new com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse();
            response.setSchoolLogo(schoolLogo);
            response.setSchoolName(major.getSchoolName());
            response.setMajorCode(major.getMajorCode());
            response.setMajorName(major.getMajorName());
            response.setDepartment(major.getDepartment());
            response.setResearchDirection(major.getResearchDirection());
            response.setStudyMode(major.getStudyMode());
            response.setExamType(major.getExamType());
            response.setPlannedEnrollment(major.getPlannedEnrollment());
            response.setExamSubject1(major.getExamSubject1());
            response.setExamSubject2(major.getExamSubject2());
            response.setExamSubject3(major.getExamSubject3());
            response.setExamSubject4(major.getExamSubject4());
            response.setAdvisor(major.getAdvisor());
            result.add(response);
        }
        
        return result;
    }
    
    /**
     * 查询某个学校某个专业的详细招生信息（向后兼容的重载方法）
     */
    public List<com.gradmate.gradmate_springboot.dto.SchoolMajorDetailResponse> getSchoolMajorDetail(
            String schoolName, String majorCode) {
        return getSchoolMajorDetail(schoolName, majorCode, null);
    }
}