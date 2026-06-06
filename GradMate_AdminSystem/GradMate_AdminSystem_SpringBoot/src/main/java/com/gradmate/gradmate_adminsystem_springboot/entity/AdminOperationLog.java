package com.gradmate.gradmate_adminsystem_springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("admin_operation_log")
public class AdminOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String adminUsername;
    private String moduleName;
    private String resourceKey;
    private String actionType;
    private String targetId;
    private String requestMethod;
    private String requestUri;
    private String resultStatus;
    private String resultMessage;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createTime;
}
