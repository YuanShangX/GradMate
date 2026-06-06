-- 专业类型表（独立表，不关联其他表）
CREATE TABLE IF NOT EXISTS `major_category` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_code` VARCHAR(2) NOT NULL COMMENT '学科门类代码（2位）',
  `category_name` VARCHAR(50) NOT NULL COMMENT '学科门类名称',
  `discipline_code` VARCHAR(4) NOT NULL COMMENT '一级学科代码（4位）',
  `discipline_name` VARCHAR(100) NOT NULL COMMENT '一级学科名称',
  `major_code` VARCHAR(10) NOT NULL COMMENT '专业代码（6位或更多）',
  `major_name` VARCHAR(100) NOT NULL COMMENT '专业名称',
  `degree_type` VARCHAR(20) NOT NULL COMMENT '学位类型：academic-学硕，professional-专硕',
  `sort_order` INT DEFAULT 0 COMMENT '排序字段',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_category` (`category_code`),
  INDEX `idx_discipline` (`discipline_code`),
  INDEX `idx_major` (`major_code`),
  INDEX `idx_degree_type` (`degree_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业类型表';

-- 插入经济学门类数据（示例）
-- 学科门类：02-经济学

-- 一级学科：0201-理论经济学（学硕）
INSERT INTO `major_category` (`category_code`, `category_name`, `discipline_code`, `discipline_name`, `major_code`, `major_name`, `degree_type`, `sort_order`) VALUES
('02', '经济学', '0201', '理论经济学', '020101', '政治经济学', 'academic', 1),
('02', '经济学', '0201', '理论经济学', '020102', '经济思想史', 'academic', 2),
('02', '经济学', '0201', '理论经济学', '020103', '经济史', 'academic', 3),
('02', '经济学', '0201', '理论经济学', '020104', '西方经济学', 'academic', 4),
('02', '经济学', '0201', '理论经济学', '020105', '世界经济', 'academic', 5),
('02', '经济学', '0201', '理论经济学', '020106', '人口、资源与环境经济学', 'academic', 6);

-- 一级学科：0202-应用经济学（学硕）
INSERT INTO `major_category` (`category_code`, `category_name`, `discipline_code`, `discipline_name`, `major_code`, `major_name`, `degree_type`, `sort_order`) VALUES
('02', '经济学', '0202', '应用经济学', '020201', '国民经济学', 'academic', 1),
('02', '经济学', '0202', '应用经济学', '020202', '区域经济学', 'academic', 2),
('02', '经济学', '0202', '应用经济学', '020203', '财政学', 'academic', 3),
('02', '经济学', '0202', '应用经济学', '020204', '金融学', 'academic', 4),
('02', '经济学', '0202', '应用经济学', '020205', '产业经济学', 'academic', 5),
('02', '经济学', '0202', '应用经济学', '020206', '国际贸易学', 'academic', 6),
('02', '经济学', '0202', '应用经济学', '020207', '劳动经济学', 'academic', 7),
('02', '经济学', '0202', '应用经济学', '020208', '统计学', 'academic', 8),
('02', '经济学', '0202', '应用经济学', '020209', '数量经济学', 'academic', 9),
('02', '经济学', '0202', '应用经济学', '020210', '国防经济', 'academic', 10);

-- 专硕类别
INSERT INTO `major_category` (`category_code`, `category_name`, `discipline_code`, `discipline_name`, `major_code`, `major_name`, `degree_type`, `sort_order`) VALUES
('02', '经济学', '0251', '金融', '025100', '金融', 'professional', 1),
('02', '经济学', '0252', '应用统计', '025200', '应用统计', 'professional', 2),
('02', '经济学', '0253', '税务', '025300', '税务', 'professional', 3),
('02', '经济学', '0254', '国际商务', '025400', '国际商务', 'professional', 4),
('02', '经济学', '0255', '保险', '025500', '保险', 'professional', 5),
('02', '经济学', '0256', '资产评估', '025600', '资产评估', 'professional', 6),
('02', '经济学', '0258', '数字经济', '025800', '数字经济', 'professional', 7);

-- 一级学科：0270-统计学（学硕）
INSERT INTO `major_category` (`category_code`, `category_name`, `discipline_code`, `discipline_name`, `major_code`, `major_name`, `degree_type`, `sort_order`) VALUES
('02', '经济学', '0270', '统计学', '027000', '统计学', 'academic', 1);
