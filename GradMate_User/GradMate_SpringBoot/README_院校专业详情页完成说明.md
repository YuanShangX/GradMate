# 院校专业详情页开发完成说明

## 开发时间
2026-02-18

## 功能概述
实现了院校专业详情页面，用户点击专业详情页的学校卡片后，可以查看该学校该专业的详细招生信息。

## 后端实现

### 1. MajorService 新增方法
文件：`src/main/java/com/gradmate/gradmate_springboot/service/MajorService.java`

新增方法：
```java
public List<Major> getSchoolMajorDetail(String schoolName, String majorCode)
```

功能：
- 根据学校名称和专业代码查询该学校该专业的所有招生记录
- 支持多个研究方向的情况
- 按院系所和研究方向排序

### 2. MajorController 接口
文件：`src/main/java/com/gradmate/gradmate_springboot/controller/MajorController.java`

接口路径：`GET /api/major/schoolMajorDetail`

请求参数：
- schoolName: 学校名称（必填）
- majorCode: 专业代码（必填）

返回数据：
- List<Major>：该学校该专业的所有招生记录列表

## 前端实现

### 1. 页面文件
文件：`GradMate_Uniapp/pages/major/schoolDetail.vue`

### 2. 页面功能
- 导航栏显示学校名称（上下左右居中，字体加大加粗）
- 显示专业代码和专业名称
- 显示该学校该专业的详细招生信息：
  - 院系所
  - 研究方向
  - 学习方式
  - 考试方式
  - 拟招生人数
  - 考试科目（4门）
  - 指导教师

### 3. UI设计
- 背景色：#f0f3fa
- 导航栏：白色背景，返回按钮+学校名称（居中、加大、加粗）
- 专业信息卡片：白色背景，圆角16rpx
- 招生信息列表：白色背景，圆角16rpx，每条记录之间用分隔线分隔

### 4. 路由配置
文件：`GradMate_Uniapp/pages.json`

路由路径：`pages/major/schoolDetail`
导航样式：custom（自定义导航栏）

### 5. 页面跳转
从专业详情页（`pages/major/detail.vue`）跳转到院校专业详情页：
```javascript
uni.navigateTo({
  url: `/pages/major/schoolDetail?schoolName=${encodeURIComponent(school.schoolName)}&majorCode=${majorCode.value}&majorName=${encodeURIComponent(majorName.value)}`
})
```

## 测试结果

### 后端测试
- 编译成功：✅
- 服务启动成功：✅ (端口9090)
- API测试：✅
  - 测试URL: `http://localhost:9090/api/major/schoolMajorDetail?schoolName=北京大学&majorCode=020101`
  - 返回状态码：200
  - 返回数据：成功返回该学校该专业的招生信息列表

### 前端测试
- 页面创建：✅
- 路由配置：✅
- 页面跳转：✅
- 数据加载：待用户测试

## 数据库表
使用现有的 `major` 表，不创建新表。

查询条件：
- school_name = ?
- major_code = ?

## 注意事项
1. 严格遵守不修改六张核心表（school_character, school_adjust, major, school, school_information, school_brochure）的数据和结构
2. 导航栏标题上下左右居中，字体加大加粗
3. UI设计风格与找院校页面保持一致
4. 支持多个研究方向的情况（一个学校一个专业可能有多条招生记录）

## 下一步
用户可以在前端测试页面功能，确认数据显示是否正确。
