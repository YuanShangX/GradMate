# 专业收藏跳转修复 - 快速指南

## 问题
从"我的收藏"和"浏览历史"点击专业时显示"专业不存在"

## 快速修复步骤

### 1️⃣ 执行数据库脚本（必须）
```cmd
cd GradMate_SpringBoot
apply_metadata_fix.cmd
```

或手动执行：
```bash
mysql -h localhost -u root -pA123456 gradmate < src/main/resources/sql/add_favorite_metadata.sql
```

### 2️⃣ 重启后端服务
```cmd
cd GradMate_SpringBoot
restart_service.cmd
```

### 3️⃣ 测试功能
1. 打开应用，进入任意专业详情页
2. 点击"收藏"按钮
3. 进入"个人中心" -> "我的收藏"
4. 点击刚才收藏的专业
5. 验证是否正确跳转

### 4️⃣ 测试 API（可选）
```powershell
cd GradMate_SpringBoot
powershell -ExecutionPolicy Bypass -File test_favorite_metadata.ps1
```

## 修复内容

### 数据库
- ✅ `user_favorite` 表添加 `metadata` 字段
- ✅ `user_history` 表添加 `metadata` 字段
- ✅ 自动更新现有数据

### 后端
- ✅ `UserFavorite` 和 `UserHistory` 实体添加 metadata 字段
- ✅ `FavoriteService` 存储完整元数据（schoolName, majorCode, majorName）
- ✅ `HistoryService` 支持元数据参数
- ✅ `HistoryController` 支持可选 metadata 参数
- ✅ `MajorService` 改进查询逻辑，支持 majorCode 或 majorName 查询
- ✅ `MajorController` 支持可选 majorName 参数

### 前端
- ✅ `favorite.vue` 智能解析 metadata 或 targetName 进行跳转
- ✅ `history.vue` 智能解析 metadata 或 targetName 进行跳转
- ✅ 所有专业收藏/历史都跳转到院校-专业详情页（schoolDetail.vue）
- ✅ 新数据使用精确查询，旧数据使用模糊查询

## 技术细节

### 元数据格式
```json
{
  "schoolName": "清华大学",
  "majorCode": "081200",
  "majorName": "计算机科学与技术"
}
```

### 跳转逻辑
```javascript
// 1. 优先从 metadata 获取完整信息
if (item.metadata) {
  const metadata = JSON.parse(item.metadata)
  schoolName = metadata.schoolName
  majorCode = metadata.majorCode
  majorName = metadata.majorName
}

// 2. 如果没有 metadata，从 targetName 提取（格式: "学校名 - 专业名"）
if (!schoolName && item.targetName.includes(' - ')) {
  const nameParts = item.targetName.split(' - ')
  schoolName = nameParts[0]
  majorName = nameParts[1]
}

// 3. 只要有学校名称，就跳转到院校-专业详情页
if (schoolName) {
  uni.navigateTo({ 
    url: `/pages/major/schoolDetail?schoolName=${schoolName}&majorCode=${majorCode}&majorName=${majorName}` 
  })
}
```

### 后端 API 改进
`/api/major/schoolMajorDetail` 接口现在支持：
- `schoolName`（必填）：学校名称
- `majorCode`（可选）：专业代码，优先使用精确匹配
- `majorName`（可选）：专业名称，当 majorCode 为空时使用模糊匹配

**查询逻辑**：
- 新数据：schoolName + majorCode → 精确查询 ✅
- 旧数据：schoolName + majorName → 模糊查询 ✅
- 都能跳转到院校-专业详情页！

## 注意事项

⚠️ **旧数据处理**：
- 现有收藏/历史记录会从 `targetName` 中提取学校名称和专业名称
- 通过 schoolName + majorName 模糊匹配查询专业信息
- 同样跳转到院校-专业详情页（schoolDetail.vue）

✅ **新数据完整**：
- 修复后新添加的收藏/历史会包含完整元数据
- 通过 schoolName + majorCode 精确查询
- 查询速度更快，结果更准确

💡 **用户体验**：
- 无论新旧数据，都能跳转到正确的院校-专业详情页
- 新数据显示精确匹配的专业信息
- 旧数据显示该学校该专业名称的所有相关专业

## 验证成功标志

- [ ] 数据库中 `user_favorite` 和 `user_history` 表有 `metadata` 字段
- [ ] 新添加的专业收藏包含 JSON 格式的 metadata
- [ ] 点击收藏的专业能正确跳转到院校-专业详情页
- [ ] 点击历史记录的专业能正确跳转到院校-专业详情页
- [ ] 旧数据也能跳转到院校-专业详情页（通过 majorName 模糊查询）

## 相关文件

- 📄 详细说明：`README_专业收藏跳转修复.md`
- 🗄️ SQL 脚本：`src/main/resources/sql/add_favorite_metadata.sql`
- 🧪 测试脚本：`test_favorite_metadata.ps1`
- ⚙️ 快速执行：`apply_metadata_fix.cmd`

## 完成时间
2026-02-20
