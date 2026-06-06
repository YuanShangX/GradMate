package com.gradmate.gradmate_springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能择校助手服务
 */
@Service
public class AssistantService {
    
    @Autowired
    private GradMateAgentService agentService;
    
    // 记录每个用户的对话状态
    private Map<String, Integer> userStepMap = new HashMap<>();
    private Map<String, Integer> userQuestionCountMap = new HashMap<>();
    
    /**
     * 处理第一步：基础信息采集
     * 需要收集：本科学校、专业、专业水平、英语水平、数学水平
     */
    public Map<String, Object> handleStep1(String userInput, Map<String, Object> profile) {
        Map<String, Object> response = new HashMap<>();
        
        // 获取当前已收集的信息数量
        int collectedCount = 0;
        if (profile.get("school") != null && !profile.get("school").toString().isEmpty()) collectedCount++;
        if (profile.get("major") != null && !profile.get("major").toString().isEmpty()) collectedCount++;
        if (profile.get("majorLevel") != null && !profile.get("majorLevel").toString().isEmpty()) collectedCount++;
        if (profile.get("englishLevel") != null && !profile.get("englishLevel").toString().isEmpty()) collectedCount++;
        if (profile.get("mathLevel") != null && !profile.get("mathLevel").toString().isEmpty()) collectedCount++;
        
        // 根据当前收集进度，更新profile并生成下一个问题
        String nextQuestion = "";
        Map<String, Object> updatedProfile = new HashMap<>(profile);
        
        if (collectedCount == 0) {
            // 收集本科学校
            updatedProfile.put("school", userInput);
            nextQuestion = "很好！接下来请告诉我你的本科专业是什么？";
        } else if (collectedCount == 1) {
            // 收集专业
            updatedProfile.put("major", userInput);
            nextQuestion = "了解了！那你觉得自己的专业水平如何呢？（优秀/良好/一般）";
        } else if (collectedCount == 2) {
            // 收集专业水平
            updatedProfile.put("majorLevel", userInput);
            nextQuestion = "好的！你的英语水平怎么样？（四级/六级/优秀）";
        } else if (collectedCount == 3) {
            // 收集英语水平
            updatedProfile.put("englishLevel", userInput);
            nextQuestion = "明白了！最后，你的数学水平如何？（优秀/良好/一般）";
        } else if (collectedCount == 4) {
            // 收集数学水平，完成第一步，使用AI生成测评
            updatedProfile.put("mathLevel", userInput);
            
            // 使用AI生成基本信息测评
            String aiPrompt = String.format(
                "作为考研择校助手，请根据以下学生信息给出简短的基础评估（50字以内）：\n" +
                "本科院校：%s\n" +
                "本科专业：%s\n" +
                "专业水平：%s\n" +
                "英语水平：%s\n" +
                "数学水平：%s\n\n" +
                "请给出鼓励性的评估，并建议适合的院校层次（985/211/双一流/普通院校）。",
                updatedProfile.get("school"),
                updatedProfile.get("major"),
                updatedProfile.get("majorLevel"),
                updatedProfile.get("englishLevel"),
                updatedProfile.get("mathLevel")
            );
            
            try {
                String aiAssessment = agentService.chat(1L, aiPrompt);
                nextQuestion = "✅ Part1 完成！\n\n基本信息测评：\n" + aiAssessment + 
                              "\n\n现在让我们进入 Part2 专业倾向测评\n\n你对哪些专业方向感兴趣？（可以说多个）";
            } catch (Exception e) {
                // AI调用失败，使用默认评估
                String assessment = generateAssessment(updatedProfile);
                nextQuestion = "✅ Part1 完成！\n\n基本信息测评：\n" + assessment + 
                              "\n\n现在让我们进入 Part2 专业倾向测评\n\n你对哪些专业方向感兴趣？（可以说多个）";
            }
            
            response.put("nextStep", true);
        }
        
        response.put("message", nextQuestion);
        response.put("profile", updatedProfile);
        
        return response;
    }
    
    /**
     * 处理第二步：专业倾向测评
     */
    public Map<String, Object> handleStep2(String userInput, Map<String, Object> profile) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> updatedProfile = new HashMap<>(profile);
        
        if (profile.get("targetMajor") == null || profile.get("targetMajor").toString().isEmpty()) {
            // 第一次回答，收集专业倾向，使用AI生成推荐
            updatedProfile.put("targetMajor", userInput);
            
            String aiPrompt = String.format(
                "作为考研择校助手，学生背景：本科%s专业，%s水平。现在对%s方向感兴趣。\n" +
                "请推荐3个适合的考研专业方向（每个用一句话说明理由，总共60字以内）。",
                profile.get("major"),
                profile.get("majorLevel"),
                userInput
            );
            
            try {
                String aiRecommendation = agentService.chat(1L, aiPrompt);
                String nextQuestion = "很好！基于你的背景和兴趣，我的建议是：\n\n" +
                                     aiRecommendation +
                                     "\n\n接下来让我们进入 Part3 学校倾向测评\n\n你希望考取什么类型的学校？（985/211/双一流/普通院校）";
                response.put("message", nextQuestion);
            } catch (Exception e) {
                // AI调用失败，使用默认推荐
                String nextQuestion = "很好！基于你的背景和兴趣，我建议你可以考虑以下专业方向：\n\n" +
                                     generateMajorRecommendation(updatedProfile) +
                                     "\n\n接下来让我们进入 Part3 学校倾向测评\n\n你希望考取什么类型的学校？（985/211/双一流/普通院校）";
                response.put("message", nextQuestion);
            }
            
            response.put("nextStep", true);
        }
        
