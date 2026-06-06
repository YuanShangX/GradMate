# GradMate 阶段5：个人中心模块 - 开发规划

## 规划时间
2026-02-13

---

## 阶段5目标

根据 `Skills/GradMate_模块化开发流程规划.md`，阶段5的目标是：

> 用户可以设定目标院校/专业，收藏感兴趣的内容，为AI推荐提供数据基础。

---

## 功能概述

### 核心功能
1. **收藏管理**：收藏院校、专业、资讯
2. **目标设定**：设定目标院校和专业
3. **浏览历史**：记录用户浏览记录（可选）
4. **个人信息**：完善用户资料

### 业务价值
- 提升用户粘性（收藏功能）
- 为AI推荐提供数据基础（目标院校/专业）
- 完善用户画像（浏览历史）
- 实现阶段2和阶段3预留的收藏按钮功能

---

## 数据库设计

### 新增表：user_favorite（收藏表）

```sql
CREATE TABLE `user_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '收藏类型：school/major/brochure/adjust/information',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_type_target` (`user_id`, `type`, `target_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_type` (`type`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收藏表';
```

**字段说明**：
- `user_id`：用户ID，关联user表
- `type`：收藏类型
  - `school`：院校
  - `major`：专业
  - `brochure`：招生简章
  - `adjust`：调剂公告
  - `information`：招生信息
- `target_id`：目标ID（根据type关联不同的表）
- `uk_user_type_target`：唯一索引，防止重复收藏

### 可选表：user_history（浏览历史表）

```sql
CREATE TABLE `user_history` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '浏览类型：school/major/brochure/adjust/information',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `view_time` DATETIME DEFAULT CUR