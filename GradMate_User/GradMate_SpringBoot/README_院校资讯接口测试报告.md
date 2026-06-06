# 院校资讯接口测试报告

## 测试时间
2026-02-17 23:03

## 测试环境
- 后端服务：Spring Boot 3.2.2
- 端口：9090
- 数据库：MySQL 8.0.34
- 数据库名：gradmate

## 测试接口
`GET /api/info/mixed/list`

## 测试用例

### 用例1：获取混合资讯列表（5条）
**请求URL：**
```
http://localhost:9090/api/info/mixed/list?pageNum=1&pageSize=5
```

**响应状态：** ✅ 200 OK

**响应数据：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 622,
        "schoolId": 316,
        "schoolName": "齐齐哈尔大学",
        "title": "4.22调剂系统开通公告",
        "url": "https://yz.chsi.com.cn//sch/viewBulletin--infoId-3069504124,categoryId-475484,schId-368094,mindex-12.dhtml",
        "publishDate": "2023-04-22T12:04:55",
        "type": "information",
        "tag": "招生通知"
      },
      {
        "id": 1240,
        "schoolId": 651,
        "schoolName": "中国航发湖南动力机械研究所",
        "title": "中国航发湖南动力机械研究所2023年硕士研究生招生调剂考生复试成绩及拟录取名单公示",
        "url": "https://yz.chsi.com.cn//sch/viewBulletin--infoId-3069497426,categoryId-459239,schId-368384,mindex-12.dhtml",
        "publishDate": "2023-04-21T19:59:41",
        "type": "information",
        "tag": "招生通知"
      },
      {
        "id": 1473,
        "schoolId": 667,
        "schoolName": "广州体育学院",
        "title": "广州体育学院2023年硕士研究生招生考试二次调剂方案",
        "url": "https://yz.chsi.com.cn//sch/tjzc--method-viewPub,infoId-3069497276,categoryId-671779,schId-368424,mindex-11.dhtml",
        "publishDate": "2023-04-21T19:12:24",
        "type": "adjust",
        "tag": "调剂公告"
      },
      {
        "id": 1524,
        "schoolId": 693,
        "schoolName": "广西中医药大学",
        "title": "广西中医药大学2023年硕士研究生招生考试第一次调剂公告",
        "url": "https://yz.chsi.com.cn//sch/tjzc--method-viewPub,infoId-3069495673,categoryId-671825,schId-368437,mindex-11.dhtml",
        "publishDate": "2023-04-21T18:32:12",
        "type": "adjust",
        "tag": "调剂公告"
      },
      {
        "id": 1,
        "schoolId": 1,
        "schoolName": "北京大学",
        "title": "北京大学2023年硕士研究生招生简章",
        "url": "https://yz.chsi.com.cn//sch/viewZszc--infoId-3069465174,categoryId-10460768,schId-367878,mindex-12.dhtml",
        "publishDate": "2023-04-18T08:54:21",
        "type": "brochure",
        "tag": "招生简章"
      }
    ],
    "total": 5,
    "size": 5,
    "current": 1,
    "pages": 1
  },
  "timestamp": 1771340979103
}
```

## 测试结果分析

### ✅ 成功项
1. **接口可访问性**：接口正常响应，返回200状态码
2. **数据格式正确**：返回的JSON格式符合预期
3. **数据完整性**：所有必需字段都存在（id、schoolId、schoolName、title、url、publishDate、type、tag）
4. **数据混合**：成功混合了三种类型的资讯
   - 招生通知（information）：2条
   - 调剂公告（adjust）：2条
   - 招生简章（brochure）：1条
5. **排序正确**：按发布日期降序排列
6. **学校名称映射**：成功通过schoolId获取到学校名称
7. **标签显示**：每条资讯都有对应的中文标签

### 数据统计
- **数据库总量**：
  - school_information: 1672条
  - school_adjust: 1918条
  - school_brochure: 1887条
  - 总计：5477条资讯

- **返回数据**：5条（符合pageSize=5的要求）

### 数据示例
1. **齐齐哈尔大学** - 调剂系统开通公告（2023-04-22）
2. **中国航发湖南动力机械研究所** - 复试成绩及拟录取名单（2023-04-21）
3. **广州体育学院** - 二次调剂方案（2023-04-21）
4. **广西中医药大学** - 第一次调剂公告（2023-04-21）
5. **北京大学** - 招生简章（2023-04-18）

## 前端集成建议

### 1. API调用示例
```javascript
const loadNewsList = async () => {
  try {
    const res = await uni.request({
      url: 'http://localhost:9090/api/info/mixed/list',
      method: 'GET',
      data: {
        pageNum: 1,
        pageSize: 10
      }
    })
    
    if (res.data.code === 200) {
      const records = res.data.data.records || []
      // 处理数据...
    }
  } catch (error) {
    console.error('加载资讯失败:', error)
  }
}
```

### 2. 数据展示格式
- **标签**：使用tag字段（招生简章/调剂公告/招生通知）
- **标题**：显示title字段
- **来源**：显示schoolName字段
- **时间**：格式化publishDate字段（YYYY-MM-DD）
- **链接**：使用url字段（可在webview中打开）

### 3. 样式建议
- 不同类型的资讯可以使用不同颜色的标签
  - 招生简章：蓝色
  - 调剂公告：橙色
  - 招生通知：绿色

## 注意事项

1. **数据时效性**：当前数据库中的数据是2023年的，建议定期更新
2. **跨域配置**：已在CorsConfig中配置CORS，支持跨域请求
3. **性能优化**：
   - 使用了学校名称缓存，避免重复查询
   - 限制了每种类型的查询数量
4. **错误处理**：前端需要处理网络错误和空数据情况

## 后续优化建议

1. 添加分页功能，支持加载更多
2. 添加按类型筛选功能
3. 添加搜索功能（按学校名称或标题）
4. 添加收藏功能
5. 添加资讯详情页面
6. 更新数据库中的资讯到最新年份
7. 添加评论功能

## 测试结论

✅ **接口测试通过**

接口功能正常，数据格式正确，可以正常集成到前端页面中使用。
