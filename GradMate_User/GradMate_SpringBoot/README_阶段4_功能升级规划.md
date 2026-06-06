# 阶段4：学习伴侣模块 - 功能升级规划

## 升级时间
2026-02-13

---

## 升级目标

将现有的简单学习伴侣模块升级为**专注学习系统**，参考专业的专注计时APP（如Forest、番茄ToDo），提供更丰富的学习体验和数据可视化。

---

## 现有功能回顾

### 已实现功能 ✅
1. 学习计划管理（CRUD）
2. 每日任务清单（CRUD、优先级）
3. 打卡日历（月历视图、打卡记录）
4. 考研倒计时
5. 学习统计（连续天数、总时长）

### 存在问题 ❌
1. 界面单调，缺少视觉吸引力
2. 没有专注计时功能（核心缺失）
3. 统计数据展示简单，缺少图表
4. 缺少激励机制（白噪音、激励语）
5. 任务管理功能薄弱

---

## 升级功能清单

### 功能1：专注计时器 ⭐⭐⭐

#### 功能描述
提供番茄钟式的专注计时功能，帮助用户集中注意力学习。

#### 前端实现
**新增页面**：`pages/study/focus.vue`

**页面元素**：
- 顶部激励语句（随机显示）
- 大字体倒计时显示（00:00格式）
- 任务名称和状态（进行中/已暂停）
- 底部控制栏：
  - 暂停/继续按钮
  - 停止按钮
  - 白噪音开关（可选）
  - 亮度调节（可选）

**交互逻辑**：
1. 从任务列表点击"开始"进入专注页面
2. 倒计时开始，每秒更新显示
3. 可暂停/继续/停止
4. 完成后自动记录学习时长
5. 震动反馈+完成动画

#### 后端实现
**新增接口**：
- `POST /api/study/focus/start` - 开始专注
- `POST /api/study/focus/pause` - 暂停专注
- `POST /api/study/focus/resume` - 继续专注
- `POST /api/study/focus/stop` - 停止专注（记录时长）

**数据库表**：复用 `study_record` 表，新增字段：
- `focus_count` INT - 专注次数
- `pause_count` INT - 暂停次数

---

### 功能2：任务卡片美化 ⭐⭐

#### 功能描述
将任务列表改为彩色渐变卡片，提升视觉效果。

#### 前端实现
**修改页面**：`pages/study/task.vue`

**卡片样式**：
- 使用渐变背景色（蓝色、粉色、绿色、紫色、青色等）
- 圆角卡片设计
- 显示任务名称、预计时长、"开始"按钮
- 支持长按编辑/删除

**渐变色方案**：
```css
/* 蓝色渐变 */
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

/* 粉色渐变 */
background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);

/* 绿色渐变 */
background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);

/* 紫色渐变 */
background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);

/* 青色渐变 */
background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
```

#### 后端实现
**新增字段**：`daily_task` 表新增：
- `color_theme` VARCHAR(20) - 卡片颜色主题（blue/pink/green/purple/cyan）
- `estimated_duration` INT - 预计学习时长（分钟）

---

### 功能3：数据统计可视化 ⭐⭐⭐

#### 功能描述
使用图表展示学习数据，提供更直观的统计分析。

#### 前端实现
**修改页面**：`pages/study/stats.vue`（新建）

**统计卡片**：
1. **累计专注卡片**
   - 专注次数
   - 专注时长
   - 目标时长
   - 完成百分比

2. **当日专注卡片**
   - 今日次数
   - 今日时长
   - 日期选择器

3. **专注时长分布图**
   - Tab切换：日/周/月/自定义
   - 柱状图展示每日学习时长
   - 使用 uCharts 或 ECharts

4. **时段分布图**
   - 显示一天中各时段的学习分布
   - 热力图或柱状图

#### 后端实现
**新增接口**：
- `GET /api/study/stats/daily` - 获取每日统计
- `GET /api/study/stats/weekly` - 获取每周统计
- `GET /api/study/stats/monthly` - 获取每月统计
- `GET /api/study/stats/hourly` - 获取时段分布

**返回数据格式**：
```json
{
  "code": 200,
  "data": {
    "dates": ["2026-02-01", "2026-02-02", ...],
    "durations": [120, 90, 150, ...],
    "focusCounts": [5, 4, 6, ...]
  }
}
```

---

### 功能4：倒计时目标美化 ⭐

#### 功能描述
支持自定义背景图，显示更详细的进度信息。

#### 前端实现
**修改组件**：`components/Countdown.vue`

**新增功能**：
- 支持上传背景图（或选择预设图片）
- 显示已过天数/剩余天数
- 进度条显示（已过/总天数）
- 大卡片样式（类似图片中的效果）

#### 后端实现
**修改表**：`study_plan` 表新增：
- `background_image` VARCHAR(255) - 背景图URL
- `start_date` DATE - 开始日期
- `total_days` INT - 总天数

---

### 功能5：激励语句库 ⭐

#### 功能描述
在专注页面和主页随机显示激励语句，增强情感陪伴。

#### 前端实现
**激励语句库**（前端配置）：
```javascript
const motivationalQuotes = [
  "微尘莫若都可让我动容，却无事可使我动摇",
  "你的努力，时间看得见",
  "每一次专注，都是在靠近梦想",
  "坚持下去，未来可期",
  "今天的努力，是明天的底气",
  // ... 更多语句
];
```

**显示位置**：
- 专注计时页面顶部
- 学习伴侣主页卡片
- 完成任务后的祝贺页面

