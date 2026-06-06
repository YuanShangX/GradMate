package com.gradmate.gradmate_adminsystem_springboot.service;

import com.gradmate.gradmate_adminsystem_springboot.common.ApiException;
import com.gradmate.gradmate_adminsystem_springboot.common.PageResult;
import com.gradmate.gradmate_adminsystem_springboot.util.PasswordUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
public class AdminDataService {
    private final JdbcTemplate jdbcTemplate;
    private final AdminMetaRegistry registry;
    private final DeleteGuardService deleteGuardService;

    public AdminDataService(JdbcTemplate jdbcTemplate, AdminMetaRegistry registry, DeleteGuardService deleteGuardService) {
        this.jdbcTemplate = jdbcTemplate;
        this.registry = registry;
        this.deleteGuardService = deleteGuardService;
    }

    public Collection<TableMeta> metas() {
        return registry.all();
    }

    public PageResult page(String resource, int pageNum, int pageSize, String keyword, Map<String, String> params) {
        TableMeta meta = registry.get(resource);
        pageNum = Math.max(pageNum, 1);
        pageSize = Math.min(Math.max(pageSize, 1), 100);
        List<Object> args = new ArrayList<>();
        String where = buildWhere(meta, keyword, params, args);
        String countSql = "schools".equals(resource)
                ? "select count(*) from school s left join school_character c on s.id = c.school_id" + where
                : "select count(*) from `" + meta.tableName() + "`" + where;
        long total = jdbcTemplate.queryForObject(countSql, Long.class, args.toArray());
        List<Object> queryArgs = new ArrayList<>(args);
        queryArgs.add(pageSize);
        queryArgs.add((pageNum - 1) * pageSize);
        String select = "schools".equals(resource)
                ? "select s.*, c.syl, c.yjsy, c.zhx from school s left join school_character c on s.id = c.school_id"
                : "select * from `" + meta.tableName() + "`";
        if ("schools".equals(resource) && where.startsWith(" where ")) {
            where = where.replaceFirst(" where ", " where ");
        }
        String sql = select + where + " order by " + meta.defaultOrder() + " limit ? offset ?";
        return new PageResult(jdbcTemplate.queryForList(sql, queryArgs.toArray()), total, pageNum, pageSize);
    }

    public Map<String, Object> detail(String resource, Object id) {
        TableMeta meta = registry.get(resource);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select * from `" + meta.tableName() + "` where `" + meta.idColumn() + "` = ?", id);
        if (rows.isEmpty()) {
            throw new ApiException("数据不存在");
        }
        return rows.get(0);
    }

