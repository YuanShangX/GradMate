# 专注计时页面优化完成说明

> 完成时间：2026-02-20  
> 优化内容：专注计时页面 UI 升级，采用院校库风格设计

---

## 优化内容

### 1. UI 风格统一

采用院校库的设计风格：
- 白色卡片设计
- 圆角元素
- 柔和阴影
- 统一的色彩体系（#5b8ff9 主色调）

### 2. 新增功能

#### 2.1 进度环显示
- 使用 SVG 绘制圆形进度条
- 实时显示剩余时间进度
- 暂停时进度环变为橙色（#faad14）

#### 2.2 统计信息卡片
新增两个统计卡片：
- 专注次数：显示当前专注会话数
- 暂停次数：记录用户暂停的次数

#### 2.3 优化控制按钮
- 按钮改为横向长条形设计
- 添加文字标签（"继续"/"暂停"、"结束"）
- 更清晰的视觉反馈

### 3. CSS 图标设计

所有图标均使用 CSS 绘制，无 emoji：

#### 任务图标
```css
.task-icon {
  width: 36rpx;
  height: 36rpx;
  border: 3rpx solid #5b8ff9;
  border-radius: 8rpx;
  /* 使用伪元素绘制横线 */
}
```

#### 专注图标
```css
.focus-icon {
  width: 28rpx;
  height: 28rpx;
  border: 3rpx solid #fa8c16;
  border-radius: 50%;
  /* 中心圆点 */
}
```

#### 暂停计数图标
```css
.pause-count-icon {
  /* 两条竖线 */
  width: 24rpx;
  height: 28rpx;
}
```

#### 播放/暂停/停止图标
- 播放：三角形（border 实现）
- 暂停：两条竖线
- 停止：圆角矩形

---

## 页面结构

```
专注计时页面
├── 导航栏（返回按钮 + 标题）
├── 激励语区域
├── 倒计时显示区域
│   ├── SVG 进度环
│   └── 时间显示
├── 任务信息卡片
├── 统计信息行
│   ├── 专注次数
│   └── 暂停次数
└── 控制按钮区域
    ├── 暂停/继续按钮
    └── 结束按钮
```

---

## 数据流转

### 1. 页面初始化
```javascript
onMounted(() => {
  // 获取任务参数
  taskId = options.taskId
  taskName = options.taskName || '学习任务'
  duration = parseInt(options.duration) || 25
  
  // 初始化倒计时
  remainingSeconds = duration * 60
  
  // 开始计时
  startFocus()
})
```

### 2. 计时逻辑
```javascript
const startTimer = () => {
  timer = setInterval(() => {
    if (remainingSeconds > 0) {
      remainingSeconds--
    } else {
      onComplete() // 完成
    }
  }, 1000)
}
```

### 3. 暂停/继续
```javascript
const togglePause = () => {
  if (isPaused) {
    // 继续：恢复计时
    isPaused = false
    startTimer()
  } else {
    // 暂停：停止计时，增加暂停次数
    isPaused = true
    pauseCount++
    clearTimer()
  }
}
```

### 4. 保存记录
```javascript
const saveFocusRecord = async () => {
  // 计算实际学习时长
  const actualDuration = Math.floor((duration * 60 - remainingSeconds) / 60)
  
  // 调用打卡接口
  await post('/study/checkin', {
    userId,
    studyDuration: actualDuration,
    focusCount: focusCount,
    pauseCount: pauseCount,
    notes: `完成任务：${taskName}`
  })
  
  // 标记任务完成
  if (taskId) {
    await post(`/study/task/${taskId}/complete`)
  }
}
```

---

## 进度环实现

### SVG 圆形进度条
```html
<svg class="progress-ring" viewBox="0 0 200 200">
  <!-- 背景圆环 -->
  <circle 
    class="progress-ring-bg" 
    cx="100" 
    cy="100" 
    r="90"
  />
  <!-- 进度圆环 -->
  <circle 
    class="progress-ring-circle" 
    cx="100" 
    cy="100" 
    r="90"
    :style="{ strokeDashoffset: progressOffset }"
  />
</svg>
```

### 进度计算
```javascript
const progressOffset = computed(() => {
  const totalSeconds = duration * 60
  const progress = remainingSeconds / totalSeconds
  const circumference = 2 * Math.PI * 90
  return circumference * (1 - progress)
})
```

---

## 视觉效果

### 1. 颜色方案
- 主色调：#5b8ff9（蓝色）
- 暂停色：#faad14（橙色）
- 停止色：#ff4d4f（红色）
- 背景色：#f0f3fa（浅灰蓝）
- 卡片背景：#ffffff（白色）

### 2. 阴影效果
```css
box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
```

### 3. 圆角设计
- 卡片圆角：16rpx
- 按钮圆角：50rpx（完全圆角）
- 图标圆角：8rpx

---

## 交互反馈

### 1. 震动反馈
- 开始计时：短震动
- 暂停/继续：短震动
- 完成：长震动

### 2. 视觉反馈
- 暂停时进度环变色
- 按钮按下效果
- 状态文字变化

### 3. 提示信息
- 退出确认弹窗
- 停止确认弹窗
- 完成提示

---

## 测试要点

### 1. 功能测试
- [ ] 倒计时正常运行
- [ ] 暂停/继续功能正常
- [ ] 进度环显示正确
- [ ] 统计数据准确
- [ ] 完成后正确保存记录

### 2. UI 测试
- [ ] 所有图标正常显示
- [ ] 进度环动画流畅
- [ ] 按钮样式正确
- [ ] 响应式布局正常

### 3. 交互测试
- [ ] 震动反馈正常
- [ ] 弹窗提示正常
- [ ] 返回逻辑正确

---

## 使用方式

### 从任务列表跳转
```javascript
uni.navigateTo({
  url: `/pages/study/focus?taskId=${task.id}&taskName=${task.taskContent}&duration=25`
})
```

### 参数说明
- `taskId`：任务 ID（可选）
- `taskName`：任务名称（默认"学习任务"）
- `duration`：时长（分钟，默认 25）

---

## 优化效果

### 优化前
- 简单的圆形计时器
- 圆形控制按钮
- 缺少统计信息
- 视觉效果单一

### 优化后
- 带进度环的计时器
- 长条形控制按钮（带文字）
- 新增统计卡片
- 统一的院校库风格
- 所有图标使用 CSS 绘制

---

## 后续优化建议

1. 添加背景音乐/白噪音选项
2. 支持自定义专注时长
3. 添加番茄钟模式（25分钟+5分钟休息）
4. 记录每日专注时长趋势
5. 添加专注排行榜

---

## 相关文件

- 前端页面：`GradMate_Uniapp/pages/study/focus.vue`
- 后端接口：`StudyController.java` - `/study/checkin`
- 数据库表：`study_record`

---

## 总结

本次优化完成了专注计时页面的 UI 升级，采用了与院校库一致的设计风格，所有图标均使用 CSS 绘制，无 emoji。新增了进度环显示和统计信息，提升了用户体验和视觉效果。
