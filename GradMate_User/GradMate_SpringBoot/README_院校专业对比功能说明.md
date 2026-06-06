# 院校专业对比功能说明

## 功能概述
院校专业对比模块允许用户同时对比2-3个院校或专业，通过横向对比表格展示差异，并使用AI生成对比分析报告。

## 后端实现

### 1. CompareService
位置：`src/main/java/com/gradmate/gradmate_springboot/service/CompareService.java`

功能：
- `compareSchools(List<Long> schoolIds)` - 对比院校
- `compareMajors(List<Long> majorIds)` - 对比专业
- `generateSchoolCompareAnalysis()` - 生成院校对比AI分析
- `generateMajorCompareAnalysis()` - 生成专业对比AI分析

### 2. CompareController
位置：`src/main/java/com/gradmate/gradmate_springboot/controller/CompareController.java`

接口：
- `POST /api/compare/schools` - 院校对比
- `POST /api/compare/majors` - 专业对比

## 前端实现

### 1. 院校对比页面
位置：`pages/school/compare.vue`

对比维度：
- 地理位置
- 主管部门
- 院校特征（双一流/211/自划线）
- 专业数量
- AI对比分析

### 2. 专业对比页面
位置：`pages/major/compare.vue`

对比维度：
- 专业代码
- 学位类型
- 学习方式
- 拟招生人数
- 考试科目
- AI对比分析

## 使用方式

### 院校对比
```javascript
// 跳转到院校对比页面
uni.navigateTo({
  url: `/pages/school/compare?schoolIds=${encodeURIComponent(JSON.stringify([1, 2, 3]))}`
})
```

### 专业对比
```javascript
// 跳转到专业对比页面
uni.navigateTo({
  url: `/pages/major/compare?majorIds=${encodeURIComponent(JSON.stringify([1, 2, 3]))}`
})
```

## API接口

### 院校对比
```
POST /api/compare/schools
Content-Type: application/json

{
  "schoolIds": [1, 2, 3]
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "schools": [
      {
        "schoolName": "北京大学",
        "location": "北京",
        "subordinate": "教育部",
        "isSyl": 1,
        "is211": 1,
        "isZhx": 1,
        "majorCount": 500
      },
      ...
    ],
    "analysis": "AI生成的对比分析文本"
  }
}
```

### 专业对比
```
POST /api/compare/majors
Content-Type: application/json

{
  "majorIds": [1, 2, 3]
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "majors": [
      {
        "schoolName": "北京大学",
        "majorName": "计算机科学与技术",
        "majorCode": "081200",
        "degreeType": "学术学位",
        "studyMode": "全日制",
        "plannedEnrollment": "50",
        "examSubject1": "思想政治理论",
        "examSubject2": "英语一",
        "examSubject3": "数学一",
        "examSubject4": "计算机学科专业基础"
      },
      ...
    ],
    "analysis": "AI生成的对比分析文本"
  }
}
```

## AI分析功能

使用 `GradMateAgentService` 生成对比分析：

### 院校对比分析维度
- 地理位置优劣
- 院校层次对比
- 考研难度评估
- 择校建议

### 专业对比分析维度
- 考试难度对比
- 招生规模分析
- 就业前景评估
- 选择建议

## 下一步开发

### 添加对比入口
需要在以下页面添加对比功能入口：

1. 院校详情页（`pages/school/detail.vue`）
   - 添加"加入对比"按钮
   - 维护对比列表（最多3个）
   - 跳转到对比页面

2. 院校列表页（`pages/school/list.vue`）
   - 列表项添加"对比"复选框
   - 底部显示对比栏
   - 跳转到对比页面

3. 专业详情页（`pages/major/detail.vue`）
   - 添加"加入对比"按钮
   - 维护对比列表（最多3个）
   - 跳转到对比页面

4. 专业搜索页（`pages/major/search.vue`）
   - 列表项添加"对比"复选框
   - 底部显示对比栏
   - 跳转到对比页面

## 测试步骤

1. 启动后端服务
2. 测试院校对比接口：
   ```bash
   curl -X POST http://localhost:9090/api/compare/schools \
     -H "Content-Type: application/json" \
     -d '{"schoolIds": [1, 2]}'
   ```
3. 测试专业对比接口：
   ```bash
   curl -X POST http://localhost:9090/api/compare/majors \
     -H "Content-Type: application/json" \
     -d '{"majorIds": [1, 2]}'
   ```
4. 前端测试：直接访问对比页面查看效果

## 注意事项

1. 必须选择2-3个院校/专业进行对比
2. AI分析可能失败，已做降级处理
3. 对比数据来自现有的详情接口
4. 页面使用自定义导航栏
5. 需要重启Spring Boot应用使新Controller生效
