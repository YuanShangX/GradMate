# 个人中心404错误修复说明

> 问题发现时间: 2026-02-20  
> 错误类型: 404 Not Found

---

## 🐛 问题描述

前端调用新增的两个接口时返回404错误:
- `GET /api/user/stats/11` - 404 Not Found
- `GET /api/user/target/detail/11` - 404 Not Found

但是收藏统计接口正常:
- `GET /api/favorite/count?userId=11` - 200 OK

---

## 🔍 问题分析

### 根本原因
新增的接口代码已经添加到`UserController.java`中,但是**Spring Boot服务没有重启**,导致新接口没有被加载到路由映射中。

### 验证方法
1. 检查代码 - ✅ 接口代码已存在
2. 检查编译 - ✅ Maven编译成功
3. 检查服务 - ❌ 服务未重启,新接口未加载

---

## ✅ 解决方案

### 方案1: 重启Spring Boot服务(推荐)

#### 步骤1: 停止当前服务
在IDEA中点击红色停止按钮,或者在命令行按 `Ctrl+C`

#### 步骤2: 重新启动服务
```bash
# 方式1: 使用IDEA
右键 GradMateSpringBootApplication.java -> Run

# 方式2: 使用Maven命令
cd GradMate_SpringBoot
mvn spring-boot:run
```

#### 步骤3: 验证服务启动
查看控制台输出,确认看到:
```
Mapped "{[/api/user/stats/{userId}],methods=[GET]}" onto ...
Mapped "{[/api/user/target/detail/{userId}],methods=[GET]}" onto ...
```

#### 步骤4: 测试接口
```bash
# 测试统计接口
curl http://localhost:9090/api/user/stats/11

# 测试目标详情接口
curl http://localhost:9090/api/user/target/detail/11
```

---

### 方案2: 使用Spring Boot DevTools(可选)

如果频繁修改代码,可以启用热重载功能。

#### 添加依赖
在`pom.xml`中添加:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

#### 配置IDEA
1. File -> Settings -> Build, Execution, Deployment -> Compiler
2. 勾选 "Build project automatically"
3. File -> Settings -> Advanced Settings
4. 勾选 "Allow auto-make to start even if developed application is currently running"

---

## 📋 完整的接口测试清单

### 1. 用户统计接口
```bash
curl http://localhost:9090/api/user/stats/11
```

**预期响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "userId": 11,
    "nickname": "测试用户"
  }
}
```

---

### 2. 目标详情接口
```bash
curl http://localhost:9090/api/user/target/detail/11
```

**预期响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "targetSchool": {
      "id": 1,
      "name": "清华大学"
    },
    "targetMajor": {
      "id": 100,
      "name": "计算机科学与技术"
    }
  }
}
```

---

### 3. 收藏统计接口(验证旧接口正常)
```bash
curl http://localhost:9090/api/favorite/count?userId=11
```

**预期响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "school": 2,
    "major": 2,
    "info": 1
  }
}
```

---

## 🔧 前端修复(可选)

如果后端接口暂时不可用,可以在前端添加容错处理:

```javascript
// 加载用户统计数据
async loadUserStats() {
    try {
        const res = await get(`/user/stats/${this.userInfo.id}`);
        if (res.code === 200) {
            this.stats = {
                ...this.stats,
                ...res.data
            };
        }
    } catch (error) {
        console.error('加载统计数据失败:', error);
        // 使用默认值,不影响页面显示
        this.stats = {
            totalStudyDuration: 0,
            continuousDays: 0,
            totalFavorites: this.favoriteCount || 0,
            completedTasks: 0
        };
    }
},

// 加载目标详细信息
async loadTargetDetail() {
    try {
        const res = await get(`/user/target/detail/${this.userInfo.id}`);
        if (res.code === 200) {
            this.targetDetail = res.data;
        }
    } catch (error) {
        console.error('加载目标详情失败:', error);
        // 使用用户基本信息作为后备
        this.targetDetail = {
            targetSchool: this.userInfo.targetSchool ? {
                id: this.userInfo.targetSchoolId,
                name: this.userInfo.targetSchool
            } : null,
            targetMajor: this.userInfo.targetMajor ? {
                id: this.userInfo.targetMajorId,
                name: this.userInfo.targetMajor
            } : null
        };
    }
}
```

---

## 🎯 验证步骤

### 1. 后端验证
- [ ] Spring Boot服务已重启
- [ ] 控制台显示新接口映射
- [ ] 使用curl测试接口返回200
- [ ] 接口返回数据格式正确

### 2. 前端验证
- [ ] 刷新浏览器页面
- [ ] 个人中心页面正常加载
- [ ] 统计数据卡片显示正确
- [ ] 目标卡片显示正确
- [ ] 无404错误

### 3. 功能验证
- [ ] 点击"查看详情"按钮正常跳转
- [ ] 数据统计数值准确
- [ ] 页面样式正常显示

---

## 📝 常见问题

### Q1: 重启后还是404?
**A**: 检查以下几点:
1. 确认代码已保存
2. 确认Maven编译成功
3. 确认端口9090没有被占用
4. 查看控制台是否有错误日志

### Q2: 接口返回500错误?
**A**: 可能是数据库连接问题或业务逻辑错误:
1. 检查数据库是否启动
2. 检查用户ID是否存在
3. 查看后端控制台错误堆栈

### Q3: 前端还是显示加载失败?
**A**: 
1. 清除浏览器缓存
2. 检查网络请求是否成功
3. 查看浏览器控制台错误信息
4. 确认baseURL配置正确

---

## 🚀 快速修复命令

```bash
# 1. 停止服务(如果正在运行)
# 在IDEA中点击停止按钮,或按Ctrl+C

# 2. 编译项目
cd GradMate_SpringBoot
mvn clean compile

# 3. 启动服务
mvn spring-boot:run

# 4. 等待启动完成(看到"Started GradMateSpringBootApplication"字样)

# 5. 测试接口
curl http://localhost:9090/api/user/stats/11
curl http://localhost:9090/api/user/target/detail/11

# 6. 刷新前端页面
# 在浏览器中按F5刷新
```

---

## ✅ 修复确认

修复完成后,应该看到:

### 后端控制台
```
Mapped "{[/api/user/stats/{userId}],methods=[GET]}" onto public com.gradmate.gradmate_springboot.common.Result com.gradmate.gradmate_springboot.controller.UserController.getUserStats(java.lang.Long)

Mapped "{[/api/user/target/detail/{userId}],methods=[GET]}" onto public com.gradmate.gradmate_springboot.common.Result com.gradmate.gradmate_springboot.controller.UserController.getTargetDetail(java.lang.Long)
```

### 前端浏览器
- 无404错误
- 统计数据正常显示
- 目标卡片正常显示
- 功能正常使用

---

## 📚 相关文档

- [个人中心模块完善方案](./README_个人中心模块完善方案.md)
- [个人中心模块测试指南](./README_个人中心模块完善测试指南.md)
- [个人中心模块完成说明](./README_个人中心模块完善完成说明.md)
- [常见错误处理](../Skills/常见错误_SKILL.md)

---

**记住: 每次新增或修改Controller接口后,都需要重启Spring Boot服务!** 🔄
