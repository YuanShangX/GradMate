package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_springboot.dto.InfoResponse;
import com.gradmate.gradmate_springboot.entity.School;
import com.gradmate.gradmate_springboot.entity.SchoolAdjust;
import com.gradmate.gradmate_springboot.entity.SchoolBrochure;
import com.gradmate.gradmate_springboot.entity.SchoolInformation;
import com.gradmate.gradmate_springboot.mapper.SchoolAdjustMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolBrochureMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolInformationMapper;
import com.gradmate.gradmate_springboot.mapper.SchoolMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资讯服务类
 */
@Service
public class InfoService {
    
    @Autowired
    private SchoolBrochureMapper brochureMapper;
    
    @Autowired
    private SchoolAdjustMapper adjustMapper;
    
    @Autowired
    private SchoolInformationMapper informationMapper;
    
    @Autowired
    private SchoolMapper schoolMapper;
    
    // 缓存学校ID和名称的映射
    private Map<Long, String> schoolNameCache = new HashMap<>();
    
    /**
     * 获取学校名称（带缓存）
     */
    private String getSchoolName(Long schoolId) {
        if (schoolId == null) {
            return "未知院校";
        }
        
        if (schoolNameCache.containsKey(schoolId)) {
            return schoolNameCache.get(schoolId);
        }
        
        School school = schoolMapper.selectById(schoolId);
        String schoolName = school != null ? school.getSchoolName() : "未知院校";
        schoolNameCache.put(schoolId, schoolName);
        return schoolName;
    }
    
    /**
     * 查询招生简章列表
     */
    public Page<InfoResponse> listBrochures(Long schoolId, String schoolName, Integer pageNum, Integer pageSize) {
        Page<SchoolBrochure> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SchoolBrochure> wrapper = new QueryWrapper<>();
        
        // 按学校ID筛选
        if (schoolId != null) {
            wrapper.eq("school_id", schoolId);
        }
        
        // 按学校名称筛选（需要先查询学校ID）
        if (schoolName != null && !schoolName.trim().isEmpty()) {
            QueryWrapper<School> schoolWrapper = new QueryWrapper<>();
            schoolWrapper.like("school_name", schoolName);
            List<School> schools = schoolMapper.selectList(schoolWrapper);
            if (!schools.isEmpty()) {
                List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
                wrapper.in("school_id", schoolIds);
            } else {
                // 没有匹配的学校，返回空结果
                return new Page<>(pageNum, pageSize);
            }
        }
        
        // 按发布日期降序排列
        wrapper.orderByDesc("publish_date");
        
        Page<SchoolBrochure> brochurePage = brochureMapper.selectPage(page, wrapper);
        
        // 转换为 InfoResponse
        Page<InfoResponse> responsePage = new Page<>(pageNum, pageSize);
        responsePage.setTotal(brochurePage.getTotal());
        responsePage.setPages(brochurePage.getPages());
        
        List<InfoResponse> records = brochurePage.getRecords().stream().map(brochure -> {
            InfoResponse response = new InfoResponse();
            BeanUtils.copyProperties(brochure, response);
            response.setSchoolName(getSchoolName(brochure.getSchoolId()));
            response.setType("brochure");
            return response;
        }).collect(Collectors.toList());
        
        responsePage.setRecords(records);
        return responsePage;
    }
    
    /**
     * 查询调剂公告列表
     */
    public Page<InfoResponse> listAdjusts(Long schoolId, String schoolName, Integer pageNum, Integer pageSize) {
        Page<SchoolAdjust> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SchoolAdjust> wrapper = new QueryWrapper<>();
        
        // 按学校ID筛选
        if (schoolId != null) {
            wrapper.eq("school_id", schoolId);
        }
        
        // 按学校名称筛选
        if (schoolName != null && !schoolName.trim().isEmpty()) {
            QueryWrapper<School> schoolWrapper = new QueryWrapper<>();
            schoolWrapper.like("school_name", schoolName);
            List<School> schools = schoolMapper.selectList(schoolWrapper);
            if (!schools.isEmpty()) {
                List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
                wrapper.in("school_id", schoolIds);
            } else {
                return new Page<>(pageNum, pageSize);
            }
        }
        
        // 按发布日期降序排列
        wrapper.orderByDesc("publish_date");
        
        Page<SchoolAdjust> adjustPage = adjustMapper.selectPage(page, wrapper);
        
        // 转换为 InfoResponse
        Page<InfoResponse> responsePage = new Page<>(pageNum, pageSize);
        responsePage.setTotal(adjustPage.getTotal());
        responsePage.setPages(adjustPage.getPages());
        
        List<InfoResponse> records = adjustPage.getRecords().stream().map(adjust -> {
            InfoResponse response = new InfoResponse();
            BeanUtils.copyProperties(adjust, response);
            response.setSchoolName(getSchoolName(adjust.getSchoolId()));
            response.setType("adjust");
            return response;
        }).collect(Collectors.toList());
        
        responsePage.setRecords(records);
        return responsePage;
    }
    
