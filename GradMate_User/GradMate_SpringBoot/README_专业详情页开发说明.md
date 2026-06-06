# 专业详情页开发说明

## 功能概述
点击具体专业后，进入专业详情页，显示所有开设该专业的学校列表。

## 数据来源
使用现有的数据库表，不创建新表，不修改现有表结构和数据：
- `major` 表：专业和学校的对应关系
- `school` 表：学校基本信息（logo、地区等）
- `school_character` 表：学校特性（985、211、双一流、自划线）

## 实现方案

### 1. 前端页面
**文件：** `GradMate_Uniapp/pages/major/detail.vue`

**功能：**
- 顶部导航栏：返回按钮 + 专业代码和名称 + 搜索按钮
- 筛选栏：所在地、院校类型、排序
- 学校卡片列表：
  - 学校logo
  - 学校名称 + 地区标签
  - 学校标签（985、211、双一流、自划线）
  - 学习方式、考试方式、院系
  - 统计信息（关注数、浏览数、招生数、分数线）

**路由参数：**
- `code`: 专业代码（如"025100"）
- `name`: 专业名称（如"金融"）

### 2. 后端API

#### 接口地址
```
GET /api/major/schools
```

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| majorCode | String | 是 | 专业代码 |
| location | String | 否 | 地区筛选 |
| schoolType | String | 否 | 学校类型：985/211/syl/zhx |

#### 响应示例
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "schoolName": "复旦大学",
      "logo": "http://example.com/logo.png",
      "location": "上海",
      "department": "哲学学院",
      "studyMode": "全日制",
      "examType": "统考",
      "plannedEnrollment": "专业：20(不含推免)",
      "is985": true,
      "is211": true,
      "isSyl": true,
      "isZhx": true,
      "followCount": 106,
      "viewCount": 5840,
      "admissionScore": "330"
    }
  ]
}
```

### 3. 后端实现

#### DTO类
**文件：** `MajorSchoolResponse.java`

包含字段：
- schoolName: 学校名称
- logo: 学校logo
- location: 所在地区
- department: 院系
- studyMode: 学习方式
- examType: 考试方式
- plannedEnrollment: 计划招生人数
- is985/is211/isSyl/isZhx: 学校特性
- followCount/viewCount/admissionScore: 统计数据（模拟）

#### Service方法
**文件：** `MajorService.java`
**方法：** `getSchoolsByMajorCode(String majorCode, String location, String schoolType)`

**实现逻辑：**
1. 从major表查询该专业代码对应的所有记录
2. 按学校名称去重
3. 关联查询school表获取logo和地区
4. 关联查询school_character表获取学校特性
5. 根据筛选条件过滤结果
6. 返回学校列表

#### Controller接口
**文件：** `MajorController.java`
**路径：** `/api/major/schools`

## 数据库查询示例

### 查询某专业的学校数量
```sql
SELECT COUNT(DISTINCT school_name) as school_count 
FROM major 
WHERE major_code = '025100';
```

### 查询某专业的学校列表
```sql
SELECT DISTINCT 
    m.school_name,
    m.department,
    m.study_mode,
    m.exam_type,
    m.planned_enrollment,
    s.logo,
    s.location,
    sc.yjsy,
    sc.syl,
    sc.zhx
FROM major m
LEFT JOIN school s ON m.school_name = s.school_name
LEFT JOIN school_character sc ON m.school_name = sc.school_name
WHERE m.major_code = '025100'
ORDER BY m.school_name;
```

## 测试步骤

### 1. 测试API接口
```bash
# 查询金融专业的学校列表
curl "http://localhost:9090/api/major/schools?majorCode=025100"

# 查询上海地区的学校
curl "http://localhost:9090/api/major/schools?majorCode=025100&location=上海"

# 查询985学校
curl "http://localhost:9090/api/major/schools?majorCode=025100&schoolType=985"
```

### 2. 测试前端页面
1. 打开找专业页面
2. 选择经济学门类
3. 切换到专硕标签
4. 点击"金融"专业
5. 应该跳转到专业详情页
6. 显示所有开设金融专业的学校列表

### 3. 测试筛选功能
- 点击"所在地"筛选（功能开发中提示）
- 点击"院校"筛选（功能开发中提示）
- 点击"筛选"排序（功能开发中提示）

## UI设计要点

### 导航栏
- 背景色：白色
- 返回按钮：左侧
- 标题：专业代码-专业名称（居中，超长省略）
- 搜索按钮：右侧

### 筛选栏
- 背景色：白色
- 三个筛选项：所在地、院校、筛选
- 每个筛选项有下拉箭头

### 学校卡片
- 背景色：白色
- 圆角：16rpx
- 内边距：30rpx
- 卡片间距：20rpx
- 学校logo：圆形，100rpx
- 学校名称：32rpx，加粗
- 地区标签：水滴形状图标 + 地区名称
- 学校标签：蓝色背景（#5b8ff9）
- 分割线：浅灰色
- 统计信息：4列均分

### 整体背景
- 背景色：#f0f3fa

## 注意事项

1. **不修改数据库表**
   - 只读取数据，不修改表结构
   - 不修改表数据

2. **数据去重**
   - 同一个学校可能有多条记录（不同院系、不同方向）
   - 按学校名称去重，保留第一条记录

3. **学校特性判断**
   - 985和211使用yjsy字段（研究生院）
   - 双一流使用syl字段
   - 自划线使用zhx字段

4. **模拟数据**
   - 关注数、浏览数、分数线使用随机数模拟
   - 后续可以从真实数据源获取

5. **性能优化**
   - 使用Map进行关联查询，避免N+1问题
   - 按学校名称去重，减少数据量

## 后续优化建议

1. **筛选功能完善**
   - 实现地区筛选（一区/二区）
   - 实现院校类型筛选（985/211/双一流/自划线）
   - 实现排序功能（按招生数、分数线等）

2. **搜索功能**
   - 支持按学校名称搜索
   - 支持按院系名称搜索

3. **数据完善**
   - 接入真实的关注数、浏览数
   - 接入真实的分数线数据
   - 添加历年招生数据对比

4. **交互优化**
   - 添加下拉刷新
   - 添加上拉加载更多
   - 添加骨架屏加载效果

5. **详情页跳转**
   - 点击学校卡片跳转到学校详情页
   - 传递学校名称参数

## 数据统计

### 测试数据（金融专业 025100）
- 开设学校数量：250所
- 总记录数：797条
- 平均每所学校：3.2条记录（不同院系/方向）

### 其他专业示例
- 应用统计（025200）
- 税务（025300）
- 国际商务（025400）
- 保险（025500）
- 资产评估（025600）
