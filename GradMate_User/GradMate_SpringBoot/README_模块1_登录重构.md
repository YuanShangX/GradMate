# 模块1：登录模块重构 - 账号密码登录

## 一、修复思路

将现有的微信登录改为传统的"账号+密码"登录方式，同时保留微信登录作为备用方案。

## 二、数据库表结构修改

### 1. 执行SQL脚本

```sql
-- 文件位置：src/main/resources/sql/user_table_upgrade.sql

ALTER TABLE `user` 
ADD COLUMN `username` VARCHAR(50) UNIQUE COMMENT '登录账号' AFTER `id`,
ADD COLUMN `password` VARCHAR(100) COMMENT '登录密码(明文)' AFTER `username`,
MODIFY COLUMN `openid` VARCHAR(100) NULL COMMENT '微信openid(可选)';

ALTER TABLE `user` ADD INDEX `idx_username` (`username`);
```

### 2. 表结构说明

| 字段 | 类型 | 说明 | 必填 |
|------|------|------|------|
| id | BIGINT | 主键 | 是 |
| username | VARCHAR(50) | 登录账号（唯一） | 是 |
| password | VARCHAR(100) | 登录密码（明文存储） | 是 |
| openid | VARCHAR(100) | 微信openid（可选） | 否 |
| nickname | VARCHAR(50) | 昵称 | 否 |
| avatar | VARCHAR(255) | 头像URL | 否 |
| phone | VARCHAR(20) | 手机号 | 否 |

## 三、后端代码实现

### 1. User实体类已更新
- 添加 `username` 字段
- 添加 `password` 字段
- `openid` 改为可选

### 2. UserService新增方法
- `getUserByUsername(String username)` - 根据用户名查询
- `register(String username, String password, String nickname)` - 用户注册
- `login(String username, String password)` - 账号密码登录
- `loginByWechat(...)` - 微信登录（保留兼容）

### 3. UserController新增接口

#### 3.1 用户注册
```
POST /api/user/register
Content-Type: application/json

{
  "username": "test001",
  "password": "123456",
  "nickname": "测试用户"
}
```

#### 3.2 用户登录
```
POST /api/user/login
Content-Type: application/json

{
  "username": "test001",
  "password": "123456"
}
```

#### 3.3 微信登录（保留）
```
POST /api/user/login/wechat
Content-Type: application/json

{
  "code": "wx_openid_xxx",
  "nickname": "微信用户",
  "avatar": "https://..."
}
```

## 四、前端修改

### 1. 登录页面修改

文件：`GradMate_Uniapp/pages/login/login.vue`

需要修改为账号密码输入框，详见前端修改文档。

## 五、测试步骤

### 1. 执行SQL脚本
```bash
mysql -u root -pA123456 gradmate < src/main/resources/sql/user_table_upgrade.sql
```

### 2. 重启后端服务

### 3. 测试注册接口
使用Postman或Apifox测试注册接口

### 4. 测试登录接口
使用注册的账号密码测试登录

### 5. 前端联调
修改前端登录页面后进行联调测试

## 六、注意事项

1. **密码明文存储**：按需求使用明文存储，生产环境建议加密
2. **用户名唯一性**：数据库已添加唯一索引
3. **兼容性**：保留了微信登录接口，不影响现有功能
4. **错误处理**：Service层会抛出异常，Controller层统一捕获返回错误信息
