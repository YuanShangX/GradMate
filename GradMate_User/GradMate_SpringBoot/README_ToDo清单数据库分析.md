# ToDo清单数据库分析

## 现有表结构

### 1. daily_task（每日任务表）

```sql
DESC daily_task;
```

| 字段 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| id | bigint | 主键 | 自增 |
| user_id | bigint | 用户ID | - |
| task_content | varchar(200) | 任务内容 | - |
| task_date | date | 任务日期 | - |
| is_completed | tinyint | 是否完成 | 0 |
| priority | tinyint | 优先级 | 1 |
| complete_time | datetime | 完成时间 | NULL |
| create_time | datetime | 创建时间 | CURRENT_TIMESTAMP |
| update_time | datetime | 更新时间 | CURRENT_TIMESTAMP |
| color_theme | varchar(20) | 颜色主题 | blue |
| estimated_duration | int | 预计时长(分钟) | 25 |

**现有数据**: 6条记录

### 2. study_plan（学习计划表）

```sql
DESC study_plan;
```

| 字段 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| id | bigint | 主键 | 自增 |
| user_id | bigint | 用户ID | - |
| title | varchar(100) | 计划标题 | - |
| content | text | 计划内容 | NULL |
| target_date | date | 目标日期 | NULL |
| status | tinyint | 状态 | 0 |
| create_time | datetime | 创建时间 | CURRENT_TIMESTAMP |
| update_time | datetime | 更新时间 | CURRENT_TIMESTAMP |
| background_image | varchar(255) | 背景图片 | NULL |
| start_date | date | 开始日期 | NULL |
| total_days | int | 总天数 | NULL |

**现有数据**: 1条记录

### 3. study_record（学习记录表）

```sql
DESC study_record;
```

| 字段 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| id | bigint | 主键 | 自增 |
| user_id | bigint | 用户ID | - |
| check_date | date | 打卡日期 | - |
| duration | int | 学习时长(分钟) | 0 |
| note | varchar(255) | 备注 | NULL |
| mood | tinyint | 心情 | NULL |
| create_time | datetime | 创建时间 | CURRENT_TIMESTAMP |
| focus_count | int | 专注次数 | 0 |
| pause_count | int | 暂停次数 | 0 |

**现有数据**: 1条记录

## 优化建议

### 需要添加的字段

#### daily_task表优化

```sql
-- 添加实际时长字段
ALTER TABLE daily_task ADD COLUMN actual_duration INT DEFAULT 0 COMMENT '实际时长(分钟)';

-- 添加标签字段
ALTER TABLE daily_task ADD COLUMN tags VARCHAR(200) COMMENT '标签(逗号分隔)';

-- 添加父任务ID（用于子任务）
ALTER TABLE daily_task ADD COLUMN parent_task_id BIGINT COMMENT '父任务ID';

-- 添加重复类型字段
ALTER TABLE daily_task ADD COLUMN repeat_type VARCHAR(20) DEFAULT 'none' COMMENT '重复类型: none/daily/weekly/monthly';

-- 添加排序字段
ALTER TABLE daily_task ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序顺序';

-- 添加任务描述字段
ALTER TABLE daily_task ADD COLUMN description TEXT COMMENT '任务描述';

-- 添加提醒时间字段
ALTER TABLE daily_task ADD COLUMN reminder_time DATETIME COMMENT '提醒时间';

-- 添加任务状态字段（待办/进行中/已完成）
ALTER TABLE daily_task ADD COLUMN status VARCHAR(20) DEFAULT 'todo' COMMENT '任务状态: todo/doing/done';
```

### 建议创建的新表

#### 1. task_tags（任务标签表）

```sql
CREATE TABLE task_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_color VARCHAR(20) DEFAULT '#006cff' COMMENT '标签颜色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_tag (user_id, tag_name)
) COMMENT '任务标签表';
```

#### 2. task_subtasks（子任务表）

```sql
CREATE TABLE task_subtasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_task_id BIGINT NOT NULL COMMENT '父任务ID',
    subtask_content VARCHAR(200) NOT NULL COMMENT '子任务内容',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_task (parent_task_id)
) COMMENT '子任务表';
```

#### 3. task_reminders（任务提醒表）

```sql
CREATE TABLE task_reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    reminder_time DATETIME NOT NULL COMMENT '提醒时间',
    is_sent TINYINT DEFAULT 0 COMMENT '是否已发送',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id),
    INDEX idx_reminder_time (reminder_time)
) COMMENT '任务提醒表';
```

### 索引优化

```sql
-- daily_task表添加索引
ALTER TABLE daily_task ADD INDEX idx_user_date (user_id, task_date);
ALTER TABLE daily_task ADD INDEX idx_status (status);
ALTER TABLE daily_task ADD INDEX idx_priority (priority);

-- study_record表添加索引
ALTER TABLE study_record ADD INDEX idx_user_date (user_id, check_date);

-- study_plan表添加索引
ALTER TABLE study_plan ADD INDEX idx_user_status (user_id, status);
```

