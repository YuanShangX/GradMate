# 专业收藏跳转修复说明

## 问题描述
从"我的收藏"和"浏览历史"中点击专业条目时，无法正确跳转到专业详情页，显示"专业不存在"错误。

## 问题原因
1. **数据存储问题**：专业收藏/历史时，使用 `schoolName + majorCode` 的哈希值作为 `targetId`，但原始的 `schoolName` 和 `majorCode` 没有单独存储
2. **数据丢失**：`UserFavorite` 和 `UserHistory` 实体只有 `targetId` 和 `targetName` 字段，`targetName` 格式为 "学校名 - 专业名"，但缺少 `majorCode`
3. **跳转参数不足**：`schoolDetail.vue` 页面需要 `schoolName`、`majorCode`、`majorName` 三个参数，但从收藏/历史列表中无法获取完整信息

## 解决方案

### 1. 数据库修改
添加 `metadata` 字段用于存储 JSON 格式的元数据（同时修改收藏表和历史表）：

```sql
-- 添加 user_favorite 表的 metadata 字段
ALTER TABLE `user_favorite` 
ADD COLUMN `metadata` TEXT COMMENT '元数据(JSON格式，存储额外信息如schoolName, majorCode等)' 
AFTER `target_name`;

-- 更新现有收藏数据
UPDATE `user_favorite` 
SET `metadata` = JSON_OBJECT(
    'schoolName', SUBSTRING_INDEX(`target_name`, ' - ', 1),
    'majorName', SUBSTRING_INDEX(`target_name`, ' - ', -1)
)
WHERE `type` = 'major' AND `metadata` IS NULL;

-- 添加 user_history 表的 metadata 字段
ALTER TABLE `user_history` 
ADD COLUMN `metadata` TEXT COMMENT '元数据(JSON格式，存储额外信息如schoolName, majorCode等)' 
AFTER `target_name`;

-- 更新现有历史数据
UPDATE `user_history` 
SET `metadata` = JSON_OBJECT(
    'schoolName', SUBSTRING_INDEX(`target_name`, ' - ', 1),
    'majorName', SUBSTRING_INDEX(`target_name`, ' - ', -1)
)
WHERE `type` = 'major' AND `metadata` IS NULL;
```

### 2. 后端修改

#### UserFavorite.java 和 UserHistory.java
添加 `metadata` 字段：
```java
/**
 * 元数据(JSON格式，存储额外信息)
 */
private String metadata;
```

#### FavoriteService.java
在添加专业收藏时，存储完整的元数据：
```java
// 存储元数据，包含学校名称和专业代码
String metadata = String.format("{\"schoolName\":\"%s\",\"majorCode\":\"%s\",\"majorName\":\"%s\"}", 
                               schoolName, majorCode, majorName);
favorite.setMetadata(metadata);
```

#### HistoryService.java
添加支持元数据的重载方法：
```java
/**
 * 添加浏览记录（带元数据）
 */
public boolean addHistory(Long userId, String type, Long targetId, String targetName, String metadata) {
    UserHistory history = new UserHistory();
    history.setUserId(userId);
    history.setType(type);
    history.setTargetId(targetId);
    history.setTargetName(targetName);
    history.setMetadata(metadata);
    
    return historyMapper.insert(history) > 0;
}
```

#### HistoryController.java
更新添加历史记录接口，支持可选的 metadata 参数：
```java
String metadata = params.containsKey("metadata") ? params.get("metadata").toString() : null;
boolean success = historyService.addHistory(userId, type, targetId, targetName, metadata);
```

### 3. 前端修改

#### favorite.vue 和 history.vue
修改 `goToDetail` 函数，智能解析跳转参数：

```javascript
const goToDetail = (item) => {
	if (item.type === 'major') {
		// 1. 优先从 metadata 获取完整信息
		let schoolName = ''
		let majorCode = ''
		let majorName = item.targetName
		
		if (item.metadata) {
			try {
				const metadata = JSON.parse(item.metadata)
				schoolName = metadata.schoolName || ''
				majorCode = metadata.majorCode || ''
				majorName = metadata.majorName || item.targetName
			} catch (e) {
				console.error('解析 metadata 失败:', e)
			}
		}
		
		// 2. 如果没有从 metadata 获取到学校名称，从 targetName 提取
		// targetName 格式: "学校名 - 专业名"
		if (!schoolName && item.targetName && item.targetName.includes(' - ')) {
			const nameParts = item.targetName.split(' - ')
			schoolName = nameParts[0]
			majorName = nameParts[1] || majorName
		}
		
		// 3. 只要有学校名称，就跳转到院校-专业详情页
		if (schoolName) {
			uni.navigateTo({ 
				url: `/pages/major/schoolDetail?schoolName=${encodeURIComponent(schoolName)}&majorCode=${encodeURIComponent(majorCode || '')}&majorName=${encodeURIComponent(majorName)}` 
			})
		} else {
			// 完全无法解析时，跳转到专业列表页
			uni.navigateTo({ 
				url: `/pages/major/detail?name=${encodeURIComponent(majorName)}` 
			})
		}
	}
}
```

