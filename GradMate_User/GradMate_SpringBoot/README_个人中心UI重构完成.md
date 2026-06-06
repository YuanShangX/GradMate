# 个人中心UI重构完成说明

> 完成时间：2026-02-20  
> 重构范围：个人中心主页 (profile.vue)

---

## 重构目标

参考院校库的设计风格，统一整个应用的视觉体验，移除所有emoji图标，使用CSS绘制图标。

---

## 主要改动

### 1. 整体布局风格统一

**改动前：**
- 灰色背景 (#F5F5F5)
- 传统卡片式布局
- 使用emoji图标

**改动后：**
- 蓝色顶部区域 (#006cff) + 白色内容区
- 圆角过渡设计 (border-radius: 32rpx)
- 渐变背景 (linear-gradient)
- 所有图标使用CSS绘制

---

### 2. 未登录状态优化

**CSS绘制的登录图标：**
```css
.login-icon {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 60rpx;
}

/* 使用伪元素绘制用户头像和身体 */
.login-icon::before { /* 头部圆形 */ }
.login-icon::after { /* 身体半圆 */ }
```

---

### 3. 用户信息卡片优化

**改动：**
- 渐变背景：`linear-gradient(135deg, #e0f6ff 0%, #d4e6ff 100%)`
- 头像添加白色边框
- 编辑按钮改为CSS绘制的编辑图标（笔形）

**编辑图标实现：**
```css
.edit-icon::before { /* 纸张 */ }
.edit-icon::after { /* 笔尖 */ }
```

---

### 4. 目标设定卡片优化

**改动：**
- 添加CSS绘制的目标图标
  - 院校图标：蓝色渐变 + 建筑物形状
  - 专业图标：橙色渐变 + 书本形状
- 增强版卡片使用渐变背景和边框
- 按钮使用蓝色渐变

**图标实现：**
```css
/* 院校图标 */
.school-target-icon {
  background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}
.school-target-icon::before { /* 建筑物窗户 */ }
.school-target-icon::after { /* 建筑物底部 */ }

/* 专业图标 */
.major-target-icon {
  background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}
.major-target-icon::before { /* 书本主体 */ }
.major-target-icon::after { /* 书本书脊 */ }
```

---

### 5. 数据统计卡片重构

**改动前：**
- 统一紫色渐变背景
- 白色文字
- 无图标

**改动后：**
- 白色卡片背景
- 顶部彩色条纹区分类别
  - 蓝色：学习时长
  - 橙色：连续打卡
  - 紫色：收藏数量
  - 绿色：完成任务
- 每个卡片添加对应的CSS图标
- 深色文字，更易阅读

**图标实现：**
```css
/* 学习图标 - 时钟 */
.study-icon::before { /* 圆形表盘 */ }
.study-icon::after { /* 时针 */ }

/* 打卡图标 - 日历 */
.checkin-icon::before { /* 日历框 */ }
.checkin-icon::after { /* 日期横线 */ }

/* 收藏图标 - 星星 */
.favorite-icon::before { /* 上三角 */ }
.favorite-icon::after { /* 下三角 */ }

/* 任务图标 - 对勾 */
.task-icon::before { /* 勾形 */ }
```

---

### 6. 功能菜单优化

**改动：**
- 移除emoji图标
- 添加CSS绘制的彩色图标
  - 收藏：紫色星星
  - 浏览历史：蓝色时钟
  - 学习统计：橙色书本
  - 退出登录：红色门形图标
- 添加"浏览历史"入口
- 箭头符号统一为 `›`

**图标实现：**
```css
/* 收藏图标 */
.favorite-menu-icon {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
}

/* 历史图标 */
.history-menu-icon {
  background: linear-gradient(135deg, #378cff 0%, #1b7dff 100%);
}

/* 学习图标 */
.study-menu-icon {
  background: linear-gradient(135deg, #ffb84d 0%, #ff9500 100%);
}

/* 退出图标 */
.logout-menu-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
}
.logout-menu-icon::before { /* 门框 */ }
.logout-menu-icon::after { /* 箭头 */ }
```

---

### 7. 代码优化

**改动：**
- 从 Options API 迁移到 Composition API (setup)
- 使用 `ref` 管理响应式数据
- 优化数据加载逻辑
- 添加状态栏占位

---

## 设计规范

### 颜色体系

| 用途 | 颜色值 | 说明 |
|------|--------|------|
| 主色调 | #006cff | 蓝色，用于顶部和主要按钮 |
| 背景渐变起点 | #e8f3ff | 浅蓝色 |
| 背景渐变终点 | #f6f7fb | 浅灰蓝 |
| 卡片背景 | #ffffff | 白色 |
| 蓝色图标 | #378cff → #1b7dff | 渐变 |
| 橙色图标 | #ffb84d → #ff9500 | 渐变 |
| 紫色图标 | #a78bfa → #8b5cf6 | 渐变 |
| 绿色图标 | #52c990 → #00a870 | 渐变 |
| 红色图标 | #ff6b6b → #ee5a52 | 渐变 |

### 圆角规范

| 元素 | 圆角值 |
|------|--------|
| 内容区顶部 | 32rpx |
| 大卡片 | 20rpx |
| 中卡片 | 16rpx |
| 小卡片 | 12rpx |
| 图标 | 8-12rpx |
| 按钮 | 12-40rpx |

### 间距规范

| 用途 | 间距值 |
|------|--------|
| 页面左右边距 | 30rpx |
| 卡片间距 | 24rpx |
| 卡片内边距 | 28-32rpx |
| 元素间距 | 16rpx |
| 小间距 | 8rpx |

---

## CSS图标绘制技巧

### 1. 使用伪元素

每个图标容器可以使用 `::before` 和 `::after` 绘制两个形状：

```css
.icon {
  position: relative;
}

.icon::before {
  content: '';
  position: absolute;
  /* 形状1 */
}

.icon::after {
  content: '';
  position: absolute;
  /* 形状2 */
}
```

### 2. 常用形状

**圆形：**
```css
width: 20rpx;
height: 20rpx;
border-radius: 50%;
background: #fff;
```

**方形：**
```css
width: 20rpx;
height: 20rpx;
border-radius: 4rpx;
background: #fff;
```

**三角形：**
```css
width: 0;
height: 0;
border-left: 10rpx solid transparent;
border-right: 10rpx solid transparent;
border-bottom: 16rpx solid #fff;
```

**对勾：**
```css
width: 6rpx;
height: 12rpx;
border-right: 3rpx solid #fff;
border-bottom: 3rpx solid #fff;
transform: rotate(45deg);
```

### 3. 渐变背景

```css
background: linear-gradient(135deg, #起始色 0%, #结束色 100%);
```

---

## 测试清单

- [x] 未登录状态显示正常
- [x] 登录后用户信息显示正常
- [x] 目标设定卡片显示正常
- [x] 数据统计卡片显示正常
- [x] 功能菜单显示正常
- [x] 所有图标使用CSS绘制
- [x] 无emoji图标
- [x] 点击跳转功能正常
- [x] 退出登录功能正常
- [x] 响应式布局正常

---

## 后续优化建议

1. **动画效果**
   - 添加卡片点击动画
   - 添加数据加载骨架屏
   - 添加页面切换过渡动画

2. **数据可视化**
   - 学习时长添加进度条
   - 连续打卡添加日历热力图
   - 收藏数量添加分类统计

3. **个性化**
   - 支持主题色切换
   - 支持头像上传
   - 支持背景图自定义

---

## 相关文件

- 前端页面：`GradMate_Uniapp/pages/profile/profile.vue`
- 参考页面：`GradMate_Uniapp/pages/school/index.vue`

---

个人中心主页UI重构完成，视觉风格与院校库保持一致，所有图标使用CSS绘制，无emoji。
