# 研小伴快速参考指南

## 📋 快速导航

### 核心文档
- [UI优化说明](./README_研小伴UI风格优化说明.md) - 详细的优化说明
- [完成报告](./README_研小伴UI优化完成报告.md) - 完整的完成报告
- [对比图示](./README_研小伴UI优化对比图.md) - 前后对比说明
- [测试指南](./README_研小伴前端测试指南.md) - 测试清单
- [工作总结](./README_研小伴UI优化总结.md) - 项目总结

### 功能文档
- [多会话管理开发说明](./README_多会话管理功能开发说明.md)
- [多会话管理测试报告](./README_多会话管理测试报告.md)
- [多会话管理完成说明](./README_多会话管理功能完成说明.md)

## 🎨 设计规范速查

### 色彩系统
```css
/* 主色系 */
--primary-blue: #006cff;      /* 院校库蓝 */
--blue-light: #5b8ff9;        /* 浅蓝 */
--blue-dark: #378cff;         /* 深蓝 */
--blue-darker: #1b7dff;       /* 更深蓝 */

/* 背景色系 */
--bg-gradient-start: #e8f3ff; /* 渐变起始 */
--bg-gradient-end: #f6f7fb;   /* 渐变结束 */
--bg-white: #ffffff;          /* 白色 */

/* 辅助色系 */
--orange: #ff9500;            /* 橙色 */
--orange-dark: #ff7b00;       /* 深橙 */
--red: #ff4d4f;               /* 红色 */
--green: #00a870;             /* 绿色 */

/* 文字色系 */
--text-primary: #333333;      /* 主文字 */
--text-secondary: #666666;    /* 次要文字 */
--text-tertiary: #999999;     /* 辅助文字 */
```

### 间距规范
```css
/* 小间距 */
8rpx, 12rpx, 16rpx

/* 标准间距 */
20rpx, 24rpx, 30rpx

/* 大间距 */
40rpx, 60rpx, 80rpx, 120rpx
```

### 圆角规范
```css
/* 小圆角 */
8rpx, 12rpx

/* 标准圆角 */
16rpx, 20rpx

/* 大圆角 */
32rpx, 36rpx

/* 圆形 */
50%
```

### 字体规范
```css
/* 大标题 */
font-size: 36rpx;
font-weight: 600;

/* 标题 */
font-size: 32rpx;
font-weight: 600;

/* 正文 */
font-size: 28rpx;
font-weight: normal;

/* 辅助文字 */
font-size: 24rpx, 26rpx;

/* 小字 */
font-size: 22rpx;
```

### 阴影规范
```css
/* 轻阴影 */
box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

/* 标准阴影 */
box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);

/* 重阴影 */
box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
```

## 🎯 CSS图标速查

### 菜单图标（三条横线）
```css
.menu-icon {
  width: 32rpx;
  height: 24rpx;
  position: relative;
}
.menu-icon::before,
.menu-icon::after {
  content: '';
  position: absolute;
  left: 0;
  width: 100%;
  height: 3rpx;
  background: #ffffff;
  border-radius: 2rpx;
}
.menu-icon::before {
  top: 0;
  box-shadow: 0 10rpx 0 #ffffff;
}
.menu-icon::after {
  bottom: 0;
}
```

### 加号图标
```css
.plus-icon {
  width: 24rpx;
  height: 24rpx;
  position: relative;
}
.plus-icon::before,
.plus-icon::after {
  content: '';
  position: absolute;
  background: #ffffff;
  border-radius: 2rpx;
}
.plus-icon::before {
  width: 24rpx;
  height: 3rpx;
  top: 50%;
  left: 0;
  transform: translateY(-50%);
}
.plus-icon::after {
  width: 3rpx;
  height: 24rpx;
  left: 50%;
  top: 0;
  transform: translateX(-50%);
}
```

### AI头像（机器人）
```css
.ai-avatar {
  width: 70rpx;
  height: 70rpx;
  background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
  border-radius: 50%;
  position: relative;
}
.ai-avatar::before {
  content: '';
  position: absolute;
  width: 28rpx;
  height: 28rpx;
  background: #ffffff;
  border-radius: 50%;
  top: 15rpx;
  left: 12rpx;
}
.ai-avatar::after {
  content: '';
  position: absolute;
  width: 28rpx;
  height: 28rpx;
  background: #ffffff;
  border-radius: 50%;
  top: 15rpx;
  right: 12rpx;
}
```