        response.put("profile", updatedProfile);
        return response;
    }
    
    /**
     * 处理第三步：学校倾向测评
     */
    public Map<String, Object> handleStep3(String userInput, Map<String, Object> profile) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> updatedProfile = new HashMap<>(profile);
        
        updatedProfile.put("targetSchoolType", userInput);
        
        // 使用AI生成最终报告
        String aiPrompt = String.format(
            "作为考研择校助手，请根据以下信息推荐3所具体院校（每所院校一句话说明推荐理由）：\n" +
            "学生背景：本科%s，%s专业，专业水平%s，英语%s，数学%s\n" +
            "目标专业：%s\n" +
            "目标院校类型：%s\n\n" +
            "请给出具体院校名称和简短推荐理由（总共100字以内）。",
            profile.get("school"),
            profile.get("major"),
            profile.get("majorLevel"),
            profile.get("englishLevel"),
            profile.get("mathLevel"),
            profile.get("targetMajor"),
            userInput
        );
        
        String report;
        try {
            report = agentService.chat(1L, aiPrompt);
        } catch (Exception e) {
            // AI调用失败，使用默认报告
            report = generateFinalReport(updatedProfile);
        }
        
        response.put("message", "🎉 测评完成！\n\n根据你的选择，我为你推荐以下院校：\n\n" + report + 
                               "\n\n💡 温馨提示：以上推荐仅供参考，请结合自身情况做出最终选择！");
        response.put("completed", true);
        response.put("report", report);
        response.put("profile", updatedProfile);
        
        return response;
    }
    
    /**
     * 生成基本信息测评
     */
    private String generateAssessment(Map<String, Object> profile) {
        StringBuilder assessment = new StringBuilder();
        
        assessment.append("📚 本科院校：").append(profile.get("school")).append("\n");
        assessment.append("🎓 本科专业：").append(profile.get("major")).append("\n");
        assessment.append("💪 专业水平：").append(profile.get("majorLevel")).append("\n");
        assessment.append("🔤 英语水平：").append(profile.get("englishLevel")).append("\n");
        assessment.append("🔢 数学水平：").append(profile.get("mathLevel")).append("\n\n");
        
        // 简单的评估逻辑
        String majorLevel = profile.get("majorLevel").toString();
        String englishLevel = profile.get("englishLevel").toString();
        
        if (majorLevel.contains("优秀") && (englishLevel.contains("六级") || englishLevel.contains("优秀"))) {
            assessment.append("综合评估：你的基础非常扎实，建议冲刺985/211院校！");
        } else if (majorLevel.contains("良好")) {
            assessment.append("综合评估：你的基础良好，可以考虑211或双一流院校！");
        } else {
            assessment.append("综合评估：建议选择适合自己的院校，稳扎稳打！");
        }
        
        return assessment.toString();
    }
    
    /**
     * 生成专业推荐
     */
    private String generateMajorRecommendation(Map<String, Object> profile) {
        String currentMajor = profile.get("major").toString();
        String targetMajor = profile.get("targetMajor").toString();
        
        // 这里可以根据专业相关性给出推荐
        return "1. " + targetMajor + "（与你的兴趣匹配）\n" +
               "2. " + currentMajor + "相关专业（发挥专业优势）\n" +
               "3. 交叉学科专业（拓展发展方向）";
    }
    
    /**
     * 生成最终报告
     */
    private String generateFinalReport(Map<String, Object> profile) {
        String schoolType = profile.get("targetSchoolType").toString();
        String targetMajor = profile.get("targetMajor").toString();
        
        StringBuilder report = new StringBuilder();
        
        if (schoolType.contains("985")) {
            report.append("🏆 推荐院校（985）：\n");
            report.append("1. 北京大学 - ").append(targetMajor).append("\n");
            report.append("2. 清华大学 - ").append(targetMajor).append("\n");
            report.append("3. 复旦大学 - ").append(targetMajor).append("\n");
        } else if (schoolType.contains("211")) {
            report.append("🎯 推荐院校（211）：\n");
            report.append("1. 北京邮电大学 - ").append(targetMajor).append("\n");
            report.append("2. 华东师范大学 - ").append(targetMajor).append("\n");
            report.append("3. 中国传媒大学 - ").append(targetMajor).append("\n");
        } else {
            report.append("📖 推荐院校：\n");
            report.append("1. 首都师范大学 - ").append(targetMajor).append("\n");
            report.append("2. 北京工业大学 - ").append(targetMajor).append("\n");
            report.append("3. 上海师范大学 - ").append(targetMajor).append("\n");
        }
        
        report.append("\n💡 温馨提示：以上推荐仅供参考，请结合自身情况做出最终选择！");
        
        return report.toString();
    }
}
