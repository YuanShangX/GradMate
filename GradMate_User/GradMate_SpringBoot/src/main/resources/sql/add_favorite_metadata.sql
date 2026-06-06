-- ============================================
-- 添加收藏表和历史表元数据字段
-- 用于存储专业收藏/历史的学校名称和专业代码
-- ============================================

-- 添加 user_favorite 表的 metadata 字段
ALTER TABLE `user_favorite` 
ADD COLUMN `metadata` TEXT COMMENT '元数据(JSON格式，存储额外信息如schoolName, majorCode等)' 
AFTER `target_name`;

-- 更新现有专业收藏记录，从 target_name 中提取学校名称
-- target_name 格式: "学校名 - 专业名"
UPDATE `user_favorite` 
SET `metadata` = JSON_OBJECT(
    'schoolName', SUBSTRING_INDEX(`target_name`, ' - ', 1),
    'majorName', SUBSTRING_INDEX(`target_name`, ' - ', -1)
)
WHERE `type` = 'major' AND `metadata` IS NULL;

-- 添加 user_history 表的 metadata 字段
ALTER TABLE `user_history` 
ADD COLUMN `metadata` TEXT COMMENT '元数据(JSON格式，存储额外信息如schoolName, majorCode等)' 
AFTER `target_name`;

-- 更新现有专业历史记录，从 target_name 中提取学校名称
UPDATE `user_history` 
SET `metadata` = JSON_OBJECT(
    'schoolName', SUBSTRING_INDEX(`target_name`, ' - ', 1),
    'majorName', SUBSTRING_INDEX(`target_name`, ' - ', -1)
)
WHERE `type` = 'major' AND `metadata` IS NULL;