### 4. 后端 API 改进

#### MajorController.java
更新 `schoolMajorDetail` 接口，支持可选的 majorName 参数：
```java
@GetMapping("/schoolMajorDetail")
public Result<List<SchoolMajorDetailResponse>> getSchoolMajorDetail(
        @RequestParam String schoolName,
        @RequestParam(required = false) String majorCode,
        @RequestParam(required = false) String majorName) {
    List<SchoolMajorDetailResponse> result = 
        majorService.getSchoolMajorDetail(schoolName, majorCode, majorName);
    return Result.success("查询成功", result);
}
```

#### MajorService.java
改进查询逻辑，支持多种查询方式：
```java
public List<SchoolMajorDetailResponse> getSchoolMajorDetail(
        String schoolName, String majorCode, String majorName) {
    QueryWrapper<Major> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("school_name", schoolName);
    
    // 优先使用 majorCode 精确匹配
    if (majorCode != null && !majorCode.trim().isEmpty()) {
        queryWrapper.eq("major_code", majorCode);
    } 
    // 如果没有 majorCode，使用 majorName 模糊匹配
    else if (majorName != null && !majorName.trim().isEmpty()) {
        queryWrapper.like("major_name", majorName);
    }
    
    // ... 查询和返回结果
}
```

## 测试步骤

### 1. 执行数据库脚本
```bash
# 在 MySQL 中执行
source GradMate_SpringBoot/src/main/resources/sql/add_favorite_metadata.sql
```

### 2. 重启后端服务
```bash
cd GradMate_SpringBoot
mvnw spring-boot:run
```

### 3. 测试流程
1. 登录应用
2. 进入任意专业详情页（schoolDetail.vue）
3. 点击"收藏"按钮
4. 进入"个人中心" -> "我的收藏"
5. 点击刚才收藏的专业
6. 验证是否正确跳转到专业详情页

### 4. 验证旧数据
对于已存在的收藏记录：
- 如果 `metadata` 字段为空，会从 `targetName` 中提取专业名称
- 跳转到专业列表页（`/pages/major/detail`），显示所有开设该专业的学校
- 建议用户重新收藏以获取完整的元数据

## 技术要点

1. **智能跳转逻辑**：
   - 优先使用 metadata 中的完整信息（schoolName + majorCode + majorName）
   - 降级方案：从 targetName 提取学校名称和专业名称
   - 只要有学校名称，就跳转到院校-专业详情页（schoolDetail.vue）
   
2. **后端 API 灵活查询**：
   - 优先使用 majorCode 精确匹配
   - 降级使用 majorName 模糊匹配
   - 保证新旧数据都能正确查询

3. **向后兼容**：
   - 新数据：完整 metadata → 精确查询
   - 旧数据：从 targetName 提取 → 模糊查询
   - 保证所有收藏都能跳转到正确的页面

4. **数据完整性**：新收藏的专业会存储完整的 `schoolName`、`majorCode`、`majorName`

5. **错误处理**：添加 try-catch 处理 JSON 解析异常

## 修改文件清单

### 后端
- `GradMate_SpringBoot/src/main/resources/sql/add_favorite_metadata.sql` (新建)
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/entity/UserFavorite.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/entity/UserHistory.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/FavoriteService.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/HistoryService.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/MajorService.java` (改进查询逻辑)
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/HistoryController.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/MajorController.java` (支持 majorName 参数)

### 前端
- `GradMate_Uniapp/pages/profile/favorite.vue` (智能跳转逻辑)
- `GradMate_Uniapp/pages/profile/history.vue` (智能跳转逻辑)

### 测试脚本
- `GradMate_SpringBoot/test_favorite_metadata.ps1` (新建)

## 注意事项

1. **数据库备份**：执行 SQL 脚本前建议备份 `user_favorite` 和 `user_history` 表
2. **旧数据处理**：SQL 脚本会自动更新现有专业收藏/历史记录，但只能提取学校名称，无法恢复 `majorCode`
3. **建议用户重新收藏**：对于重要的专业收藏，建议用户取消后重新收藏，以获取完整的元数据
4. **历史记录同步**：历史记录表也已同步更新，支持相同的元数据结构
5. **向后兼容**：前端代码已添加降级处理，即使元数据缺失也能正常跳转到专业列表页

## 完成时间
2026-02-20
