# GradMate 核心功能重构总览

> 重构时间：2026-02-17  
> 重构内容：登录、个人中心、目标设定、收藏功能

---

## 📋 重构模块清单

### ✅ 模块1：登录模块重构
- **文档**：`README_模块1_登录重构.md`
- **状态**：已完成后端代码
- **内容**：账号密码登录替代微信登录
- **SQL脚本**：`src/main/resources/sql/user_table_upgrade.sql`

### ⏳ 模块2：个人中心Bug修复
- **问题**：登录后无法修改/编辑信息
- **原因**：前端未正确传递userId，后端更新逻辑不完善
- **解决**：已优化UserController的update接口

### ⏳ 模块3：目标院校与专业设置重构
- **核心需求**：
  1. 独立页面设置（路由跳转）
  2. 级联检索与绑定（院校→专业联动）
  3. 专业详情页快捷设置
- **SQL脚本**：`src/main/resources/sql/user_table_upgrade.sql`（已包含）

### ⏳ 模块4：我的收藏功能完善
- **问题**：收藏列表为空
- **原因**：数据库表可能未创建或查询逻辑有问题
- **SQL脚本**：`src/main/resources/sql/user_favorite_fix.sql`

---

## 🔧 快速开始

### 1. 执行SQL脚本

```bash
# 进入MySQL
mysql -u root -pA123456

# 选择数据库
USE gradmate;

# 执行用户表升级脚本
SOURCE D:/Desktop/Computer/GitHub/GradMate/GradMate_SpringBoot/src/main/resources/sql/user_table_upgrade.sql;

# 执行收藏表修复脚本
SOURCE D:/Desktop/Computer/GitHub/GradMate/GradMate_SpringBoot/src/main/resources/sql/user_favorite_fix.sql;
```

### 2. 重启后端服务

在IDEA中重新运行 `GradMateSpringBootApplication`

### 3. 测试接口

使用Postman/Apifox测试新接口

### 4. 前端修改

根据各模块文档修改前端代码

---

## 📊 数据库表关系

```
user (用户表)
├── id (主键)
├── username (登录账号) ← 新增
├── password (登录密码) ← 新增
├── target_school_id → school.id (目标院校ID)
└── target_major_id → major.id (目标专业ID)

school (院校表)
├── id (主键)
└── school_name (院校名称)

major (专业表)
├── id (主键)
├── school_name (院校名称)
└── major_name (专业名称)

user_favorite (收藏表)
├── id (主键)
├── user_id → user.id
├── type (类型：school/major/brochure/adjust/information)
└── target_id (目标ID)
```

---

## 🎯 核心接口清单

### 用户模块
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info/{id}` - 获取用户信息
- `PUT /api/user/update` - 更新用户信息
- `PUT /api/user/target` - 更新目标设定
- `GET /api/user/target/{userId}` - 获取目标设定

### 收藏模块
- `POST /api/favorite/add` - 添加收藏
- `GET /api/favorite/remove` - 取消收藏
- `GET /api/favorite/list` - 收藏列表
- `GET /api/favorite/check` - 检查是否收藏
- `GET /api/favorite/count` - 收藏统计

---

## ⚠️ 重要提示

1. **数据库备份**：执行SQL脚本前请备份数据库
2. **密码明文**：按需求使用明文存储，生产环境需加密
3. **测试数据**：建议先创建测试账号进行测试
4. **前端同步**：后端修改完成后需同步修改前端

---

## 📝 后续任务

### 前端待修改页面
1. `pages/login/login.vue` - 登录页面
2. `pages/profile/edit.vue` - 编辑个人信息页面
3. `pages/profile/target.vue` - 目标设定页面（新建）
4. `pages/major/detail.vue` - 专业详情页（添加快捷设置按钮）
5. `pages/profile/favorite.vue` - 收藏列表页面

### 后端待完善功能
1. 目标设定页面的级联查询接口
2. 专业详情页的快捷设置接口
3. 收藏功能的详细信息查询（关联查询）

---

**重构完成后请参考各模块详细文档进行测试！**
