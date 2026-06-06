-- ============================================
-- GradMate 收藏表完善脚本
-- 功能：确保收藏表结构完整
-- ============================================

-- 检查并创建 user_favorite 表（如果不存在）
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '收藏类型：school/major/brochure/adjust/information',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_name` VARCHAR(200) COMMENT '目标名称(冗余字段，方便查询)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_type_target` (`user_id`, `type`, `target_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';
