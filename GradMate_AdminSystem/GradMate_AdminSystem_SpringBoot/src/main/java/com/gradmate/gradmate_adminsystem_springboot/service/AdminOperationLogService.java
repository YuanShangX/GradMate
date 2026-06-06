package com.gradmate.gradmate_adminsystem_springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import com.gradmate.gradmate_adminsystem_springboot.common.PageResult;
import com.gradmate.gradmate_adminsystem_springboot.entity.AdminOperationLog;
import com.gradmate.gradmate_adminsystem_springboot.mapper.AdminOperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminOperationLogService {
    private final AdminOperationLogMapper logMapper;
    private final AdminMetaRegistry metaRegistry;

    public AdminOperationLogService(AdminOperationLogMapper logMapper, AdminMetaRegistry metaRegistry) {
        this.logMapper = logMapper;
        this.metaRegistry = metaRegistry;
    }

    public void record(HttpServletRequest request, String resourceKey, String actionType, Object targetId, String resultStatus, String resultMessage) {
        try {
            AdminOperationLog log = new AdminOperationLog();
            log.setAdminId(resolveAdminId(request));
            log.setAdminUsername(resolveAdminUsername(request));
            log.setModuleName(moduleName(resourceKey));
            log.setResourceKey(resourceKey);
            log.setActionType(actionType);
            log.setTargetId(targetId == null ? null : String.valueOf(targetId));
            log.setRequestMethod(request == null ? null : request.getMethod());
            log.setRequestUri(request == null ? null : request.getRequestURI());
            log.setResultStatus(resultStatus);
            log.setResultMessage(limit(resultMessage, 500));
            log.setIpAddress(resolveIp(request));
            log.setUserAgent(limit(request == null ? null : request.getHeader("User-Agent"), 500));
            log.setCreateTime(LocalDateTime.now());
            logMapper.insert(log);
        } catch (Exception e) {
            System.err.println("记录系统日志失败: " + e.getMessage());
        }
    }

    public PageResult page(int pageNum, int pageSize, String keyword, String actionType, String resourceKey, String adminUsername) {
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 100);
        QueryWrapper<AdminOperationLog> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like("admin_username", keyword)
                    .or().like("module_name", keyword)
                    .or().like("resource_key", keyword)
                    .or().like("target_id", keyword)
                    .or().like("result_message", keyword)
                    .or().like("ip_address", keyword));
        }
        if (StringUtils.hasText(actionType)) {
            wrapper.eq("action_type", actionType);
        }
        if (StringUtils.hasText(resourceKey)) {
            wrapper.eq("resource_key", resourceKey);
        }
        if (StringUtils.hasText(adminUsername)) {
            wrapper.like("admin_username", adminUsername);
        }
        wrapper.orderByDesc("create_time").orderByDesc("id");
        Page<AdminOperationLog> page = logMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Map<String, Object>> records = page.getRecords().stream().map(this::toMap).toList();
        return new PageResult(records, page.getTotal(), pageNum, pageSize);
    }

    public Map<String, Object> detail(Long id) {
        AdminOperationLog log = logMapper.selectById(id);
        if (log == null) {
            throw new ApiException("系统日志不存在");
        }
        return toMap(log);
    }

    public void delete(Long id, HttpServletRequest request) {
        AdminOperationLog log = logMapper.selectById(id);
        if (log == null) {
            throw new ApiException("系统日志不存在");
        }
        logMapper.deleteById(id);
        record(request, "logs", "DELETE_LOG", id, "SUCCESS", "删除系统日志");
    }

    private Map<String, Object> toMap(AdminOperationLog log) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", log.getId());
        map.put("adminId", log.getAdminId());
        map.put("adminUsername", log.getAdminUsername());
        map.put("moduleName", log.getModuleName());
        map.put("resourceKey", log.getResourceKey());
        map.put("actionType", log.getActionType());
        map.put("targetId", log.getTargetId());
        map.put("requestMethod", log.getRequestMethod());
        map.put("requestUri", log.getRequestUri());
        map.put("resultStatus", log.getResultStatus());
        map.put("resultMessage", log.getResultMessage());
        map.put("ipAddress", log.getIpAddress());
        map.put("userAgent", log.getUserAgent());
        map.put("createTime", log.getCreateTime());
        return map;
    }

    private String moduleName(String resourceKey) {
        if ("logs".equals(resourceKey)) {
            return "系统日志";
        }
        try {
            return metaRegistry.get(resourceKey).title();
        } catch (Exception e) {
            return switch (resourceKey) {
                case "auth" -> "登录认证";
                default -> resourceKey;
            };
        }
    }

    private Long resolveAdminId(HttpServletRequest request) {
        Object id = request == null ? null : request.getAttribute("adminId");
        return id instanceof Number number ? number.longValue() : null;
    }

    private String resolveAdminUsername(HttpServletRequest request) {
        Object username = request == null ? null : request.getAttribute("adminUsername");
        return username == null ? null : String.valueOf(username);
    }

    private String resolveIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    private String limit(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max);
    }
}
