package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gradmate.gradmate_springboot.entity.Major;
import com.gradmate.gradmate_springboot.entity.School;
import com.gradmate.gradmate_springboot.mapper.MajorMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 对比服务
 */
@Service
public class CompareService {
    
    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private MajorService majorService;
    
    @Autowired
    private SchoolMapper schoolMapper;
    
    @Autowired
    private MajorMapper majorMapper;
    
    @Autowired
    private GradMateAgentService agentService;
    
    /**
     * 对比院校
     * @param schoolIds 院校ID列表（2-3个）
     * @return 对比结果
     */
    public Map<String, Object> compareSchools(List<Long> schoolIds) {
        Map<String, Object> result = new HashMap<>();
        
        // 参数校验
        if (schoolIds == null || schoolIds.size() < 2 || schoolIds.size() > 3) {
            throw new RuntimeException("请选择2-3个院校进行对比");
        }
        
        // 获取院校详细信息
        List<Map<String, Object>> schools = new ArrayList<>();
        for (Long schoolId : schoolIds) {
            Map<String, Object> schoolDetail = schoolService.getSchoolDetail(schoolId);
            if (schoolDetail != null) {
                // 转换为扁平化的Map
                Map<String, Object> flatSchool = flattenSchoolDetail(schoolDetail);
                schools.add(flatSchool);
            }
        }
        
        result.put("schools", schools);
        
        // 生成AI对比分析
        String aiAnalysis = generateSchoolCompareAnalysis(schools);
        result.put("analysis", aiAnalysis);
        
        return result;
    }
    
    /**
     * 将SchoolService返回的嵌套Map转换为扁平化Map
     */
    private Map<String, Object> flattenSchoolDetail(Map<String, Object> schoolDetail) {
        Map<String, Object> flat = new HashMap<>();
        
        // 提取School对象的字段
        Object schoolObj = schoolDetail.get("school");
        if (schoolObj instanceof School) {
            School school = (School) schoolObj;
            flat.put("id", school.getId());
            flat.put("schoolName", school.getSchoolName());
            flat.put("logo", school.getLogo());
            flat.put("location", school.getLocation());
            flat.put("subordinate", school.getSubordinate());
            flat.put("link", school.getLink());
        }
        
        // 提取特征字段
        flat.put("is985", schoolDetail.get("is985"));
        flat.put("is211", schoolDetail.get("is211"));
        flat.put("isSyl", schoolDetail.get("isSyl"));
        flat.put("isZhx", schoolDetail.get("isZhx"));
        
        // 统计专业数量
        if (flat.get("schoolName") != null) {
            QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("school_name", flat.get("schoolName"));
            long majorCount = majorMapper.selectCount(queryWrapper);
            flat.put("majorCount", (int) majorCount);
        }
        
        return flat;
    }
    
    /**
     * 对比专业
     * @param majorIds 专业ID列表（2-3个）
     * @return 对比结果
     */
    public Map<String, Object> compareMajors(List<Long> majorIds) {
        Map<String, Object> result = new HashMap<>();
        
        // 参数校验
        if (majorIds == null || majorIds.size() < 2 || majorIds.size() > 3) {
            throw new RuntimeException("请选择2-3个专业进行对比");
        }
        
        // 获取专业详细信息
        List<Map<String, Object>> majors = new ArrayList<>();
        for (Long majorId : majorIds) {
            Major major = majorService.getMajorDetail(majorId.intValue());
            if (major != null) {
                Map<String, Object> majorMap = convertMajorToMap(major);
                majors.add(majorMap);
            }
        }
        
        result.put("majors", majors);
        
        // 生成AI对比分析
        String aiAnalysis = generateMajorCompareAnalysis(majors);
        result.put("analysis", aiAnalysis);
        
        return result;
    }
    