---

### 功能6：待办任务列表页 ⭐⭐

#### 功能描述
将现有的任务管理页面改造为"待办"主页，展示所有待完成的学习任务卡片。

#### 前端实现
**修改页面**：`pages/study/task.vue` → 改为待办主页风格

**页面布局**：
- 顶部工具栏：统计图表、地图（可选）、添加任务按钮
- 任务卡片列表：
  - 彩色渐变背景
  - 任务标题
  - 预计时长
  - "开始"按钮（点击进入专注计时）
- 底部导航：待办、待办单、锁机（可选）、统计数据、我的

**任务卡片交互**：
- 点击"开始"按钮 → 跳转到专注计时页面
- 长按卡片 → 显示编辑/删除菜单
- 左滑卡片 → 显示删除按钮

#### 后端实现
复用现有的任务管理接口，无需新增。

---

### 功能7：待办单管理页 ⭐

#### 功能描述
提供更详细的任务清单管理功能，支持任务分组、排序、筛选。

#### 前端实现
**新增页面**：`pages/study/tasklist.vue`

**页面功能**：
- 任务列表（简洁样式，带复选框）
- 任务分组（今日/本周/全部）
- 任务排序（按优先级/时间/科目）
- 批量操作（批量删除/完成）
- 搜索功能

#### 后端实现
复用现有接口，可新增：
- `GET /api/study/task/group` - 按分组获取任务
- `POST /api/study/task/batch` - 批量操作任务

---

## 数据库表结构调整

### 1. 修改 `daily_task` 表
```sql
ALTER TABLE `daily_task`
ADD COLUMN `color_theme` VARCHAR(20) DEFAULT 'blue' COMMENT '卡片颜色主题',
ADD COLUMN `estimated_duration` INT DEFAULT 25 COMMENT '预计学习时长（分钟）';
```

### 2. 修改 `study_record` 表
```sql
ALTER TABLE `study_record`
ADD COLUMN `focus_count` INT DEFAULT 0 COMMENT '专注次数',
ADD COLUMN `pause_count` INT DEFAULT 0 COMMENT '暂停次数';
```

### 3. 修改 `study_plan` 表
```sql
ALTER TABLE `study_plan`
ADD COLUMN `background_image` VARCHAR(255) COMMENT '背景图URL',
ADD COLUMN `start_date` DATE COMMENT '开始日期',
ADD COLUMN `total_days` INT COMMENT '总天数';
```

---

## 开发优先级

### P0（必须实现）⭐⭐⭐
1. 专注计时器功能
2. 任务卡片美化
3. 数据统计图表

### P1（重要功能）⭐⭐
4. 倒计时目标美化
5. 激励语句库

### P1（重要功能）⭐⭐
6. 待办任务列表页

### P2（锦上添花）⭐
7. 待办单管理页

---

## 开发时间估算

- 专注计时器：2-3天
- 任务卡片美化：1天
- 数据统计图表：2-3天
- 倒计时美化：1天
- 激励语句：0.5天
- 待办任务列表页：1天
- 待办单管理页：1天（可选）

**总计**：8-11天

---

## 技术要点

### 1. 倒计时实现
使用 `setInterval` 每秒更新：
```javascript
let timer = setInterval(() => {
  if (seconds > 0) {
    seconds--;
    updateDisplay();
  } else {
    clearInterval(timer);
    onComplete();
  }
}, 1000);
```

### 2. 图表库选择
推荐使用 **uCharts**（Uniapp专用）：
- 轻量级，性能好
- 支持多种图表类型
- 文档完善

安装：
```bash
npm install @qiun/ucharts
```

### 3. 渐变背景实现
```css
.task-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16rpx;
  padding: 40rpx;
  margin-bottom: 20rpx;
}
```

### 4. 左滑删除实现
```javascript
// 使用 uni-swipe-action 组件
<uni-swipe-action>
  <uni-swipe-action-item :options="options" @click="handleSwipe">
    <view class="task-card">任务内容</view>
  </uni-swipe-action-item>
</uni-swipe-action>
```

---

## 用户体验优化

### 1. 动画效果
- 任务卡片进入动画（淡入+上滑）
- 倒计时数字跳动动画
- 完成任务的庆祝动画

### 2. 震动反馈
- 开始专注：短震动
- 暂停/继续：短震动
- 完成任务：长震动

### 3. 手势交互
- 左滑删除任务
- 长按显示菜单
- 下拉刷新任务列表

---

## 验收标准

### 功能验收
- [ ] 专注计时器能正常启动、暂停、停止
- [ ] 任务卡片显示彩色渐变背景
- [ ] 统计页面显示图表（日/周/月）
- [ ] 倒计时目标显示背景图和进度
- [ ] 激励语句随机显示
- [ ] 待办任务列表显示正常
- [ ] 左滑删除功能正常
- [ ] 待办单管理功能正常（可选）

### 性能验收
- [ ] 倒计时精确度误差 < 1秒
- [ ] 图表加载时间 < 2秒
- [ ] 页面切换流畅，无卡顿

### 用户体验验收
- [ ] 界面美观，色彩搭配合理
- [ ] 操作流畅，反馈及时
- [ ] 数据统计直观易懂

---

## 下一步行动

1. **确认升级方案**：用户确认是否按此方案升级
2. **开发专注计时器**：优先实现核心功能
3. **美化任务卡片**：提升视觉效果
4. **实现数据图表**：增强数据可视化
5. **测试和优化**：确保功能稳定

---

**是否开始升级开发？请确认！**
