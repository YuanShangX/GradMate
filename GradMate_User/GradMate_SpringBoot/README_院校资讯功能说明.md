# 院校资讯功能说明

## 功能概述
在院校库首页的"推荐资讯"模块展示来自数据库的真实资讯信息，包括：
- 招生简章（school_brochure表）
- 调剂公告（school_adjust表）
- 招生通知（school_information表）

## 数据库表结构

### 1. school_information（招生通知）
```sql
+--------------+--------------+------+-----+---------+----------------+
| Field        | Type         | Null | Key | Default | Extra          |
+--------------+--------------+------+-----+---------+----------------+
| id           | bigint       | NO   | PRI | NULL    | auto_increment |
| school_id    | bigint       | YES  |     | NULL    |                |
| title        | varchar(255) | YES  |     | NULL    |                |
| url          | varchar(500) | YES  |     | NULL    |                |
| publish_date | datetime     | YES  |     | NULL    |                |
+--------------+--------------+------+-----+---------+----------------+
```
数据量：1672条

### 2. school_adjust（调剂公告）
```sql
+--------------+--------------+------+-----+---------+----------------+
| Field        | Type         | Null | Key | Default | Extra          |
+--------------+--------------+------+-----+---------+----------------+
| id           | bigint       | NO   | PRI | NULL    | auto_increment |
| school_id    | bigint       | YES  |     | NULL    |                |
| title        | varchar(255) | YES  |     | NULL    |                |
| url          | varchar(500) | YES  |     | NULL    |                |
| publish_date | datetime     | YES  |     | NULL    |                |
+--------------+--------------+------+-----+---------+----------------+
```
数据量：1918条

### 3. school_brochure（招生简章）
```sql
+--------------+--------------+------+-----+---------+----------------+
| Field        | Type         | Null | Key | Default | Extra          |
+--------------+--------------+------+-----+---------+----------------+
| id           | bigint       | NO   | PRI | NULL    | auto_increment |
| school_id    | bigint       | YES  |     | NULL    |                |
| title        | varchar(255) | YES  |     | NULL    |                |
| url          | varchar(500) | YES  |     | NULL    |                |
| publish_date | datetime     | YES  |     | NULL    |                |
+--------------+--------------+------+-----+---------+----------------+
```
数据量：1887条

## 后端接口

### 1. 混合资讯列表接口（用于首页）
**接口地址：** `GET /api/info/mixed/list`

**请求参数：**
- pageNum: 页码（默认1）
- pageSize: 每页数量（默认10）

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 622,
        "schoolId": 316,
        "schoolName": "某某大学",
        "title": "2023年硕士研究生招生简章",
        "url": "https://...",
        "publishDate": "2023-04-22T12:04:55",
        "type": "brochure",
        "tag": "招生简章"
      }
    ],
    "total": 10,
    "pages": 1
  }
}
```

### 2. 其他接口
- `GET /api/info/brochure/list` - 招生简章列表
- `GET /api/info/adjust/list` - 调剂公告列表
- `GET /api/info/information/list` - 招生通知列表
- `GET /api/info/brochure/{id}` - 招生简章详情
- `GET /api/info/adjust/{id}` - 调剂公告详情
- `GET /api/info/information/{id}` - 招生通知详情

## 前端实现

### 页面位置
`GradMate_Uniapp/pages/school/index.vue`

### 功能特点
1. 页面加载时自动从后端获取资讯数据
2. 显示资讯标签（招生简章/调剂公告/招生通知）
3. 显示学校名称、标题、发布时间
4. 点击资讯项可查看详情（待实现）
5. 支持加载更多功能（待实现）

### 数据格式
```javascript
{
  id: 622,
  type: 'brochure',
  tag: '招生简章',
  title: '2023年硕士研究生招生简章',
  source: '某某大学',
  time: '2023-04-22',
  url: 'https://...',
  comment: 23
}
```

## 测试步骤

### 1. 启动后端服务
```bash
cd GradMate_SpringBoot
mvn spring-boot:run
```
或在IDE中运行 `GradMateSpringBootApplication`

### 2. 测试接口
使用Postman或浏览器访问：
```
http://localhost:9090/api/info/mixed/list?pageNum=1&pageSize=10
```

### 3. 启动前端
```bash
cd GradMate_Uniapp
# 使用HBuilderX运行到浏览器或模拟器
```

### 4. 验证功能
1. 登录后进入院校库首页
2. 滚动到"推荐资讯"模块
3. 查看是否显示真实的资讯数据
4. 检查标签、标题、学校名称、时间是否正确显示

## 注意事项

1. **数据库连接**：确保MySQL服务正常运行，数据库名为`gradmate`
2. **端口配置**：后端端口为9090，前端请求时注意端口号
3. **跨域问题**：已在CorsConfig中配置CORS，允许跨域请求
4. **数据时效性**：当前数据库中的数据是2023年的，可能需要更新
5. **评论数**：目前评论数是随机生成的，后续可以添加真实的评论功能

## 后续优化建议

1. 添加资讯详情页面
2. 实现真实的评论功能
3. 添加资讯收藏功能
4. 支持按类型筛选资讯
5. 添加搜索功能
6. 实现分页加载更多
7. 更新数据库中的资讯数据到最新年份