    /**
     * 将Major对象转换为Map
     */
    private Map<String, Object> convertMajorToMap(Major major) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", major.getId());
        map.put("schoolName", major.getSchoolName());
        map.put("majorName", major.getMajorName());
        map.put("province", major.getProvince());
        map.put("majorCode", major.getMajorCode());
        map.put("degreeType", major.getDegreeType());
        map.put("examType", major.getExamType());
        map.put("department", major.getDepartment());
        map.put("studyMode", major.getStudyMode());
        map.put("researchDirection", major.getResearchDirection());
        map.put("plannedEnrollment", major.getPlannedEnrollment());
        map.put("examSubject1", major.getExamSubject1());
        map.put("examSubject2", major.getExamSubject2());
        map.put("examSubject3", major.getExamSubject3());
        map.put("examSubject4", major.getExamSubject4());
        return map;
    }
    
    /**
     * 生成院校对比AI分析
     */
    private String generateSchoolCompareAnalysis(List<Map<String, Object>> schools) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为考研择校助手，请对比分析以下院校，给出简洁的对比分析（150字以内）：\n\n");
        
        for (int i = 0; i < schools.size(); i++) {
            Map<String, Object> school = schools.get(i);
            prompt.append(String.format("院校%d：%s\n", i + 1, school.get("schoolName")));
            prompt.append(String.format("- 地理位置：%s\n", school.get("location")));
            prompt.append(String.format("- 主管部门：%s\n", school.get("subordinate")));
            
            // 院校特征 - 安全地处理Boolean/Integer类型
            List<String> tags = new ArrayList<>();
            if (isTrue(school.get("isSyl"))) {
                tags.add("双一流");
            }
            if (isTrue(school.get("is211"))) {
                tags.add("211");
            }
            if (isTrue(school.get("isZhx"))) {
                tags.add("自划线");
            }
            if (!tags.isEmpty()) {
                prompt.append(String.format("- 院校特征：%s\n", String.join("、", tags)));
            }
            prompt.append("\n");
        }
        
        prompt.append("请从地理位置、院校层次、考研难度等维度对比，并给出择校建议。");
        
        try {
            return agentService.chat(1L, prompt.toString());
        } catch (Exception e) {
            return "AI分析暂时不可用，请根据上述信息自行对比分析。";
        }
    }
    
    /**
     * 安全地判断值是否为true
     * 支持Boolean、Integer、String类型
     */
    private boolean isTrue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Integer) {
            return (Integer) value == 1;
        }
        if (value instanceof String) {
            return "1".equals(value) || "true".equalsIgnoreCase((String) value);
        }
        return false;
    }
    
    /**
     * 生成专业对比AI分析
     */
    private String generateMajorCompareAnalysis(List<Map<String, Object>> majors) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为考研择校助手，请对比分析以下专业，给出简洁的对比分析（150字以内）：\n\n");
        
        for (int i = 0; i < majors.size(); i++) {
            Map<String, Object> major = majors.get(i);
            prompt.append(String.format("专业%d：%s - %s\n", i + 1, major.get("schoolName"), major.get("majorName")));
            prompt.append(String.format("- 学位类型：%s\n", major.get("degreeType")));
            prompt.append(String.format("- 学习方式：%s\n", major.get("studyMode")));
            prompt.append(String.format("- 拟招生人数：%s\n", major.get("plannedEnrollment")));
            
            // 考试科目
            List<String> subjects = new ArrayList<>();
            if (major.get("examSubject1") != null) subjects.add(major.get("examSubject1").toString());
            if (major.get("examSubject2") != null) subjects.add(major.get("examSubject2").toString());
            if (major.get("examSubject3") != null) subjects.add(major.get("examSubject3").toString());
            if (major.get("examSubject4") != null) subjects.add(major.get("examSubject4").toString());
            if (!subjects.isEmpty()) {
                prompt.append(String.format("- 考试科目：%s\n", String.join("、", subjects)));
            }
            prompt.append("\n");
        }
        
        prompt.append("请从考试难度、招生规模、就业前景等维度对比，并给出选择建议。");
        
        try {
            return agentService.chat(1L, prompt.toString());
        } catch (Exception e) {
            return "AI分析暂时不可用，请根据上述信息自行对比分析。";
        }
    }
}
