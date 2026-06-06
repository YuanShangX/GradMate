# 专业详情页合并说明

## 合并时间
2026-02-18

## 合并原因
系统中存在两种跳转到专业详情页的方式：
1. 旧方式：传递 `id` 参数（从院校搜索、院校详情、浏览历史、收藏列表）
2. 新方式：传递 `code` 和 `name` 参数（从找专业页面）

为了统一用户体验，将两种方式合并到同一个页面 `pages/major/detail.vue`

## 页面功能

### 统一的页面结构
- 导航栏：显示专业代码和专业名称
- 筛选栏：所在地区、院校标签
- 学校列表：显示开设该专业的所有学校

### 支持两种参数方式

#### 1. 旧方式：通过 id 参数
**来源页面：**
- `pages/school/search.vue` - 院校搜索页
- `pages/school/detail.vue` - 院校详情页
- `pages/profile/history.vue` - 浏览历史
- `pages/profile/favorite.vue` - 我的收藏

**跳转方式：**
```javascript
uni.navigateTo({
  url: `/pages/major/detail?id=${major.id}`
})
```

**处理流程：**
1. 接收 `id` 参数
2. 调用 `/api/major/${id}` 接口获取专业详情
3. 从返回数据中提取 `majorCode` 和 `majorName`
4. 调用 `/api/major/schools` 接口获取开设该专业的学校列表
5. 显示学校列表

#### 2. 新方式：通过 code 和 name 参数
**来源页面：**
- `pages/major/search.vue` - 找专业页面

**跳转方式：**
```javascript
uni.navigateTo({
  url: `/pages/major/detail?code=${major.code}&name=${encodeURIComponent(major.name)}`
})
```

**处理流程：**
1. 接收 `code` 和 `name` 参数
2. 直接调用 `/api/major/schools` 接口获取开设该专业的学校列表
3. 显示学校列表

## 代码实现

### 核心逻辑
```javascript
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options
  
  if (options.id) {
    // 旧方式：通过id查询专业详情
    loadMajorById(options.id)
  } else if (options.code) {
    // 新方式：通过code查询开设该专业的学校列表
    majorCode.value = options.code || ''
    majorName.value = decodeURIComponent(options.name || '')
    loadSchoolList()
  }
})
```

### 新增函数

#### loadMajorById(id)
- 功能：通过专业id查询专业详情
- 接口：`GET /api/major/${id}`
- 返回：Major对象（包含majorCode和majorName）
- 后续：调用loadSchoolList()加载学校列表

#### loadSchoolList()
- 功能：加载开设该专业的学校列表
- 接口：`GET /api/major/schools`
- 参数：majorCode, location（可选）, schoolType（可选）
- 返回：学校列表

## 用户体验
无论从哪个页面跳转到专业详情页，用户看到的都是：
1. 专业代码和名称
2. 筛选功能（地区、标签）
3. 开设该专业的学校列表
4. 点击学校卡片可查看该校该专业的详细招生信息

## 后续优化建议
1. 统一所有跳转方式，建议都使用 `code` 和 `name` 参数
2. 如果使用 `id` 参数，需要额外一次API调用，性能略差
3. 可以考虑在各个来源页面直接传递 `code` 和 `name`，避免通过 `id` 查询

## 相关文件
- 前端：`GradMate_Uniapp/pages/major/detail.vue`
- 后端：`GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/MajorController.java`
- 后端：`GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/MajorService.java`