    public Long create(String resource, Map<String, Object> body) {
        TableMeta meta = registry.get(resource);
        normalize(meta, body, true);
        validateRequired(meta, body);
        validateRelation(meta, body);
        List<String> fields = writableFields(meta, body);
        if (fields.isEmpty()) {
            throw new ApiException("没有可保存的字段");
        }
        String columns = fields.stream().map(this::q).collect(Collectors.joining(","));
        String placeholders = fields.stream().map(f -> "?").collect(Collectors.joining(","));
        Object[] values = fields.stream().map(body::get).toArray();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into `" + meta.tableName() + "` (" + columns + ") values (" + placeholders + ")",
                    Statement.RETURN_GENERATED_KEYS
            );
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey() == null ? null : keyHolder.getKey().longValue();
        if ("schools".equals(resource)) {
            body.put("id", id);
            syncSchoolCharacter(body);
        }
        return id;
    }

    public void update(String resource, Object id, Map<String, Object> body) {
        TableMeta meta = registry.get(resource);
        detail(resource, id);
        normalize(meta, body, false);
        validateRelation(meta, body);
        List<String> fields = writableFields(meta, body).stream().filter(f -> !meta.idColumn().equals(f)).toList();
        if (fields.isEmpty()) {
            return;
        }
        List<Object> args = fields.stream().map(body::get).collect(Collectors.toCollection(ArrayList::new));
        args.add(id);
        String sets = fields.stream().map(f -> q(f) + " = ?").collect(Collectors.joining(","));
        jdbcTemplate.update("update `" + meta.tableName() + "` set " + sets + " where `" + meta.idColumn() + "` = ?", args.toArray());
        if ("schools".equals(resource)) {
            body.put("id", id);
            syncSchoolCharacter(body);
        }
    }

    public void delete(String resource, Object id) {
        TableMeta meta = registry.get(resource);
        detail(resource, id);
        deleteGuardService.guard(resource, id);
        jdbcTemplate.update("delete from `" + meta.tableName() + "` where `" + meta.idColumn() + "` = ?", id);
    }

    public List<Map<String, Object>> schoolOptions(String keyword) {
        String kw = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        return jdbcTemplate.queryForList("select id, school_name from school where school_name like ? order by id desc limit 20", kw);
    }

    private String buildWhere(TableMeta meta, String keyword, Map<String, String> params, List<Object> args) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.hasText(keyword) && !meta.searchFields().isEmpty()) {
            parts.add("(" + meta.searchFields().stream().map(f -> columnRef(meta, f) + " like ?").collect(Collectors.joining(" or ")) + ")");
            for (int i = 0; i < meta.searchFields().size(); i++) {
                args.add("%" + keyword.trim() + "%");
            }
        }
        meta.filterFields().forEach((param, column) -> {
            String value = params.get(param);
            if (StringUtils.hasText(value)) {
                parts.add(columnRef(meta, column) + " = ?");
                args.add(value.trim());
            }
        });
        return parts.isEmpty() ? "" : " where " + String.join(" and ", parts);
    }

    private void normalize(TableMeta meta, Map<String, Object> body, boolean create) {
        body.keySet().removeIf(key -> !meta.fields().contains(key));
        body.replaceAll((k, v) -> v instanceof String s && !StringUtils.hasText(s) ? null : v);
        if ("admins".equals(meta.key()) && body.containsKey("password") && body.get("password") != null) {
            String raw = String.valueOf(body.get("password"));
            if (!raw.startsWith("$2a$") && !raw.startsWith("$2b$") && !raw.startsWith("$2y$")) {
                body.put("password", PasswordUtil.hash(raw));
            }
        }
        if ("admins".equals(meta.key()) && body.get("password") == null) {
            body.remove("password");
        }
        if ("admins".equals(meta.key()) && create) {
            body.putIfAbsent("role", "ADMIN");
            body.putIfAbsent("status", 1);
        }
    }

    private void validateRequired(TableMeta meta, Map<String, Object> body) {
        for (String field : meta.requiredFields()) {
            if (body.get(field) == null) {
                throw new ApiException(meta.title() + "缺少必填字段: " + field);
            }
        }
    }

    private void validateRelation(TableMeta meta, Map<String, Object> body) {
        if (List.of("brochure", "adjust", "information").contains(meta.key()) && body.get("school_id") != null) {
            ensureExists("school", "id", body.get("school_id"), "院校不存在");
        }
        if ("users".equals(meta.key())) {
            if (body.get("target_school_id") != null) {
                ensureExists("school", "id", body.get("target_school_id"), "目标院校不存在");
            }
            if (body.get("target_major_id") != null) {
                ensureExists("major", "id", body.get("target_major_id"), "目标专业不存在");
            }
        }
    }

    private void ensureExists(String table, String idColumn, Object id, String message) {
        Long count = jdbcTemplate.queryForObject("select count(*) from `" + table + "` where `" + idColumn + "` = ?", Long.class, id);
        if (count == null || count == 0) {
            throw new ApiException(message);
        }
    }

    private void syncSchoolCharacter(Map<String, Object> body) {
        Object id = body.get("id");
        if (id == null) {
            Long latest = jdbcTemplate.queryForObject("select max(id) from school", Long.class);
            id = latest;
        }
        if (!body.containsKey("syl") && !body.containsKey("yjsy") && !body.containsKey("zhx")) {
            return;
        }
        Long count = jdbcTemplate.queryForObject("select count(*) from school_character where school_id = ?", Long.class, id);
        Object syl = body.getOrDefault("syl", 0);
        Object yjsy = body.getOrDefault("yjsy", 0);
        Object zhx = body.getOrDefault("zhx", 0);
        if (count != null && count > 0) {
            jdbcTemplate.update("update school_character set syl = ?, yjsy = ?, zhx = ? where school_id = ?", syl, yjsy, zhx, id);
        } else {
            jdbcTemplate.update("insert into school_character (school_id, syl, yjsy, zhx) values (?, ?, ?, ?)", id, syl, yjsy, zhx);
        }
    }

    private List<String> writableFields(TableMeta meta, Map<String, Object> body) {
        return body.keySet().stream()
                .filter(meta.fields()::contains)
                .filter(field -> !"schools".equals(meta.key()) || !List.of("syl", "yjsy", "zhx").contains(field))
                .toList();
    }

    private String q(String column) {
        return "`" + column + "`";
    }

    private String columnRef(TableMeta meta, String column) {
        if ("schools".equals(meta.key()) && List.of("school_name", "logo", "link", "location", "subordinate").contains(column)) {
            return "s." + q(column);
        }
        if ("schools".equals(meta.key()) && List.of("syl", "yjsy", "zhx").contains(column)) {
            return "c." + q(column);
        }
        return q(column);
    }
}
