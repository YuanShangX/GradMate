-- ============================================
-- GradMate 用户表升级脚本
-- 功能：添加账号密码登录支持
-- ============================================

-- 1. 修改 user 表，添加账号和密码字段
ALTER TABLE `user` 
ADD COLUMN `username` VARCHAR(50) UNIQUE COMMENT '登录账号' AFTER `id`,
ADD COLUMN `password` VARCHAR(100) COMMENT '登录密码(明文)' AFTER `username`,
MODIFY COLUMN `openid` VARCHAR(100) NULL COMMENT '微信openid(可选)';

-- 2. 添加索引
ALTER TABLE `user` ADD INDEX `idx_username` (`username`);

-- 3. 修改目标院校和专业字段，确保ID字段存在
-- (如果已存在则跳过)
ALTER TABLE `user` 
MODIFY COLUMN `target_school_id` BIGINT COMMENT '目标院校ID',
MODIFY COLUMN `target_major_id` BIGINT COMMENT '目标专业ID';

-- 4. 添加外键约束（可选，根据需要）
-- ALTER TABLE `user` 
-- ADD CONSTRAINT `fk_user_school` FOREIGN KEY (`target_school_id`) REFERENCES `school`(`id`) ON DELETE SET NULL,
-- ADD CONSTRAINT `fk_user_major` FOREIGN KEY (`target_major_id`) REFERENCES `major`(`id`) ON DELETE SET NULL;
