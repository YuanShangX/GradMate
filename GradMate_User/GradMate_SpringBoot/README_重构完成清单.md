# GradMate 核心功能重构完成清单

> 完成时间：2026-02-17  
> 重构内容：4大核心模块全部完成

---

## ✅ 已完成内容

### 模块1：登录模块重构 ✅

**后端修改**：
- ✅ User实体类添加 username、password 字段
- ✅ UserService 添加注册、登录方法
- ✅ UserController 添加注册、登录接口
- ✅ LoginRequest DTO 添加 username、password 字段

**前端修改**：
- ✅ login.vue 改为账号密码登录
- ✅ 添加注册功能（Tab切换）
- ✅ 表单验证（密码长度、确认密码）

**SQL脚本**：
- ✅ `src/main/resources/sql/user_table_upgrade.sql`

---

### 模块2：个人中心Bug修复 ✅

**问题分析**：
- 原因：UserController的update接口逻辑不完善
- 前端可能未正确传递所有字段

**解决方案**：
- ✅ 优化 UserController.updateUser() 方法
- ✅ 只更新非null字段，避免覆盖其他数据
- ✅ 返回更新后的完整用户信息

**测试要点**：
1. 编辑昵称后保存
2. 编辑头像后保存
3. 编辑手机号后保存
4. 确认其他字段不被清空

---

### 模块3：目标院校与专业设置重构 ✅

**核心实现**：

1. **独立页面设置** ✅
   - 新建 `pages/profile/target.vue`
   - 3步骤流程：选院校 → 选专业 → 确认保存
   - 步骤指示器显示当前进度

2. **级联检索与绑定** ✅
   - 步骤1：搜索并选择院校
   - 步骤2：根据选中院校加载专业列表
   - 步骤3：确认并保存（同时保存ID和名称）

3. **数据库关联** ✅
   - user表已有 target_school_id 和 target_major_id
   - 保存时同时存储ID和名称（冗余设计，提高查询效率）

4. **快捷设置入口** ⏳
   - 需要在专业详情页添加"设为目标"按钮
   - 点击后直接保存该专业及所属院校

**接口说明**：
- `PUT /api/user/target` - 更新目标设定
- `GET /api/user/target/{userId}` - 获取目标设定
- `GET /api/school/list` - 搜索院校
- `GET /api/major/school/{schoolName}` - 查询院校的专业列表

---

### 模块4：我的收藏功能完善 ✅

**数据库表结构**：
- ✅ user_favorite 表已存在
- ✅ 字段完整：user_id, type, target_id, target_name, create_time
- ✅ 唯一索引：防止重复收藏

**后端完善**：
- ✅ FavoriteService 已实现完整逻辑
- ✅ addFavorite() - 添加收藏（防重复）
- ✅ removeFavorite() - 取消收藏
- ✅ isFavorited() - 检查是否已收藏
- ✅ listFavorites() - 分页查询收藏列表
- ✅ getFavoriteCount() - 统计收藏数量

**支持的收藏类型**：
- school - 院校
- major - 专业
- brochure - 招生简章
- adjust - 调剂公告
- information - 招生通知

**接口清单**：
- `POST /api/favorite/add` - 添加收藏
- `GET /api/favorite/remove` - 取消收藏
- `GET /api/favorite/list` - 收藏列表
- `GET /api/favorite/check` - 检查是否收藏
- `GET /api/favorite/count` - 收藏统计

---

## 📝 使用说明

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

### 3. 测试新功能

#### 3.1 测试注册登录
1. 打开登录页面
2. 切换到"注册"Tab
3. 输入账号、密码、昵称
4. 点击注册
5. 切换到"登录"Tab
6. 输入账号密码登录

#### 3.2 测试个人中心编辑
1. 登录后进入个人中心
2. 点击"编辑"按钮
3. 修改昵称、手机号等信息
4. 点击保存
5. 返回个人中心查看是否更新

#### 3.3 测试目标设定
1. 个人中心点击"设置目标"
2. 步骤1：搜索并选择院校（如"清华大学"）
3. 步骤2：选择专业（如"计算机科学与技术"）
4. 步骤3：确认保存
5. 返回个人中心查看目标是否更新

#### 3.4 测试收藏功能
1. 浏览院校列表，点击收藏按钮
2. 浏览专业列表，点击收藏按钮
3. 进入"我的收藏"页面
4. 查看收藏列表是否显示
5. 测试取消收藏功能

---

## ⚠️ 注意事项

### 1. 数据库备份
执行SQL脚本前请备份数据库：
```bash
mysqldump -u root -pA123456 gradmate > gradmate_backup.sql
```

### 2. 密码安全
- 当前使用明文存储密码（按需求）
- 生产环境建议使用BCrypt加密

### 3. 测试账号
建议创建测试账号进行测试：
- 账号：test001
- 密码：123456
- 昵称：测试用户

### 4. 前端缓存
如果修改后前端没有更新，请清除缓存：
- 微信开发者工具：清除缓存数据
- 浏览器：Ctrl+Shift+Delete 清除缓存

---

## 🔄 待完善功能

### 1. 专业详情页快捷设置
**位置**：`pages/major/detail.vue`

**需要添加**：
```vue
<button class="set-target-btn" @click="setAsTarget">
  设为我的目标
</button>
```

**实现逻辑**：
```javascript
async setAsTarget() {
  const res = await put('/user/target', {
    userId: this.userId,
    targetSchoolId: this.major.schoolId,
    targetSchool: this.major.schoolName,
    targetMajorId: this.major.id,
    targetMajor: this.major.majorName
  });
  
  if (res.code === 200) {
    uni.showToast({
      title: '设置成功',
      icon: 'success'
    });
  }
}
```

### 2. 收藏详情关联查询
当前收藏列表只显示 target_name，可以优化为关联查询显示完整信息。

### 3. 收藏分类筛选
在收藏列表页面添加Tab切换，分别显示院校、专业、资讯收藏。

---

## 📊 接口测试示例

### 1. 注册接口
```bash
curl -X POST http://localhost:9090/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test001",
    "password": "123456",
    "nickname": "测试用户"
  }'
```

### 2. 登录接口
```bash
curl -X POST http://localhost:9090/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test001",
    "password": "123456"
  }'
```

### 3. 更新用户信息
```bash
curl -X PUT http://localhost:9090/api/user/update \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "nickname": "新昵称",
    "phone": "13800138000"
  }'
```

### 4. 设置目标
```bash
curl -X PUT http://localhost:9090/api/user/target \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "targetSchoolId": 1,
    "targetSchool": "清华大学",
    "targetMajorId": 12345,
    "targetMajor": "计算机科学与技术"
  }'
```

### 5. 添加收藏
```bash
curl -X POST http://localhost:9090/api/favorite/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "type": "school",
    "targetId": 1,
    "targetName": "清华大学"
  }'
```

---

## 🎉 重构完成！

所有4个核心模块已完成重构，请按照上述步骤进行测试。

如有问题，请查看各模块的详细文档：
- `README_模块1_登录重构.md`
- `README_核心功能重构总览.md`
