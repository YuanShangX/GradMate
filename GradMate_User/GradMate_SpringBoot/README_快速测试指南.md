# GradMate 快速测试指南

## 🚀 5分钟快速测试流程

### 步骤1：执行SQL脚本（1分钟）

```bash
# Windows CMD
cd D:\Desktop\Computer\GitHub\GradMate\GradMate_SpringBoot

# 执行SQL
mysql -u root -pA123456 gradmate < src\main\resources\sql\user_table_upgrade.sql
mysql -u root -pA123456 gradmate < src\main\resources\sql\user_favorite_fix.sql
```

### 步骤2：重启后端（30秒）

在IDEA中点击重启按钮，或：
```bash
mvn spring-boot:run
```

### 步骤3：测试注册登录（2分钟）

#### 3.1 使用Postman测试注册
```
POST http://localhost:9090/api/user/register
Content-Type: application/json

{
  "username": "test001",
  "password": "123456",
  "nickname": "测试用户"
}
```

**预期结果**：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "test001",
    "nickname": "测试用户",
    ...
  }
}
```

#### 3.2 测试登录
```
POST http://localhost:9090/api/user/login
Content-Type: application/json

{
  "username": "test001",
  "password": "123456"
}
```

**预期结果**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "test001",
    ...
  }
}
```

### 步骤4：测试前端（1.5分钟）

1. 打开前端项目
2. 进入登录页面
3. 切换到"注册"Tab
4. 输入账号密码注册
5. 切换到"登录"Tab登录
6. 成功跳转到校院库页面

---

## 🔍 详细测试用例

### 测试用例1：用户注册

**测试数据**：
- 账号：test001
- 密码：123456
- 昵称：测试用户

**测试步骤**：
1. 打开登录页面
2. 切换到"注册"Tab
3. 输入账号、密码、确认密码、昵称
4. 点击"立即注册"

**预期结果**：
- 显示"注册成功，请登录"
- 自动切换到登录Tab
- 账号密码自动填充

---

### 测试用例2：用户登录

**测试数据**：
- 账号：test001
- 密码：123456

**测试步骤**：
1. 在登录Tab输入账号密码
2. 点击"立即登录"

**预期结果**：
- 显示"登录成功"
- 跳转到校院库页面
- 本地存储保存用户信息

---

### 测试用例3：编辑个人信息

**测试步骤**：
1. 登录后进入个人中心
2. 点击"编辑"按钮
3. 修改昵称为"新昵称"
4. 修改手机号为"13800138000"
5. 点击"保存"

**预期结果**：
- 显示"保存成功"
- 返回个人中心
- 昵称和手机号已更新

---

### 测试用例4：设置目标院校和专业

**测试步骤**：
1. 个人中心点击"设置目标"
2. 步骤1：搜索"清华"
3. 选择"清华大学"
4. 步骤2：选择"计算机科学与技术"专业
5. 步骤3：点击"确认保存"

**预期结果**：
- 显示"设置成功"
- 返回个人中心
- 目标院校显示"清华大学"
- 目标专业显示"计算机科学与技术"

---

### 测试用例5：收藏功能

**测试步骤**：
1. 进入院校列表
2. 点击某个院校的收藏按钮
3. 进入"我的收藏"页面
4. 查看收藏列表

**预期结果**：
- 收藏按钮状态改变
- 收藏列表显示该院校
- 可以取消收藏

---

## ❌ 常见问题排查

### 问题1：SQL执行失败

**错误信息**：`Duplicate column name 'username'`

**原因**：字段已存在

**解决**：跳过该SQL，字段已添加

---

### 问题2：登录失败 - 用户不存在

**原因**：未注册或注册失败

**解决**：
1. 先测试注册接口
2. 检查数据库是否有该用户
```sql
SELECT * FROM user WHERE username = 'test001';
```

---

### 问题3：前端登录后跳转失败

**错误信息**：`navigateTo fail`

**原因**：pages.json配置问题

**解决**：
1. 检查 pages.json 是否包含目标页面
2. 使用 switchTab 而不是 navigateTo

---

### 问题4：收藏列表为空

**原因**：
1. 未添加收藏
2. userId 不匹配

**解决**：
1. 先测试添加收藏接口
2. 检查 localStorage 中的 userId
3. 查询数据库确认数据
```sql
SELECT * FROM user_favorite WHERE user_id = 1;
```

---

## 📊 数据库验证SQL

### 检查用户表结构
```sql
DESC user;
```

**预期结果**：应该包含 username 和 password 字段

### 检查收藏表
```sql
DESC user_favorite;
SELECT * FROM user_favorite;
```

### 检查测试用户
```sql
SELECT * FROM user WHERE username = 'test001';
```

### 检查目标设定
```sql
SELECT id, username, target_school, target_major 
FROM user 
WHERE id = 1;
```

---

## 🎯 性能测试

### 测试院校搜索性能
```sql
EXPLAIN SELECT * FROM school 
WHERE school_name LIKE '%清华%' 
LIMIT 20;
```

### 测试专业查询性能
```sql
EXPLAIN SELECT * FROM major 
WHERE school_name = '清华大学' 
LIMIT 100;
```

### 测试收藏查询性能
```sql
EXPLAIN SELECT * FROM user_favorite 
WHERE user_id = 1 
ORDER BY create_time DESC;
```

---

## ✅ 测试完成检查清单

- [ ] SQL脚本执行成功
- [ ] 后端服务启动正常
- [ ] 注册接口测试通过
- [ ] 登录接口测试通过
- [ ] 前端注册功能正常
- [ ] 前端登录功能正常
- [ ] 个人信息编辑正常
- [ ] 目标设定功能正常
- [ ] 收藏功能正常
- [ ] 数据库数据正确

---

**测试完成后，请在 README_重构完成清单.md 中标记完成状态！**