### 用户头像
```css
.user-avatar {
  width: 70rpx;
  height: 70rpx;
  background: linear-gradient(135deg, #ff9500 0%, #ff7b00 100%);
  border-radius: 50%;
  position: relative;
}
.user-avatar::before {
  content: '';
  position: absolute;
  width: 32rpx;
  height: 32rpx;
  background: #ffffff;
  border-radius: 50%;
  top: 12rpx;
  left: 50%;
  transform: translateX(-50%);
}
.user-avatar::after {
  content: '';
  position: absolute;
  width: 40rpx;
  height: 20rpx;
  background: #ffffff;
  border-radius: 20rpx 20rpx 0 0;
  bottom: 8rpx;
  left: 50%;
  transform: translateX(-50%);
}
```

### 发送箭头
```css
.send-icon {
  width: 28rpx;
  height: 28rpx;
  border: 3rpx solid #ffffff;
  border-left: none;
  border-bottom: none;
  transform: rotate(45deg);
  margin-left: 4rpx;
}
```

## 🔧 常用渐变

### 内容区背景
```css
background: linear-gradient(180deg, #e8f3ff 0%, #f6f7fb 15%, #f6f7fb 100%);
```

### 按钮渐变
```css
background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
```

### 侧边栏头部
```css
background: linear-gradient(135deg, #e8f3ff 0%, #ffffff 100%);
```

### 激活会话
```css
background: linear-gradient(135deg, #e8f3ff 0%, #f0f8ff 100%);
```

## 📱 页面结构

```
┌─────────────────────────────┐
│ 状态栏占位                   │
├─────────────────────────────┤
│ 蓝色顶部区域 (#006cff)       │
│ ≡  研小伴  ⊕                │
├─────────────────────────────┤
│ ╭───────────────────────╮   │
│ │ 白色内容区（圆角）     │   │
│ │ 渐变背景               │   │
│ │                       │   │
│ │ 消息列表               │   │
│ │                       │   │
│ ├───────────────────────┤   │
│ │ 输入栏                 │   │
│ ╰───────────────────────╯   │
└─────────────────────────────┘
```

## 🚀 快速启动

### 后端启动
```bash
cd GradMate_SpringBoot
mvn spring-boot:run
# 或
java -jar target/GradMate_SpringBoot-0.0.1-SNAPSHOT.jar
```

### 前端启动
```bash
# 在HBuilderX中
1. 打开项目 GradMate_Uniapp
2. 运行 -> 运行到浏览器 -> Chrome
3. 导航到 /pages/ai/chat
```

## 🧪 快速测试

### 基础测试
```bash
1. 打开研小伴页面
2. 检查顶部蓝色区域
3. 检查CSS图标显示
4. 检查渐变效果
5. 发送测试消息
```

### 功能测试
```bash
1. 创建新会话
2. 切换会话
3. 重命名会话
4. 删除会话
5. 发送消息
6. 查看AI回复
```

## 📊 API接口

### 会话管理
```javascript
// 创建会话
POST /api/ai/session/create
{ userId: 1 }

// 获取会话列表
GET /api/ai/session/list?userId=1

// 重命名会话
PUT /api/ai/session/{sessionId}/title
{ title: "新标题" }

// 删除会话
DELETE /api/ai/session/{sessionId}
```

### AI对话
```javascript
// 发送消息
POST /api/ai/chat
{
  userId: 1,
  sessionId: "xxx",
  message: "你好"
}
```

## 🐛 常见问题

### Q1: 图标不显示？
```
A: 检查CSS伪元素是否生效，确认浏览器支持
```

### Q2: 渐变不显示？
```
A: 检查linear-gradient语法，确认浏览器支持
```

### Q3: 动画卡顿？
```
A: 使用transform代替position，避免重绘
```

### Q4: 侧边栏不滑出？
```
A: 检查transition属性，确认left值变化
```

### Q5: 后端连接失败？
```
A: 确认后端服务运行在9090端口
```

## 📝 代码片段

### 渐变按钮
```css
.btn {
  background: linear-gradient(135deg, #5b8ff9 0%, #378cff 100%);
  border-radius: 50%;
  box-shadow: 0 4rpx 12rpx rgba(91, 143, 249, 0.3);
}
```

### 消息气泡
```css
.message-bubble {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 24rpx 28rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}
```

### 加载动画
```css
.dot {
  width: 8rpx;
  height: 8rpx;
  background: #ccc;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}
```

## 🎓 学习资源

### CSS技巧
- 伪元素绘制图标
- 渐变背景实现
- 动画性能优化
- Flex布局应用

### 设计原则
- 色彩系统统一
- 视觉层次清晰
- 交互反馈及时
- 品牌一致性强

## 📞 联系方式

### 技术支持
- 文档: 查看相关README文档
- 代码: 查看chat.vue源码
- 测试: 参考测试指南

### 问题反馈
- 记录问题详情
- 提供复现步骤
- 附上截图说明
- 标注优先级

---

**更新时间**: 2026-02-19  
**版本**: v1.0  
**状态**: ✅ 已完成
