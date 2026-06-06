-- 阶段4：学习伴侣模块数据库表

-- 学习计划表
CREATE TABLE `study_plan` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '计划标题',
  `content` TEXT COMMENT '计划内容',
  `target_date` DATE COMMENT '目标日期（考研日期）',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0进行中 1已完成 2已放弃',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学习计划表';

-- 打卡记录表
CREATE TABLE `study_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `check_date` DATE NOT NULL COMMENT '打卡日期',
  `duration` INT DEFAULT 0 COMMENT '学习时长（分钟）',
  `note` VARCHAR(255) COMMENT '打卡备注',
  `mood` TINYINT COMMENT '心情：1-5星',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_date` (`user_id`, `check_date`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打卡记录表';

-- 每日任务表
CREATE TABLE `daily_task` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `task_content` VARCHAR(200) NOT NULL COMMENT '任务内容',
  `task_date` DATE NOT NULL COMMENT '任务日期',
  `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成：0未完成 1已完成',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级：1低 2中 3高',
  `complete_time` DATETIME COMMENT '完成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_date` (`user_id`, `task_date`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '每日任务表';