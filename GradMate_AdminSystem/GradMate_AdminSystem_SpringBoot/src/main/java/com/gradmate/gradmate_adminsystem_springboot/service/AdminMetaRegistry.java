package com.gradmate.gradmate_adminsystem_springboot.service;

import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AdminMetaRegistry {
    private final Map<String, TableMeta> metas = new LinkedHashMap<>();

    public AdminMetaRegistry() {
        add("schools", "院校", "school", "id",
                f("school_name", "logo", "link", "location", "subordinate", "syl", "yjsy", "zhx"),
                f("school_name"), f("school_name", "location", "subordinate"),
                m("location", "location", "subordinate", "subordinate"), "s.id desc");
        add("majors", "专业", "major", "id",
                f("school_name", "major_name", "province", "major_code", "degree_type", "exam_type", "department", "study_mode", "research_direction", "veteran_program", "shaogu_program", "advisor", "planned_enrollment", "exam_subject1", "exam_subject2", "exam_subject3", "exam_subject4"),
                f("school_name", "major_name"), f("school_name", "major_name", "major_code", "department", "research_direction"),
                m("schoolName", "school_name", "majorName", "major_name", "province", "province", "degreeType", "degree_type", "examType", "exam_type", "studyMode", "study_mode"), "id desc");
        add("major-categories", "专业分类", "major_category", "id",
                f("category_code", "category_name", "discipline_code", "discipline_name", "major_code", "major_name", "degree_type", "sort_order"),
                f("category_code", "category_name", "discipline_code", "discipline_name", "major_code", "major_name", "degree_type"),
                f("category_code", "category_name", "discipline_code", "discipline_name", "major_code", "major_name"),
                m("degreeType", "degree_type", "categoryCode", "category_code"), "sort_order asc,id desc");
        add("brochure", "招生简章", "school_brochure", "id",
                f("school_id", "title", "url", "publish_date"),
                f("school_id", "title"), f("title", "url"),
                m("schoolId", "school_id"), "publish_date desc,id desc");
        add("adjust", "调剂通知", "school_adjust", "id",
                f("school_id", "title", "url", "publish_date"),
                f("school_id", "title"), f("title", "url"),
                m("schoolId", "school_id"), "publish_date desc,id desc");
        add("information", "校园资讯", "school_information", "id",
                f("school_id", "title", "url", "publish_date"),
                f("school_id", "title"), f("title", "url"),
                m("schoolId", "school_id"), "publish_date desc,id desc");
        add("users", "用户", "user", "id",
                f("username", "password", "openid", "nickname", "avatar", "phone", "target_school", "target_major", "target_school_id", "target_major_id"),
                f(), f("username", "openid", "nickname", "phone", "target_school", "target_major"),
                m("username", "username", "nickname", "nickname", "targetSchoolId", "target_school_id", "targetMajorId", "target_major_id"), "id desc");
        add("favorites", "收藏", "user_favorite", "id",
                f("user_id", "type", "target_id", "target_name", "metadata"),
                f("user_id", "type", "target_id"), f("target_name", "metadata"),
                m("userId", "user_id", "type", "type"), "id desc");
        add("histories", "浏览历史", "user_history", "id",
                f("user_id", "type", "target_id", "target_name", "metadata", "view_time"),
                f("user_id", "type", "target_id"), f("target_name", "metadata"),
                m("userId", "user_id", "type", "type"), "view_time desc,id desc");
        add("study-tasks", "学习任务", "daily_task", "id",
                f("user_id", "task_content", "task_date", "is_completed", "priority", "complete_time", "color_theme", "estimated_duration", "actual_duration", "tags", "parent_task_id", "repeat_type", "sort_order", "description", "reminder_time", "status"),
                f("user_id", "task_content", "task_date"), f("task_content", "tags", "description"),
                m("userId", "user_id", "status", "status", "isCompleted", "is_completed"), "task_date desc,id desc");
        add("study-records", "学习记录", "study_record", "id",
                f("user_id", "check_date", "duration", "note", "mood", "focus_count", "pause_count", "study_type"),
                f("user_id", "check_date"), f("note", "study_type"),
                m("userId", "user_id", "studyType", "study_type"), "check_date desc,id desc");
        add("ai-sessions", "AI会话", "chat_session", "id",
                f("user_id", "session_id", "title", "message_count"),
                f("user_id", "session_id"), f("session_id", "title"),
                m("userId", "user_id"), "update_time desc,id desc");
        add("ai-messages", "AI消息", "chat_message", "id",
                f("session_id", "user_id", "role", "content", "tokens", "tool_calls"),
                f("session_id", "user_id", "role", "content"), f("session_id", "role", "content"),
                m("userId", "user_id", "sessionId", "session_id", "role", "role"), "create_time desc,id desc");
        add("admins", "管理员", "admin_user", "id",
                f("username", "password", "nickname", "role", "status"),
                f("username", "password"), f("username", "nickname", "role"),
                m("status", "status", "role", "role"), "id desc");
    }

    public TableMeta get(String key) {
        TableMeta meta = metas.get(key);
        if (meta == null) {
            throw new ApiException("不支持的管理资源: " + key);
        }
        return meta;
    }

    public Collection<TableMeta> all() {
        return metas.values();
    }

    private void add(String key, String title, String tableName, String idColumn, List<String> fields, List<String> requiredFields, List<String> searchFields, Map<String, String> filterFields, String defaultOrder) {
        metas.put(key, new TableMeta(key, title, tableName, idColumn, fields, requiredFields, searchFields, filterFields, defaultOrder));
    }

    private static List<String> f(String... fields) {
        return List.of(fields);
    }

    private static Map<String, String> m(String... kv) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            map.put(kv[i], kv[i + 1]);
        }
        return map;
    }
}
