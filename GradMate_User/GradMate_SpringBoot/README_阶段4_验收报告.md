# GradMate 阶段4：学习伴侣模块 - 验收报告

## 验收时间
2026-02-13

---

## 问题诊断（更新）

### 错误现象
前端调用学习伴侣模块的所有API时，均返回 500 Internal Server Error：
- `GET /api/study/stats?userId=1` - 500错误
- `GET /api/study/plan/user/1` - 500错误
- `GET /api/study/countdown?userId=1` - 500错误
- `GET /api/study/task/today?userId=1` - 500错误
- `POST /api/study/task` - 500错误

### 根本原因分析

经过两轮修复，问题依然存在。现在最可能的原因是：

**数据库表未创建** 或 **后端未正确重启**

500错误通常意味着：
1. 数据库表不存在 → MyBatis Plus查询时抛出异常
2. Mapper注入失败 → Service层调用Mapper时空指针异常
3. 后端代码未重新编译 → 修改的代码没有生效

---

## 紧急排查步骤

### 步骤1：确认数据库表是否存在 ⚠️

**在MySQL中执行以下命令**：

```sql
USE gradmate;
SHOW TABLES LIKE 'study%';
```

**预期结果**：应该看到3张表
```
+---------------------------+
| Tables_in_gradmate (study%) |
+---------------------------+
| study_plan                |
| study_record              |
| daily_task                |
+---------------------------+
```

**如果没有看到这3张表**，说明表还没创建！请立即执行：

```sql
-- 方法1：在MySQL客户端中直接复制粘贴执行
SOURCE GradMate_SpringBoot/src/main/resources/sql/study_tables.sql;

-- 方法2：或者使用我创建的测试脚本
SOURCE test_database.sql;
```

### 步骤2：查看后端控制台日志 ⚠️

**在IDEA的控制台中查找错误信息**，关键词：
- `Table 'gradmate.study_plan' doesn't exist`
- `NullPointerException`
- `No qualifying bean`
- `Error creating bean`

**请把完整的错误堆栈信息复制给我**，这样我才能准确定位问题！

### 步骤3：确认后端已重启 ⚠️

1. 在IDEA中**完全停止**Spring Boot应用（点击红色方块按钮）
2. 等待3秒
3. **重新运行** `GradMateSpringBootApplication`
4. 等待看到 `Started GradMateSpringBootApplication in X seconds`
5. 确认没有红色错误信息

### 步骤4：测试单个API

**在浏览器中直接访问**：
```
http://localhost:9090/api/study/stats?userId=1
```

**预期结果**：
- 如果表不存在：会看到 `Table 'gradmate.study_plan' doesn't exist` 错误
- 如果表存在但没数据：会返回 `{"code":200,"message":"success","data":{...}}`

---

## 已修复的代码问题

### 修复1：创建学习计划（已完成✅）
```java
// 修复前：使用 @RequestParam（错误）
@PostMapping("/plan")
public Result<StudyPlan> createPlan(
        @RequestParam Long userId,
        @RequestParam String title, ...) { }

// 修复后：使用 @RequestBody（正确）
@PostMapping("/plan")
public Result<StudyPlan> createPlan(@RequestBody StudyPlan request) {
    StudyPlan plan = studyService.createPlan(
        request.getUserId(), 
        request.getTitle(), 
        request.getContent(), 
        request.getTargetDate()
    );
    return Result.success(plan);
}
```

### 修复2：更新学习计划（已完成✅）
```java
@PutMapping("/plan/{id}")
public Result<Boolean> updatePlan(@PathVariable Long id, @RequestBody StudyPlan request) {
    boolean success = studyService.updatePlan(
        id, 
        request.getTitle(), 
        request.getContent(), 
        request.getTargetDate()
    );
    return success ? Result.success(true) : Result.error("更新失败");
}
```

### 修复3：更新任务（已完成✅）
```java
@PutMapping("/task/{id}")
public Result<Boolean> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
    boolean success = studyService.updateTask(id, request.getTaskContent(), request.getPriority());
    return success ? Result.success(true) : Result.error("更新失败");
}
```

---

## 快速验证命令

### 1. 验证数据库表
```bash
# Windows CMD
mysql -u root -pA123456 -e "USE gradmate; SHOW TABLES LIKE 'study%';"
```

### 2. 验证后端API
```bash
# 在浏览器中访问
http://localhost:9090/api/study/stats?userId=1
```

---

## 如果问题依然存在

请提供以下信息：

1. **数据库表检查结果**：
   ```sql
   SHOW TABLES LIKE 'study%';
   ```
   的输出结果

2. **后端控制台完整错误日志**：
   从IDEA控制台复制完整的红色错误信息（包括堆栈跟踪）

3. **浏览器访问API的结果**：
   访问 `http://localhost:9090/api/study/stats?userId=1` 后看到的完整响应

有了这些信息，我就能精确定位问题所在！

---

## 验收状态

- ✅ Controller参数接收方式已修复
- ⏳ 等待确认数据库表已创建
- ⏳ 等待确认后端已重启
- ⏳ 等待查看后端错误日志

---

**下一步**：请按照上面的步骤1-4依次检查，并把结果告诉我！
