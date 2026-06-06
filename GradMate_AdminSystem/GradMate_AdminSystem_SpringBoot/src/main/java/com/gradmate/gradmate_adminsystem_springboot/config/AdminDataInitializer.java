package com.gradmate.gradmate_adminsystem_springboot.config;

import com.gradmate.gradmate_adminsystem_springboot.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AdminDataInitializer implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public AdminDataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
                create table if not exists admin_user (
                  id bigint not null auto_increment comment '管理员ID',
                  username varchar(50) not null comment '登录账号',
                  password varchar(120) not null comment 'BCrypt密码哈希',
                  nickname varchar(50) null default null comment '昵称',
                  role varchar(30) null default 'ADMIN' comment '角色',
                  status tinyint null default 1 comment '状态：1启用 0禁用',
                  last_login_time datetime null default null comment '最后登录时间',
                  create_time datetime null default current_timestamp comment '创建时间',
                  update_time datetime null default current_timestamp on update current_timestamp comment '更新时间',
                  primary key (id),
                  unique key uk_admin_username (username),
                  key idx_admin_status (status)
                ) engine=InnoDB default charset=utf8mb4 comment='管理员用户表'
                """);
        jdbcTemplate.execute("""
                create table if not exists admin_operation_log (
                  id bigint not null auto_increment comment '日志ID',
                  admin_id bigint null default null comment '管理员ID',
                  admin_username varchar(50) null default null comment '管理员账号',
                  module_name varchar(100) null default null comment '模块名称',
                  resource_key varchar(60) null default null comment '资源标识',
                  action_type varchar(30) not null comment '动作类型',
                  target_id varchar(80) null default null comment '目标ID',
                  request_method varchar(10) null default null comment '请求方法',
                  request_uri varchar(255) null default null comment '请求地址',
                  result_status varchar(20) null default null comment '执行结果',
                  result_message varchar(500) null default null comment '结果信息',
                  ip_address varchar(64) null default null comment 'IP地址',
                  user_agent varchar(500) null default null comment 'User-Agent',
                  create_time datetime null default current_timestamp comment '创建时间',
                  primary key (id),
                  key idx_log_admin_id (admin_id),
                  key idx_log_action_type (action_type),
                  key idx_log_resource_key (resource_key),
                  key idx_log_create_time (create_time)
                ) engine=InnoDB default charset=utf8mb4 comment='管理员系统操作日志表'
                """);
        Long count = jdbcTemplate.queryForObject("select count(*) from admin_user where username = 'admin'", Long.class);
        if (count == null || count == 0) {
            jdbcTemplate.update("""
                    insert into admin_user (username, password, nickname, role, status)
                    values (?, ?, ?, ?, ?)
                    """, "admin", PasswordUtil.hash("admin123"), "系统管理员", "SUPER_ADMIN", 1);
        }
    }
}
