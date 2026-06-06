-- 为study_record表添加学习类型字段
ALTER TABLE study_record ADD COLUMN study_type VARCHAR(50) DEFAULT 'other' COMMENT '学习类型：major(专业课),english(英语),math(数学),coding(编程),exercise(刷题),reading(阅读),other(其他)';

-- 查看表结构
DESC study_record;
