# 专业类型表说明

## 概述
为了优化找专业页面的查询性能，创建了独立的`major_category`表，用于存储学科门类、一级学科和具体专业的层级结构，并支持学硕/专硕分类。

## 表结构

### major_category 表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键ID |
| category_code | VARCHAR(2) | 学科门类代码（2位），如"02" |
| category_name | VARCHAR(50) | 学科门类名称，如"经济学" |
| discipline_code | VARCHAR(4) | 一级学科代码（4位），如"0201" |
| discipline_name | VARCHAR(100) | 一级学科名称，如"理论经济学" |
| major_code | VARCHAR(10) | 专业代码（6位或更多），如"020101" |
| major_name | VARCHAR(100) | 专业名称，如"政治经济学" |
| degree_type | VARCHAR(20) | 学位类型：academic-学硕，professional-专硕 |
| sort_order | INT | 排序字段 |
| create_time | TIMESTAMP | 创建时间 |

## 学硕和专硕的区分规则

### 学硕（academic）
- 专业代码第4位不是5
- 例如：020101（政治经济学）、020204（金融学）

### 专硕（professional）
- 专业代码第4位是5
- 例如：025100（金融）、025200（应用统计）

## 数据导入步骤

1. 创建表结构
```bash
mysql -uroot -pA123456 -D gradmate < src/main/resources/sql/major_category_table.sql
```

2. 导入数据（从major表提取）
```bash
mysql -uroot -pA123456 -D gradmate < src/main/resources/sql/import_major_category_data.sql
```

## API接口

### 新接口：/api/major/disciplines-v2
- 方法：GET
- 参数：
  - categoryCode: 学科门类代码（必填）
  - degreeType: 学位类型（可选）
    - academic: 学硕
    - professional: 专硕
    - 不传：返回所有类型

- 示例：
```
GET http://localhost:9090/api/major/disciplines-v2?categoryCode=02&degreeType=academic
```

### 旧接口：/api/major/disciplines
- 保留原有接口，从major表查询
- 不支持学硕/专硕筛选

## 前端使用

前端页面已更新为使用新接口`/api/major/disciplines-v2`，支持学硕/专硕切换：

```javascript
const loadDisciplines = async () => {
  const res = await uni.request({
    url: 'http://localhost:9090/api/major/disciplines-v2',
    method: 'GET',
    data: {
      categoryCode: selectedCategory.value,
      degreeType: degreeType.value  // 'academic' 或 'professional'
    }
  })
}
```

## 优势

1. 查询性能优化：独立表结构，避免大表扫描
2. 支持学硕/专硕筛选：前端可以快速切换
3. 数据结构清晰：层级关系明确
4. 不影响原有数据：不修改major表和school表

## 注意事项

1. 该表是独立表，不关联其他表
2. 数据从major表提取，需要定期同步
3. 不要修改major表和school表的结构和数据
