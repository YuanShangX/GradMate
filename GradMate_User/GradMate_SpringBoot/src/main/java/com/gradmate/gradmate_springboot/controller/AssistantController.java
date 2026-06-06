package com.gradmate.gradmate_springboot.controller;

import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.service.AssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 智能择校助手控制器
 */
@RestController
@RequestMapping("/api/ai/assistant")
public class AssistantController {
    
    @Autowired
    private AssistantService assistantService;
    
    /**
     * 第一步：基础信息采集
     */
    @PostMapping("/step1")
    public Result<Map<String, Object>> handleStep1(@RequestBody Map<String, Object> request) {
        try {
            String userInput = (String) request.get("userInput");
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) request.get("profile");
            
            Map<String, Object> response = assistantService.handleStep1(userInput, profile);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("处理失败：" + e.getMessage());
        }
    }
    
    /**
     * 第二步：专业倾向测评
     */
    @PostMapping("/step2")
    public Result<Map<String, Object>> handleStep2(@RequestBody Map<String, Object> request) {
        try {
            String userInput = (String) request.get("userInput");
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) request.get("profile");
            
            Map<String, Object> response = assistantService.handleStep2(userInput, profile);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("处理失败：" + e.getMessage());
        }
    }
    
    /**
     * 第三步：学校倾向测评
     */
    @PostMapping("/step3")
    public Result<Map<String, Object>> handleStep3(@RequestBody Map<String, Object> request) {
        try {
            String userInput = (String) request.get("userInput");
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) request.get("profile");
            
            Map<String, Object> response = assistantService.handleStep3(userInput, profile);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("处理失败：" + e.getMessage());
        }
    }
}