    /**
     * 查询招生通知列表
     */
    public Page<InfoResponse> listInformations(Long schoolId, String schoolName, Integer pageNum, Integer pageSize) {
        Page<SchoolInformation> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SchoolInformation> wrapper = new QueryWrapper<>();
        
        // 按学校ID筛选
        if (schoolId != null) {
            wrapper.eq("school_id", schoolId);
        }
        
        // 按学校名称筛选
        if (schoolName != null && !schoolName.trim().isEmpty()) {
            QueryWrapper<School> schoolWrapper = new QueryWrapper<>();
            schoolWrapper.like("school_name", schoolName);
            List<School> schools = schoolMapper.selectList(schoolWrapper);
            if (!schools.isEmpty()) {
                List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
                wrapper.in("school_id", schoolIds);
            } else {
                return new Page<>(pageNum, pageSize);
            }
        }
        
        // 按发布日期降序排列
        wrapper.orderByDesc("publish_date");
        
        Page<SchoolInformation> informationPage = informationMapper.selectPage(page, wrapper);
        
        // 转换为 InfoResponse
        Page<InfoResponse> responsePage = new Page<>(pageNum, pageSize);
        responsePage.setTotal(informationPage.getTotal());
        responsePage.setPages(informationPage.getPages());
        
        List<InfoResponse> records = informationPage.getRecords().stream().map(information -> {
            InfoResponse response = new InfoResponse();
            BeanUtils.copyProperties(information, response);
            response.setSchoolName(getSchoolName(information.getSchoolId()));
            response.setType("information");
            return response;
        }).collect(Collectors.toList());
        
        responsePage.setRecords(records);
        return responsePage;
    }
    
    /**
     * 获取招生简章详情
     */
    public InfoResponse getBrochureDetail(Long id) {
        SchoolBrochure brochure = brochureMapper.selectById(id);
        if (brochure == null) {
            return null;
        }
        
        InfoResponse response = new InfoResponse();
        BeanUtils.copyProperties(brochure, response);
        response.setSchoolName(getSchoolName(brochure.getSchoolId()));
        response.setType("brochure");
        return response;
    }
    
    /**
     * 获取调剂公告详情
     */
    public InfoResponse getAdjustDetail(Long id) {
        SchoolAdjust adjust = adjustMapper.selectById(id);
        if (adjust == null) {
            return null;
        }
        
        InfoResponse response = new InfoResponse();
        BeanUtils.copyProperties(adjust, response);
        response.setSchoolName(getSchoolName(adjust.getSchoolId()));
        response.setType("adjust");
        return response;
    }
    
    /**
     * 获取招生通知详情
     */
    public InfoResponse getInformationDetail(Long id) {
        SchoolInformation information = informationMapper.selectById(id);
        if (information == null) {
            return null;
        }
        
        InfoResponse response = new InfoResponse();
        BeanUtils.copyProperties(information, response);
        response.setSchoolName(getSchoolName(information.getSchoolId()));
        response.setType("information");
        return response;
    }
    
    /**
     * 获取混合资讯列表（用于首页展示）
     * 混合展示招生简章、调剂公告、招生通知
     */
    public Page<InfoResponse> listMixedInfo(Integer pageNum, Integer pageSize) {
        Page<InfoResponse> responsePage = new Page<>(pageNum, pageSize);
        
        // 分别获取三种类型的最新数据
        int itemsPerType = (int) Math.ceil(pageSize / 3.0);
        
        // 获取最新的招生简章
        QueryWrapper<SchoolBrochure> brochureWrapper = new QueryWrapper<>();
        brochureWrapper.orderByDesc("publish_date").last("LIMIT " + itemsPerType);
        List<SchoolBrochure> brochures = brochureMapper.selectList(brochureWrapper);
        
        // 获取最新的调剂公告
        QueryWrapper<SchoolAdjust> adjustWrapper = new QueryWrapper<>();
        adjustWrapper.orderByDesc("publish_date").last("LIMIT " + itemsPerType);
        List<SchoolAdjust> adjusts = adjustMapper.selectList(adjustWrapper);
        
        // 获取最新的招生通知
        QueryWrapper<SchoolInformation> informationWrapper = new QueryWrapper<>();
        informationWrapper.orderByDesc("publish_date").last("LIMIT " + itemsPerType);
        List<SchoolInformation> informations = informationMapper.selectList(informationWrapper);
        
        // 转换并合并结果
        List<InfoResponse> records = new java.util.ArrayList<>();
        
        // 添加招生简章
        for (SchoolBrochure brochure : brochures) {
            InfoResponse response = new InfoResponse();
            BeanUtils.copyProperties(brochure, response);
            response.setSchoolName(getSchoolName(brochure.getSchoolId()));
            response.setType("brochure");
            response.setTag("招生简章");
            records.add(response);
        }
        
        // 添加调剂公告
        for (SchoolAdjust adjust : adjusts) {
            InfoResponse response = new InfoResponse();
            BeanUtils.copyProperties(adjust, response);
            response.setSchoolName(getSchoolName(adjust.getSchoolId()));
            response.setType("adjust");
            response.setTag("调剂公告");
            records.add(response);
        }
        
        // 添加招生通知
        for (SchoolInformation information : informations) {
            InfoResponse response = new InfoResponse();
            BeanUtils.copyProperties(information, response);
            response.setSchoolName(getSchoolName(information.getSchoolId()));
            response.setType("information");
            response.setTag("招生通知");
            records.add(response);
        }
        
        // 按发布日期排序
        records.sort((a, b) -> {
            if (a.getPublishDate() == null) return 1;
            if (b.getPublishDate() == null) return -1;
            return b.getPublishDate().compareTo(a.getPublishDate());
        });
        
        // 限制返回数量
        if (records.size() > pageSize) {
            records = records.subList(0, pageSize);
        }
        
        responsePage.setRecords(records);
        responsePage.setTotal(records.size());
        responsePage.setPages(1);
        
        return responsePage;
    }
}