## 数据迁移建议

### 1. 迁移现有数据

```sql
-- 将is_completed转换为status
UPDATE daily_task SET status = 'done' WHERE is_completed = 1;
UPDATE daily_task SET status = 'todo' WHERE is_completed = 0;
```

### 2. 清理测试数据（可选）

```sql
-- 查看现有数据
SELECT * FROM daily_task;
SELECT * FROM study_plan;
SELECT * FROM study_record;

-- 如果需要清理测试数据
-- DELETE FROM daily_task WHERE user_id = 1;
-- DELETE FROM study_plan WHERE user_id = 1;
-- DELETE FROM study_record WHERE user_id = 1;
```

## 完整的优化SQL脚本

```sql
-- ============================================
-- ToDo清单模块数据库优化脚本
-- 执行前请备份数据库！
-- ============================================

USE gradmate;

-- 1. 优化daily_task表
ALTER TABLE daily_task 
    ADD COLUMN actual_duration INT DEFAULT 0 COMMENT '实际时长(分钟)',
    ADD COLUMN tags VARCHAR(200) COMMENT '标签(逗号分隔)',
    ADD COLUMN parent_task_id BIGINT COMMENT '父任务ID',
    ADD COLUMN repeat_type VARCHAR(20) DEFAULT 'none' COMMENT '重复类型',
    ADD COLUMN sort_order INT DEFAULT 0 COMMENT '排序顺序',
    ADD COLUMN description TEXT COMMENT '任务描述',
    ADD COLUMN reminder_time DATETIME COMMENT '提醒时间',
    ADD COLUMN status VARCHAR(20) DEFAULT 'todo' COMMENT '任务状态';

-- 2. 添加索引
ALTER TABLE daily_task 
    ADD INDEX idx_user_date (user_id, task_date),
    ADD INDEX idx_status (status),
    ADD INDEX idx_priority (priority);

ALTER TABLE study_record 
    ADD INDEX idx_user_date (user_id, check_date);

ALTER TABLE study_plan 
    ADD INDEX idx_user_status (user_id, status);

-- 3. 创建任务标签表
CREATE TABLE IF NOT EXISTS task_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_color VARCHAR(20) DEFAULT '#006cff' COMMENT '标签颜色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_tag (user_id, tag_name)
) COMMENT '任务标签表';

-- 4. 创建子任务表
CREATE TABLE IF NOT EXISTS task_subtasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_task_id BIGINT NOT NULL COMMENT '父任务ID',
    subtask_content VARCHAR(200) NOT NULL COMMENT '子任务内容',
    is_completed TINYINT DEFAULT 0 COMMENT '是否完成',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_task (parent_task_id)
) COMMENT '子任务表';

-- 5. 创建任务提醒表
CREATE TABLE IF NOT EXISTS task_reminders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT NOT NULL COMMENT '任务ID',
    reminder_time DATETIME NOT NULL COMMENT '提醒时间',
    is_sent TINYINT DEFAULT 0 COMMENT '是否已发送',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id),
    INDEX idx_reminder_time (reminder_time)
) COMMENT '任务提醒表';

-- 6. 迁移现有数据
UPDATE daily_task SET status = 'done' WHERE is_completed = 1;
UPDATE daily_task SET status = 'todo' WHERE is_completed = 0;

-- 7. 插入默认标签
INSERT INTO task_tags (user_id, tag_name, tag_color) VALUES
(1, '学习', '#006cff'),
(1, '工作', '#52c41a'),
(1, '生活', '#faad14'),
(1, '运动', '#ff4d4f'),
(1, '娱乐', '#722ed1');

-- 完成！
SELECT '数据库优化完成！' as message;
```

## 执行步骤

1. **备份数据库**
```bash
mysqldump -u root -pA123456 gradmate > gradmate_backup_$(date +%Y%m%d).sql
```

2. **执行优化脚本**
```bash
mysql -u root -pA123456 gradmate < optimize_todo_database.sql
```

3. **验证结果**
```sql
-- 查看表结构
DESC daily_task;
DESC task_tags;
DESC task_subtasks;
DESC task_reminders;

-- 查看数据
SELECT * FROM daily_task LIMIT 5;
SELECT * FROM task_tags;
```

## 注意事项

1. ⚠️ 执行前务必备份数据库
2. ⚠️ 在测试环境先验证
3. ⚠️ 检查现有数据是否会受影响
4. ⚠️ 更新后需要同步修改Java实体类
5. ⚠️ 需要更新前端代码以支持新字段

---

**创建时间**: 2026-02-20
**状态**: 待执行
