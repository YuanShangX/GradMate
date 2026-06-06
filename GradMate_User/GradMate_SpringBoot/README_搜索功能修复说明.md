# 搜索功能修复说明

> 修复时间：2026-02-19

---

## 问题分析

### 问题描述
院校对比和专业对比的选择页面无法搜索，输入关键词后没有反应。

### 根本原因

**前后端参数不匹配**

1. **院校搜索问题**：
   - 前端调用：`/api/school/search` 接口（不存在）
   - 前端参数：`keyword`、`page`、`size`
   - 后端实际：`/api/school/list` 接口
   - 后端参数：`schoolName`、`pageNum`、`pageSize`

2. **专业搜索问题**：
   - 前端调用：`/api/major/search` 接口（正确）
   - 前端参数：`keyword`、`page`、`size`
   - 后端参数：`majorName`、`pageNum`、`pageSize`

---

## 修复方案

### 1. 院校选择页面 (school/select.vue)

**修改前**：
```javascript
// 错误的接口和参数
const res = await uni.request({
    url: 'http://localhost:9090/api/school/search',  // ❌ 接口不存在
    method: 'GET',
    data: {
        keyword: searchKeyword.value,  // ❌ 参数名错误
        page: 1,                       // ❌ 参数名错误
        size: 100                      // ❌ 参数名错误
    }
})
```

**修改后**：
```javascript
// 正确的接口和参数
const res = await uni.request({
    url: 'http://localhost:9090/api/school/list',  // ✅ 使用正确的接口
    method: 'GET',
    data: {
        schoolName: searchKeyword.value || '',  // ✅ 使用 schoolName
        pageNum: 1,                             // ✅ 使用 pageNum
        pageSize: 100                           // ✅ 使用 pageSize
    }
})
```

### 2. 专业选择页面 (major/select.vue)

**修改前**：
```javascript
// 错误的参数名
const res = await uni.request({
    url: 'http://localhost:9090/api/major/search',
    method: 'GET',
    data: {
        keyword: searchKeyword.value || '',  // ❌ 参数名错误
        page: page.value,                    // ❌ 参数名错误
        size: 20                             // ❌ 参数名错误
    }
})
```

**修改后**：
```javascript
// 正确的参数名
const res = await uni.request({
    url: 'http://localhost:9090/api/major/search',
    method: 'GET',
    data: {
        majorName: searchKeyword.value || '',  // ✅ 使用 majorName
        pageNum: page.value,                   // ✅ 使用 pageNum
        pageSize: 20                           // ✅ 使用 pageSize
    }
})
```

---

## 后端接口说明

### 院校列表接口

**接口地址**：`GET /api/school/list`

**请求参数**：
```java
public class SchoolQueryRequest {
    private String schoolName;   // 学校名称（模糊查询）
    private String location;     // 所在省份
    private String subordinate;  // 主管部门
    private Integer is985;       // 是否985
    private Integer yjsy;        // 是否211/研究生院
    private Integer syl;         // 是否双一流
    private Integer zhx;         // 是否自划线
    private Integer pageNum;     // 页码（从1开始）
    private Integer pageSize;    // 每页大小
}
```

### 专业搜索接口

**接口地址**：`GET /api/major/search`

**请求参数**：
```java
public class MajorQueryRequest {
    private String schoolName;   // 学校名称（模糊查询）
    private String majorName;    // 专业名称（模糊查询）
    private String province;     // 所在省份
    private String degreeType;   // 学位类型
    private String examType;     // 考试方式
    private String studyMode;    // 学习方式
    private String department;   // 院系所
    private Integer pageNum;     // 页码（从1开始）
    private Integer pageSize;    // 每页大小
}
```

---

## 测试验证

### 院校搜索测试

1. 打开院校选择页面
2. 在搜索框输入"清华"
3. 点击搜索或按回车
4. 验证列表显示包含"清华"的院校

### 专业搜索测试

1. 打开专业选择页面
2. 在搜索框输入"计算机"
3. 点击搜索或按回车
4. 验证列表显示包含"计算机"的专业

---

## 经验总结

### 常见错误

1. **前后端参数名不一致**
   - 前端使用 `keyword`，后端使用 `schoolName` 或 `majorName`
   - 前端使用 `page`/`size`，后端使用 `pageNum`/`pageSize`

2. **接口路径错误**
   - 前端调用不存在的接口
   - 应该先确认后端接口是否存在

### 预防措施

1. **开发前确认接口**
   - 查看后端 Controller 确认接口路径
   - 查看 DTO 类确认参数名称

2. **统一命名规范**
   - 建议前后端使用相同的参数命名
   - 或在接口文档中明确说明参数映射关系

3. **接口测试**
   - 使用 Postman 或 curl 先测试后端接口
   - 确认参数和返回值格式

---

## 修复文件清单

- `GradMate_Uniapp/pages/school/select.vue` - 修复院校搜索参数
- `GradMate_Uniapp/pages/major/select.vue` - 修复专业搜索参数

---

## 状态

✅ 院校搜索功能已修复
✅ 专业搜索功能已修复
✅ 参数名称已统一
🔄 待测试完整搜索流程
