package com.gradmate.gradmate_adminsystem_springboot.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {
    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> stats() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("users", count("user"));
        data.put("schools", count("school"));
        data.put("majors", count("major"));
        data.put("infos", count("school_brochure") + count("school_adjust") + count("school_information"));
        data.put("studyRecords", count("study_record"));
        data.put("aiSessions", count("chat_session"));
        data.put("favorites", count("user_favorite"));
        data.put("admins", count("admin_user"));
        return data;
    }

    private long count(String table) {
        Long value = jdbcTemplate.queryForObject("select count(*) from `" + table + "`", Long.class);
        return value == null ? 0 : value;
    }
}
