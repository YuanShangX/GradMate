# GradMate 项目常见错误记录

> 最后更新：2026-02-13

---

## 错误1：未创建数据库表就开始开发 🔴

### 出现阶段
阶段1、阶段3、阶段4
 
### 错误表现
- 前端调用API返回 500 Internal Server Error
- 后端控制台：`Table 'gradmate.xxx' doesn't exist`

### 根本原因
写了SQL文件和后端代码，但忘记在MySQL中执行SQL创建表

### 正确流程
```
1. 编写SQL文件
2. ⚠️ 在MySQL中执行SQL创建表（关键！）
3. 验证表已创建：SHOW TABLES LIKE 'xxx%';
4. 创建Entity、Mapper、Service、Controller
5. ⚠️ 重启Spring Boot后端
6. 测试API
7. 开发前端
```

### 检查命令
```bash
# 检查表是否存在
mysql -u root -pA123456 -e "USE gradmate; SHOW TABLES LIKE 'study%';"

# 创建表
mysql -u root -pA123456 gradmate -e "CREATE TABLE IF NOT EXISTS xxx (...);"
```

---

## 错误2：Controller参数接收方式错误 🟡

### 出现阶段
阶段4

### 错误表现
- 前端调用API返回 400/500 错误
- 后端控制台：`Required request parameter 'xxx' is not present`

### 根本原因
前端发送JSON请求体，后端用 `@RequestParam` 接收（期望URL参数）

### 正确用法
```java
// ❌ 错误
@PostMapping("/plan")
public Result createPlan(@RequestParam Long userId, @RequestParam String title) { }

// ✅ 正确
@PostMapping("/plan")
public Result createPlan(@RequestBody StudyPlan request) { }
```

### 规范
- GET请求 → `@RequestParam` 接收URL参数
- POST/PUT请求 → `@RequestBody` 接收JSON请求体
- 路径变量 → `@PathVariable` 接收

---

## 错误3：新增Controller后未重启后端 🟡

### 出现阶段
阶段3、阶段4

### 错误表现
- 前端调用新API返回 404 Not Found
- 后端控制台没有错误信息

### 根本原因
Spring Boot启动时扫描Controller，新增Controller必须重启才能加载

### 解决方法
1. 停止Spring Boot应用
2. 重新运行 `GradMateSpringBootApplication`
3. 等待启动完成
4. 测试API

---

## 开发检查清单 ✅

### 数据库
- [ ] ⚠️ SQL文件已在MySQL中执行
- [ ] 表已创建（SHOW TABLES验证）

### 后端
- [ ] Entity、Mapper、Service、Controller已创建
- [ ] Controller参数接收方式正确
- [ ] ⚠️ Spring Boot后端已重启
- [ ] API已测试（浏览器/Postman）

### 前端
- [ ] API路径与后端一致
- [ ] 请求方式正确（GET/POST/PUT/DELETE）
- [ ] 前后端联调测试通过

---

## 快速排查500错误

1. 检查数据库表：`mysql -u root -pA123456 -e "USE gradmate; SHOW TABLES;"`
2. 查看后端控制台错误日志
3. 测试单个API：`http://localhost:9090/api/xxx`
4. 确认后端已重启

---

## 错误4：页面元素右边超出屏幕 🟡

### 出现阶段
前端开发阶段（聊天页面、卡片列表等）

### 错误表现
- 页面内容超出屏幕右边缘
- 出现横向滚动条
- 卡片或文字被截断

### 根本原因
父容器有 padding，但子元素没有设置正确的盒模型和宽度限制

### 问题分析
```css
/* 问题代码 */
.chat-container {
  padding: 20rpx 30rpx;  /* 左右各30rpx padding */
}

.message-card {
  /* 没有box-sizing和max-width */
  padding: 24rpx;
  /* 实际宽度 = 100% + padding，超出容器 */
}
```

### 解决方案
```css
/* ✅ 正确写法 */
.chat-container {
  padding: 20rpx 30rpx;
  box-sizing: border-box;  /* padding计算在宽度内 */
}

.message-card {
  box-sizing: border-box;  /* 关键！ */
  max-width: 100%;         /* 限制最大宽度 */
  word-wrap: break-word;   /* 长文本自动换行 */
  padding: 24rpx;
}

/* 有margin的元素需要减去margin */
.user-message {
  margin-left: 100rpx;
  max-width: calc(100% - 100rpx);  /* 考虑左边距 */
  box-sizing: border-box;
}
```

### 核心要点
1. 所有容器和子元素都加 `box-sizing: border-box`
2. 子元素设置 `max-width: 100%` 或 `max-width: calc(100% - margin值)`
3. 长文本添加 `word-wrap: break-word` 自动换行
4. 使用 margin 调节间距，而不是调整宽度

### 调试技巧
```css
/* 临时添加边框查看元素实际占用空间 */
.debug {
  border: 1px solid red !important;
}
```

---

## 错误5：Spring Boot依赖注入失败导致404 🔴

### 出现阶段
后端开发阶段

### 错误表现
- 前端调用API返回 404 Not Found
- Controller代码正确，路径也正确
- 后端启动无明显错误

### 根本原因
Service中 `@Autowired` 注入了不存在的依赖，导致Bean初始化失败，Controller无法注册

### 问题代码
```java
@Service
public class AssistantService {
    @Autowired
    private AiService aiService;  // ❌ AiService不存在
    // ...
}
```

### 解决方法
1. 检查 `@Autowired` 注入的所有依赖是否存在
2. 删除未使用的依赖注入
3. 重启Spring Boot应用
4. 查看启动日志确认Controller已注册

### 验证方法
启动后查看控制台日志，应该能看到：
```
Mapped "{[/api/xxx],methods=[POST]}" onto ...
```

---

不要用emoji图标，用css绘制图标;
经常出现外部链接无法跳转问题;
右边超出，用margin跳转；