package com.gradmate.gradmate_adminsystem_springboot.service;

import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeleteGuardService {
    private final JdbcTemplate jdbcTemplate;

    public DeleteGuardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void guard(String resource, Object id) {
        if ("schools".equals(resource)) {
            guardSchool(id);
        } else if ("users".equals(resource)) {
            guardUser(id);
        } else if ("admins".equals(resource)) {
            long total = count("select count(*) from admin_user where status = 1");
            if (total <= 1) {
                throw new ApiException("至少需要保留一个启用的管理员账号");
            }
        }
    }

    private void guardSchool(Object id) {
        String schoolName = jdbcTemplate.queryForObject("select school_name from school where id = ?", String.class, id);
        StringBuilder refs = new StringBuilder();
        append(refs, "院校标签", count("select count(*) from school_character where school_id = ?", id));
        append(refs, "招生简章", count("select count(*) from school_brochure where school_id = ?", id));
        append(refs, "调剂通知", count("select count(*) from school_adjust where school_id = ?", id));
        append(refs, "校园资讯", count("select count(*) from school_information where school_id = ?", id));
        append(refs, "专业", count("select count(*) from major where school_name = ?", schoolName));
        append(refs, "用户目标", count("select count(*) from user where target_school_id = ?", id));
        append(refs, "收藏", count("select count(*) from user_favorite where type = 'school' and target_id = ?", id));
        append(refs, "浏览历史", count("select count(*) from user_history where type = 'school' and target_id = ?", id));
        if (!refs.isEmpty()) {
            throw new ApiException("该院校仍有关联数据，不能删除：" + refs);
        }
    }

    private void guardUser(Object id) {
        StringBuilder refs = new StringBuilder();
        append(refs, "学习任务", count("select count(*) from daily_task where user_id = ?", id));
        append(refs, "学习计划", count("select count(*) from study_plan where user_id = ?", id));
        append(refs, "学习记录", count("select count(*) from study_record where user_id = ?", id));
        append(refs, "任务标签", count("select count(*) from task_tags where user_id = ?", id));
        append(refs, "收藏", count("select count(*) from user_favorite where user_id = ?", id));
        append(refs, "浏览历史", count("select count(*) from user_history where user_id = ?", id));
        append(refs, "AI会话", count("select count(*) from chat_session where user_id = ?", id));
        append(refs, "AI消息", count("select count(*) from chat_message where user_id = ?", id));
        if (!refs.isEmpty()) {
            throw new ApiException("该用户仍有关联数据，不能删除：" + refs);
        }
    }

    private void append(StringBuilder refs, String label, long count) {
        if (count <= 0) {
            return;
        }
        if (!refs.isEmpty()) {
            refs.append("、");
        }
        refs.append(label).append("(").append(count).append(")");
    }

    private long count(String sql, Object... args) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class, args);
        return value == null ? 0 : value;
    }
}
