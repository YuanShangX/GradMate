package com.gradmate.gradmate_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gradmate.gradmate_springboot.entity.*;
import com.gradmate.gradmate_springboot.mapper.*;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * GradMate AI工具类
 * 提供AI可调用的工具方法
 */
@Component
public class GradMateTools {
    
    @Autowired
    private SchoolMapper schoolMapper;
    
    @Autowired
    private MajorMapper majorMapper;
    
    @Autowired
    private SchoolBrochureMapper brochureMapper;
    
    @Autowired
    private SchoolAdjustMapper adjustMapper;
    
    @Autowired
    private SchoolInformationMapper informationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 分析推荐目标院校
     * AI会在需要时自动调用此方法
     */
    @Tool("根据用户的本科背景和意向分析推荐目标院校,返回推荐的院校列表")
    public String analyzeTargetUniversity(
            @P("用户本科背景描述,如学校层次(985/211/普通本科)、专业、成绩等") String profile
    ) {
        try {
            // 解析用户背景
            boolean is985 = profile.contains("985");
            boolean is211 = profile.contains("211");
            boolean isGood = profile.contains("成绩好") || profile.contains("成绩优秀");
            
            // 构建查询条件
            QueryWrapper<School> wrapper = new QueryWrapper<>();
            wrapper.isNotNull("school_name");
            wrapper.last("LIMIT 5");
            
            List<School> schools = schoolMapper.selectList(wrapper);
            
            // 构建返回结果
            if (schools.isEmpty()) {
                return "暂时没有找到合适的院校推荐,建议您提供更详细的背景信息。";
            }
            
            StringBuilder result = new StringBuilder("基于您的背景,为您推荐以下院校:\n\n");
            for (int i = 0; i < schools.size(); i++) {
                School school = schools.get(i);
                result.append(i + 1).append(". ").append(school.getSchoolName());
                if (school.getLocation() != null) {
                    result.append(" (").append(school.getLocation()).append(")");
                }
                result.append("\n");
                
                // 添加院校特点
                if (school.getSubordinate() != null) {
                    result.append("   主管部门: ").append(school.getSubordinate()).append("\n");
                }
                result.append("\n");
            }
            
            result.append("建议您根据自己的实际情况和目标专业,进一步了解这些院校的招生信息。");
            
            return result.toString();
            
        } catch (Exception e) {
            return "查询院校信息时出现错误,请稍后再试。";
        }
    }
    
    /**
     * 获取指定院校专业的分数线
     */
    @Tool("获取指定院校专业的历年分数线信息")
    public String getScoreLine(
            @P("院校名称") String university,
            @P("专业名称") String major
    ) {
        try {
            // 查询专业信息
            QueryWrapper<Major> wrapper = new QueryWrapper<>();
            wrapper.like("school_name", university)
                   .like("major_name", major)
                   .last("LIMIT 3");
            
            List<Major> majors = majorMapper.selectList(wrapper);
            
            if (majors.isEmpty()) {
                return String.format("未找到 %s - %s 的专业信息。可能是院校或专业名称不准确,建议您在院校库中搜索确认。", 
                        university, major);
            }
            
            StringBuilder result = new StringBuilder();
            result.append(university).append(" - ").append(major).append(" 专业信息:\n\n");
            
            for (Major m : majors) {
                result.append("📊 专业信息:\n");
                result.append("   学位类型: ").append(m.getDegreeType() != null ? m.getDegreeType() : "未公布").append("\n");
                result.append("   学习方式: ").append(m.getStudyMode() != null ? m.getStudyMode() : "未公布").append("\n");
                result.append("   拟招生人数: ").append(m.getPlannedEnrollment() != null ? m.getPlannedEnrollment() : "未公布").append("\n");
                if (m.getDepartment() != null) {
                    result.append("   院系所: ").append(m.getDepartment()).append("\n");
                }
                result.append("\n");
            }
            
            result.append("注: 以上数据仅供参考,实际录取分数线可能因年份和政策调整而变化。");
            
            return result.toString();
            
        } catch (Exception e) {
            return "查询分数线信息时出现错误,请稍后再试。";
        }
    }
    
    /**
     * 获取最新的考研资讯
     */
    @Tool("获取最新的考研资讯和政策,包括招生简章、调剂信息等")
    public String getGraduationNews() {
        try {
            // 查询最新简章
            List<SchoolBrochure> brochures = brochureMapper.selectList(
                    new QueryWrapper<SchoolBrochure>()
                            .orderByDesc("publish_date")
                            .last("LIMIT 2")
            );
            
            // 查询最新调剂信息
            List<SchoolAdjust> adjusts = adjustMapper.selectList(
                    new QueryWrapper<SchoolAdjust>()
                            .orderByDesc("publish_date")
                            .last("LIMIT 2")
            );
            
            StringBuilder result = new StringBuilder("📰 最新考研资讯:\n\n");
            
            // 添加简章信息
            if (!brochures.isEmpty()) {
                result.append("【招生简章】\n");
                for (SchoolBrochure b : brochures) {
                    result.append("• ").append(b.getTitle()).append("\n");
                    result.append("  发布时间: ").append(b.getPublishDate()).append("\n\n");
                }
            }
            
            // 添加调剂信息
            if (!adjusts.isEmpty()) {
                result.append("【调剂信息】\n");
                for (SchoolAdjust a : adjusts) {
                    result.append("• ").append(a.getTitle()).append("\n");
                    result.append("  发布时间: ").append(a.getPublishDate()).append("\n\n");
                }
            }
            
            if (brochures.isEmpty() && adjusts.isEmpty()) {
                return "暂时没有最新的考研资讯,建议您定期关注目标院校的官方网站。";
            }
            
            result.append("提示: 您可以在资讯页面查看更多详细信息。");
            
            return result.toString();
            
        } catch (Exception e) {
            return "获取考研资讯时出现错误,请稍后再试。";
        }
    }
    
    /**
     * 获取用户设定的目标院校和专业
     */
    @Tool("获取用户设定的目标院校和专业信息")
    public String getUserTarget(@P("用户ID") Long userId) {
        try {
            User user = userMapper.selectById(userId);
            
            if (user == null) {
                return "未找到用户信息。";
            }
            
            String targetSchool = user.getTargetSchool();
            String targetMajor = user.getTargetMajor();
            
            if (targetSchool == null && targetMajor == null) {
                return "用户尚未设定目标院校和专业。建议用户在个人中心设置目标,以便获得更精准的推荐。";
            }
            
            StringBuilder result = new StringBuilder("用户目标设定:\n");
            if (targetSchool != null) {
                result.append("目标院校: ").append(targetSchool).append("\n");
            }
            if (targetMajor != null) {
                result.append("目标专业: ").append(targetMajor).append("\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "获取用户目标信息时出现错误。";
        }
    }
}
