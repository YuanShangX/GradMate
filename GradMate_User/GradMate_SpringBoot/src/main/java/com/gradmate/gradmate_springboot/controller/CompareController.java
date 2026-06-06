package com.gradmate.gradmate_springboot.controller;

import com.gradmate.gradmate_springboot.common.Result;
import com.gradmate.gradmate_springboot.service.CompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 对比控制器
 */
@RestController
@RequestMapping("/api/compare")
public class CompareController {
    
    @Autowired
    private CompareService compareService;
    
    /**
     * 对比院校
     * @param request { schoolIds: [1, 2, 3] }
     * @return 对比结果
     */
    @PostMapping("/schools")
    public Result<Map<String, Object>> compareSchools(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> schoolIdsObj = (List<Object>) request.get("schoolIds");
            
            if (schoolIdsObj == null || schoolIdsObj.isEmpty()) {
                return Result.error("请选择要对比的院校");
            }
            
            // 转换为Long类型
            List<Long> schoolIds = schoolIdsObj.stream()
                .map(obj -> {
                    if (obj instanceof Integer) {
                        return ((Integer) obj).longValue();
                    } else if (obj instanceof Long) {
                        return (Long) obj;
                    } else {
                        return Long.parseLong(obj.toString());
                    }
                })
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> result = compareService.compareSchools(schoolIds);
            return Result.success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("对比失败：" + e.getMessage());
        }
    }
    
    /**
     * 对比专业
     * @param request { majorIds: [1, 2, 3] }
     * @return 对比结果
     */
    @PostMapping("/majors")
    public Result<Map<String, Object>> compareMajors(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Object> majorIdsObj = (List<Object>) request.get("majorIds");
            
            if (majorIdsObj == null || majorIdsObj.isEmpty()) {
                return Result.error("请选择要对比的专业");
            }
            
            // 转换为Long类型
            List<Long> majorIds = majorIdsObj.stream()
                .map(obj -> {
                    if (obj instanceof Integer) {
                        return ((Integer) obj).longValue();
                    } else if (obj instanceof Long) {
                        return (Long) obj;
                    } else {
                        return Long.parseLong(obj.toString());
                    }
                })
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> result = compareService.compareMajors(majorIds);
            return Result.success(result);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("对比失败：" + e.getMessage());
        }
    }
}
