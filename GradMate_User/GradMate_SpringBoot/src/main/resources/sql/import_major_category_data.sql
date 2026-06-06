-- 从major表导入数据到major_category表
-- 注意：这个脚本会根据专业代码规则自动判断学硕和专硕

-- 清空表（如果需要重新导入）
-- TRUNCATE TABLE major_category;

-- 插入学硕数据（专业代码以02开头，且不是025开头的专硕代码）
INSERT INTO major_category (category_code, category_name, discipline_code, discipline_name, major_code, major_name, degree_type, sort_order)
SELECT DISTINCT
    SUBSTRING(major_code, 1, 2) AS category_code,
    CASE SUBSTRING(major_code, 1, 2)
        WHEN '01' THEN '哲学'
        WHEN '02' THEN '经济学'
        WHEN '03' THEN '法学'
        WHEN '04' THEN '教育学'
        WHEN '05' THEN '文学'
        WHEN '06' THEN '历史学'
        WHEN '07' THEN '理学'
        WHEN '08' THEN '工学'
        WHEN '09' THEN '农学'
        WHEN '10' THEN '医学'
        WHEN '11' THEN '军事学'
        WHEN '12' THEN '管理学'
        WHEN '13' THEN '艺术学'
        WHEN '14' THEN '交叉学科'
        ELSE '其他'
    END AS category_name,
    SUBSTRING(major_code, 1, 4) AS discipline_code,
    '' AS discipline_name,  -- 一级学科名称需要后续更新
    major_code,
    major_name,
    'academic' AS degree_type,
    0 AS sort_order
FROM major
WHERE major_code IS NOT NULL 
    AND major_code != ''
    AND LENGTH(major_code) >= 6
    AND major_code NOT LIKE '%5_00'  -- 排除专硕代码（第4位是5）
    AND major_code NOT REGEXP '^[0-9]{2}5[0-9]'  -- 排除专硕代码
ORDER BY major_code;

-- 插入专硕数据（专业代码第4位是5，如025100）
INSERT INTO major_category (category_code, category_name, discipline_code, discipline_name, major_code, major_name, degree_type, sort_order)
SELECT DISTINCT
    SUBSTRING(major_code, 1, 2) AS category_code,
    CASE SUBSTRING(major_code, 1, 2)
        WHEN '01' THEN '哲学'
        WHEN '02' THEN '经济学'
        WHEN '03' THEN '法学'
        WHEN '04' THEN '教育学'
        WHEN '05' THEN '文学'
        WHEN '06' THEN '历史学'
        WHEN '07' THEN '理学'
        WHEN '08' THEN '工学'
        WHEN '09' THEN '农学'
        WHEN '10' THEN '医学'
        WHEN '11' THEN '军事学'
        WHEN '12' THEN '管理学'
        WHEN '13' THEN '艺术学'
        WHEN '14' THEN '交叉学科'
        ELSE '其他'
    END AS category_name,
    SUBSTRING(major_code, 1, 4) AS discipline_code,
    major_name AS discipline_name,  -- 专硕的一级学科名称就是专业名称
    major_code,
    major_name,
    'professional' AS degree_type,
    0 AS sort_order
FROM major
WHERE major_code IS NOT NULL 
    AND major_code != ''
    AND LENGTH(major_code) >= 6
    AND major_code REGEXP '^[0-9]{2}5[0-9]'  -- 只选择专硕代码（第4位是5）
ORDER BY major_code;

-- 更新一级学科名称（从代码以00结尾的记录中获取）
UPDATE major_category mc1
JOIN (
    SELECT DISTINCT 
        SUBSTRING(major_code, 1, 4) AS discipline_code,
        major_name
    FROM major
    WHERE major_code LIKE '%00'
        AND LENGTH(major_code) = 6
) mc2 ON mc1.discipline_code = mc2.discipline_code
SET mc1.discipline_name = mc2.major_name
WHERE mc1.degree_type = 'academic'
    AND (mc1.discipline_name IS NULL OR mc1.discipline_name = '');

-- 更新排序字段
UPDATE major_category 
SET sort_order = id;
