# GradMate 项目模块化开发技能规划（持续更新）

> 最后更新时间：2026-02-13  
> 开发原则：模块化推进，前后端联调，逐个击破

---

## 项目目标

打造一个面向考研学生的智能信息聚合与辅助决策平台，解决考研信息不对称和碎片化问题。

**核心价值**：
- 精准推荐：基于用户目标院校/专业，智能推送相关资讯
- 信息聚合：整合32万+专业数据、875所院校信息、招生简章、调剂公告
- AI辅助：提供择校建议、考研问答、学习计划管理
- 便捷体验：微信小程序即开即用，Web后台高效管理

---

## 技术栈

### 后端
- **框架**：Spring Boot 3.x
- **ORM**：MyBatis Plus（简化CRUD）
- **数据库**：MySQL 8.0
- **Java版本**：JDK 17
- **API设计**：RESTful风格
- **工具类**：Hutool（推荐）

### 前端（小程序端）
- **框架**：Uniapp + Vue3
- **UI组件**：uni-ui / uView
- **状态管理**：Pinia（可选）
- **图表**：uCharts（分数线趋势图）

### 前端（管理端）
- **框架**：Vue3 + Vite
- **UI组件**：Element Plus
- **路由**：Vue Router
- **HTTP客户端**：Axios

### AI集成
- **推荐方案**：DeepSeek API / 文心一言 / 通义千问
- **调用方式**：RestTemplate / OkHttp

---

## 开发原则

1. **模块化开发**：每个功能模块独立开发，前后端同步推进
2. **联调优先**：每完成一个模块立即联调测试，确保可用后再进入下一模块
3. **数据驱动**：充分利用现有32万+专业数据，避免重复造轮子
4. **渐进增强**：先实现核心功能（查询、展示），再添加高级功能（AI、推荐）
5. **代码规范**：统一命名规范（驼峰、RESTful路径）、注释清晰

---

## 整体开发流程（7大阶段）

```
阶段0：环境搭建与数据库验证
  ↓
阶段1：用户认证模块（登录/注册）
  ↓
阶段2：核心查询模块（院校/专业检索）
  ↓
阶段3：资讯管理模块（简章/调剂/通知）
  ↓
阶段4：学习伴侣模块（打卡/计划/倒计时）
  ↓
阶段5：个人中心模块（收藏/目标设定）
  ↓
阶段6：AI智能模块（推荐/问答）
  ↓
阶段7：管理后台模块（数据维护）
```

---

## 阶段0：环境搭建与数据库验证 ✅

**优先级**：🔴 P0（必须最先完成）

### 目标
确保开发环境就绪，数据库连接正常，项目骨架可运行。

### 后端任务
- [x] 创建Spring Boot项目（已完成）
- [ ] 配置`application.properties`（数据库连接、端口、MyBatis Plus）
- [ ] 引入依赖：MyBatis Plus、Lombok、Hutool、MySQL驱动
- [ ] 测试数据库连接（编写简单的测试类查询`school`表）
- [ ] 配置跨域（CORS）：允许前端调用

### 前端任务
- [x] 创建Uniapp项目（已完成）
- [ ] 配置`manifest.json`（AppID、权限）
- [ ] 配置`pages.json`（页面路由、tabBar）
- [ ] 封装HTTP请求工具（统一baseURL、请求拦截器）

### 验证标准
- 后端启动无报错，访问`http://localhost:8080`返回默认页面
- 前端能成功调用后端测试接口（如`/api/test`）

---

## 阶段1：用户认证模块（登录/注册）

**优先级**：🔴 P0（核心基础功能）

### 目标
实现微信小程序一键登录，建立用户体系，为后续个性化功能打基础。

### 数据库设计
**新增表**：`user`
```sql
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `openid` VARCHAR(100) UNIQUE COMMENT '微信openid',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `phone` VARCHAR(20) COMMENT '手机号（可选）',
  `target_school` VARCHAR(100) COMMENT '目标院校',
  `target_major` VARCHAR(100) COMMENT '目标专业',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 后端任务
- [ ] 创建`User`实体类（对应user表）
- [ ] 创建`UserMapper`接口（继承BaseMapper）
- [ ] 创建`UserService`（登录逻辑：根据openid查询或新建用户）
- [ ] 创建`UserController`：
  - `POST /api/user/login`：接收微信code，调用微信API获取openid，返回用户信息
  - `GET /api/user/info/{id}`：获取用户详情
  - `PUT /api/user/update`：更新用户信息（昵称、目标院校等）

### 前端任务
- [ ] 创建登录页面`pages/login/login.vue`
- [ ] 调用`uni.login()`获取code
- [ ] 调用后端`/api/user/login`接口，存储用户信息到本地（`uni.setStorageSync`）
- [ ] 创建个人中心页面`pages/profile/profile.vue`（展示用户信息）

### 技术要点
- **微信登录流程**：小程序获取code → 后端用code换openid → 返回用户信息
- **注意**：需要在微信公众平台配置AppID和AppSecret（课设可用测试号）
- **简化方案**：如果微信登录配置复杂，可先用手机号+验证码模拟登录

### 验证标准
- 小程序点击登录按钮，能成功获取用户信息并跳转到首页
- 个人中心能正确显示用户昵称、头像

---

## 阶段2：核心查询模块（院校/专业检索）

**优先级**：🔴 P0（核心业务功能）

### 目标
实现院校库和专业库的多维度检索，这是整个平台的数据核心。

### 后端任务

#### 2.1 院校查询
- [ ] 创建`School`实体类（对应school表）
- [ ] 创建`SchoolCharacter`实体类（对应school_character表）
- [ ] 创建`SchoolService`：
  - `listSchools(province, subordinate, is985, is211)`：多条件筛选
  - `getSchoolDetail(schoolId)`：获取院校详情（含特性标签）
- [ ] 创建`SchoolController`：
  - `GET /api/school/list`：院校列表（支持分页、筛选）
  - `GET /api/school/{id}`：院校详情
  - `GET /api/school/provinces`：获取所有省份列表（去重）

#### 2.2 专业查询
- [ ] 创建`Major`实体类（对应major表）
- [ ] 创建`MajorService`：
  - `searchMajors(schoolName, majorName, province, degreeType)`：多条件搜索
  - `getMajorDetail(majorId)`：专业详情（含考试科目、拟招生人数）
  - `getMajorsBySchool(schoolName)`：查询某院校的所有专业
- [ ] 创建`MajorController`：
  - `GET /api/major/search`：专业搜索（支持模糊查询、分页）
  - `GET /api/major/{id}`：专业详情
  - `GET /api/major/school/{schoolName}`：按院校查专业

### 前端任务

#### 2.1 院校库页面
- [ ] 创建`pages/school/list.vue`：
  - 顶部搜索框（院校名称）
  - 筛选器（省份、985/211、主管部门）
  - 院校列表（卡片式展示：logo、名称、位置、标签）
- [ ] 创建`pages/school/detail.vue`：
  - 院校基本信息
  - 特性标签（985/211/双一流）
  - 相关专业列表（跳转到专业详情）

#### 2.2 专业库页面
- [ ] 创建`pages/major/search.vue`：
  - 搜索框（支持院校名、专业名）
  - 筛选器（学位类型、学习方式、考试方式）
  - 搜索结果列表
- [ ] 创建`pages/major/detail.vue`：
  - 专业详细信息（代码、院系、研究方向）
  - 考试科目展示
  - 拟招生人数
  - 指导教师（如有）

### 技术要点
- **分页处理**：MyBatis Plus的`Page`对象，前端传`pageNum`和`pageSize`
- **模糊查询**：使用`QueryWrapper.like()`
- **性能优化**：major表数据量大（32万+），需添加索引（已有major_name索引）
- **数据展示**：考试科目可能为空，需做空值处理

### 验证标准
- 能按省份筛选出对应院校
- 搜索"计算机"能返回相关专业列表
- 点击院校能查看详情并跳转到该校专业列表

---

## 阶段3：资讯管理模块（简章/调剂/通知）

**优先级**：🟡 P1（重要功能）

### 目标
展示招生简章、调剂公告、招生通知，支持按院校筛选和时间排序。

### 后端任务

#### 3.1 招生简章
- [ ] 创建`SchoolBrochure`实体类
- [ ] 创建`BrochureService`：
  - `listBrochures(schoolId, pageNum, pageSize)`：分页查询
  - `getBrochureDetail(id)`：详情
- [ ] 创建`BrochureController`：
  - `GET /api/brochure/list`：简章列表
  - `GET /api/brochure/{id}`：简章详情

#### 3.2 调剂公告
- [ ] 创建`SchoolAdjust`实体类
- [ ] 创建`AdjustService`（逻辑同上）
- [ ] 创建`AdjustController`：
  - `GET /api/adjust/list`
  - `GET /api/adjust/{id}`

#### 3.3 招生通知
- [ ] 创建`SchoolInformation`实体类
- [ ] 创建`InformationService`（逻辑同上）
- [ ] 创建`InformationController`：
  - `GET /api/information/list`
  - `GET /api/information/{id}`

### 前端任务
- [ ] 创建资讯首页`pages/info/index.vue`：
  - Tab切换（简章/调剂/通知）
  - 列表展示（标题、发布时间、院校名称）
  - 下拉刷新、上拉加载更多
- [ ] 创建资讯详情页`pages/info/detail.vue`：
  - 展示标题、发布时间
  - 外链跳转按钮（跳转到原始URL）

### 技术要点
- **时间排序**：按`publish_date`降序排列
- **外链处理**：小程序内使用`web-view`组件打开外部链接
- **数据关联**：通过`school_id`关联院校名称

### 验证标准
- 资讯列表能正常加载并分页
- 点击资讯能跳转到详情页或外部链接
- Tab切换流畅，数据正确

---

## 阶段4：学习伴侣模块（打卡/计划/倒计时）

**优先级**：🟡 P1（核心功能，情感陪伴）

### 目标
提供学习计划管理、每日任务清单、打卡日历、倒计时组件，增强用户粘性和学习动力。

### 数据库设计
**新增表**：`study_plan`（学习计划）、`study_record`（打卡记录）、`daily_task`（每日任务）

```sql
-- 学习计划表
CREATE TABLE `study_plan` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '计划标题',
  `content` TEXT COMMENT '计划内容',
  `target_date` DATE COMMENT '目标日期（考研日期）',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0进行中 1已完成 2已放弃',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`)
);

-- 打卡记录表
CREATE TABLE `study_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `check_date` DATE NOT NULL COMMENT '打卡日期',
  `duration` INT DEFAULT 0 COMMENT '学习时长（分钟）',
  `note` VARCHAR(255) COMMENT '打卡备注',
  `mood` TINYINT COMMENT '心情：1-5星',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_date` (`user_id`, `check_date`),
  INDEX `idx_user_id` (`user_id`)
);

-- 每日任务表
CREATE TABLE `daily_task` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `task_content` VARCHAR(200) NOT NULL COMMENT '任务内容',
  `task_date` DATE NOT NULL COMMENT '任务日期',
  `is_completed` TINYINT DEFAULT 0 COMMENT '是否完成：0未完成 1已完成',
  `priority` TINYINT DEFAULT 1 COMMENT '优先级：1低 2中 3高',
  `complete_time` DATETIME COMMENT '完成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_user_date` (`user_id`, `task_date`)
);
```

### 后端任务

#### 4.1 学习计划管理
- [ ] 创建`StudyPlan`实体类
- [ ] 创建`StudyPlanService`：
  - `createPlan(userId, title, content, targetDate)`：创建计划
  - `updatePlan(planId, ...)`：更新计划
  - `deletePlan(planId)`：删除计划
  - `getPlanByUser(userId)`：获取用户的学习计划
  - `calculateDaysLeft(targetDate)`：计算距离考研剩余天数
- [ ] 创建`StudyPlanController`：
  - `POST /api/study/plan`：创建计划
  - `PUT /api/study/plan/{id}`：更新计划
  - `DELETE /api/study/plan/{id}`：删除计划
  - `GET /api/study/plan/user/{userId}`：获取用户计划
  - `GET /api/study/countdown`：获取倒计时天数

#### 4.2 打卡记录管理
- [ ] 创建`StudyRecord`实体类
- [ ] 创建`StudyRecordService`：
  - `checkIn(userId, duration, note, mood)`：今日打卡
  - `getRecordByDate(userId, date)`：查询某日打卡记录
  - `getRecordsByMonth(userId, year, month)`：查询某月打卡记录（日历展示）
  - `getContinuousDays(userId)`：计算连续打卡天数
  - `getTotalDuration(userId)`：计算总学习时长
- [ ] 创建`StudyRecordController`：
  - `POST /api/study/checkin`：打卡
  - `GET /api/study/record/date`：查询某日记录
  - `GET /api/study/record/month`：查询某月记录
  - `GET /api/study/stats`：获取统计数据（连续天数、总时长）

#### 4.3 每日任务管理（CRUD）
- [ ] 创建`DailyTask`实体类
- [ ] 创建`DailyTaskService`：
  - `createTask(userId, content, date, priority)`：创建任务
  - `updateTask(taskId, content, priority)`：更新任务
  - `deleteTask(taskId)`：删除任务
  - `completeTask(taskId)`：标记完成
  - `getTasksByDate(userId, date)`：查询某日任务列表
  - `getTodayTasks(userId)`：获取今日任务
- [ ] 创建`DailyTaskController`：
  - `POST /api/task`：创建任务
  - `PUT /api/task/{id}`：更新任务
  - `DELETE /api/task/{id}`：删除任务
  - `PUT /api/task/{id}/complete`：完成任务
  - `GET /api/task/today`：今日任务列表
  - `GET /api/task/date/{date}`：指定日期任务列表

### 前端任务

#### 4.1 倒计时组件
- [ ] 创建全局倒计时组件`components/Countdown.vue`：
  - 显示"距离2026年考研还有X天"
  - 支持自定义目标日期（从用户学习计划中读取）
  - 动态更新（每日0点刷新）
  - 可在首页、学习伴侣页面复用

#### 4.2 每日任务清单页面
- [ ] 创建任务管理页面`pages/study/task.vue`：
  - 顶部显示今日日期和倒计时
  - 任务列表（支持滑动删除）
  - 任务项：复选框、内容、优先级标签（高/中/低）
  - 底部添加任务按钮（弹出输入框）
  - 完成任务时显示动画效果（✓ 打勾动画）
  - 支持任务编辑（长按弹出菜单）

#### 4.3 打卡日历页面
- [ ] 创建打卡日历页面`pages/study/calendar.vue`：
  - 使用日历组件（推荐uni-calendar或自定义）
  - 已打卡日期显示标记（如绿色圆点）
  - 点击日期查看当日打卡详情（学习时长、备注、心情）
  - 顶部显示统计数据：
    - 连续打卡X天
    - 本月打卡X天
    - 累计学习X小时
  - 今日打卡按钮（弹出打卡表单）

#### 4.4 打卡表单
- [ ] 创建打卡弹窗组件`components/CheckInModal.vue`：
  - 学习时长选择器（滑动选择或输入）
  - 心情选择（1-5星，点击星星）
  - 备注输入框（今日学习内容）
  - 提交按钮（提交后显示激励动画）

#### 4.5 动态激励动画
- [ ] 实现打卡成功动画：
  - 使用Lottie动画或CSS动画
  - 显示鼓励文案（如"太棒了！坚持就是胜利！"）
  - 随机显示不同的激励语（可配置激励语库）
- [ ] 实现任务完成动画：
  - 复选框打勾动画
  - 任务项划掉效果（删除线）
  - 完成所有任务时显示特殊动画（如烟花、奖杯）

#### 4.6 学习伴侣主页
- [ ] 创建学习伴侣主页`pages/study/index.vue`：
  - 顶部倒计时卡片（醒目展示）
  - 今日任务概览（X/Y已完成）
  - 打卡日历入口（显示本月打卡天数）
  - 学习计划入口
  - 学习统计卡片（总时长、连续天数）

### 技术要点
- **倒计时计算**：使用`dayjs`或原生Date计算日期差
- **日历组件**：推荐使用`uni-calendar`或`uView`的日历组件
- **动画实现**：
  - CSS动画：`@keyframes`实现简单动画
  - Lottie动画：使用`lottie-miniprogram`播放JSON动画
  - 交互反馈：`uni.showToast()`、`uni.vibrateShort()`（震动反馈）
- **数据持久化**：任务和打卡数据实时同步到后端
- **离线支持**：任务列表可先存本地，联网后同步（可选）
- **激励语库**：可在前端配置或从后端获取

### 验证标准
- 倒计时准确显示剩余天数
- 能创建、编辑、删除、完成每日任务
- 打卡日历正确显示打卡记录
- 打卡成功后显示激励动画
- 连续打卡天数计算准确
- 任务完成时有明显的视觉反馈

---

## 阶段5：个人中心模块（收藏/目标设定）

**优先级**：🟡 P1（用户体验核心）

### 目标
用户可以设定目标院校/专业，收藏感兴趣的内容，为AI推荐提供数据基础。

### 数据库设计
**新增表**：`user_favorite`（收藏表）
```sql
CREATE TABLE `user_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '收藏类型：school/major/brochure/adjust/info',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_type_target` (`user_id`, `type`, `target_id`)
);
```

### 后端任务
- [ ] 创建`UserFavorite`实体类
- [ ] 创建`FavoriteService`：
  - `addFavorite(userId, type, targetId)`：添加收藏
  - `removeFavorite(userId, type, targetId)`：取消收藏
  - `listFavorites(userId, type)`：查询用户收藏列表
  - `isFavorited(userId, type, targetId)`：判断是否已收藏
- [ ] 创建`FavoriteController`：
  - `POST /api/favorite/add`：添加收藏
  - `DELETE /api/favorite/remove`：取消收藏
  - `GET /api/favorite/list`：收藏列表

### 前端任务
- [ ] 完善个人中心页面`pages/profile/profile.vue`：
  - 用户信息卡片（头像、昵称、目标院校/专业）
  - 功能入口：我的收藏、目标设定、浏览历史
- [ ] 创建目标设定页面`pages/profile/target.vue`：
  - 选择目标院校（搜索选择器）
  - 选择目标专业（搜索选择器）
  - 保存按钮（调用`/api/user/update`）
- [ ] 创建收藏列表页面`pages/profile/favorite.vue`：
  - Tab切换（院校/专业/资讯）
  - 列表展示
  - 取消收藏按钮

### 技术要点
- **收藏状态同步**：在院校/专业详情页显示收藏按钮，实时更新状态
- **数据关联**：收藏列表需关联查询原始数据（如院校名称、专业名称）
- **防重复收藏**：数据库设置唯一索引

### 验证标准
- 能成功设定目标院校和专业
- 收藏功能正常，列表能正确展示
- 取消收藏后状态实时更新

---

## 阶段6：AI智能模块（推荐/问答）

**优先级**：🟢 P2（亮点功能）

### 目标
集成大模型API，提供智能择校建议、考研问答、个性化推荐。

### 后端任务

#### 5.1 AI问答
- [ ] 创建`AiService`：
  - `chat(userId, question)`：调用大模型API（DeepSeek/文心一言）
  - 实现流式输出（SSE，可选）
- [ ] 创建`AiController`：
  - `POST /api/ai/chat`：AI问答接口
- [ ] 配置API Key（在`application.properties`中）

#### 5.2 智能推荐
- [ ] 创建`RecommendService`：
  - `recommendSchools(userId)`：基于用户目标专业推荐院校
  - `recommendMajors(userId)`：基于用户目标院校推荐专业
  - `recommendInfo(userId)`：推荐相关资讯
- [ ] 创建`RecommendController`：
  - `GET /api/recommend/schools`：院校推荐
  - `GET /api/recommend/majors`：专业推荐
  - `GET /api/recommend/info`：资讯推荐

### 前端任务
- [ ] 创建AI助手页面`pages/ai/chat.vue`：
  - 聊天界面（消息列表、输入框）
  - 支持打字机效果（流式输出）
  - 预设问题快捷按钮（如"如何选择院校"）
- [ ] 在首页添加推荐模块：
  - "为你推荐"院校卡片
  - "相关资讯"列表

### 技术要点
- **API选择**：推荐DeepSeek（性价比高）或文心一言（中文友好）
- **Prompt工程**：设计系统提示词，限定回答范围为考研相关
- **推荐算法**：
  - 简单版：基于用户目标院校/专业，SQL查询相关数据
  - 进阶版：结合历史浏览记录、收藏记录，计算相似度
- **成本控制**：设置单次对话token上限，避免超额消费

### 验证标准
- AI能正确回答考研相关问题
- 推荐的院校/专业与用户目标相关
- 响应时间在3秒内

---

## 阶段7：管理后台模块（数据维护）

**优先级**：🟢 P2（演示加分项）

### 目标
提供Web管理端，方便管理员维护数据、发布资讯、查看统计。

### 后端任务
- [ ] 创建管理员表`admin`（id、username、password）
- [ ] 实现管理员登录接口（简单的用户名密码验证）
- [ ] 创建管理接口：
  - `GET /api/admin/stats`：统计数据（用户数、院校数、专业数）
  - `POST /api/admin/brochure`：发布简章
  - `PUT /api/admin/brochure/{id}`：编辑简章
  - `DELETE /api/admin/brochure/{id}`：删除简章
  - （调剂、通知同理）

### 前端任务（Vue3 + Element Plus）
- [ ] 创建管理后台项目（Vite + Vue3）
- [ ] 实现登录页面
- [ ] 实现Dashboard（数据统计图表）
- [ ] 实现资讯管理页面（表格+CRUD）
- [ ] 实现用户管理页面（列表+禁用功能）

### 技术要点
- **权限控制**：简单的token验证即可（课设不需要复杂的RBAC）
- **富文本编辑器**：使用`wangEditor`或`Quill`
- **图表展示**：使用`ECharts`

### 验证标准
- 管理员能登录后台
- 能成功发布/编辑/删除资讯
- Dashboard能展示统计数据

---



## 阶段8：高级功能（可选扩展）

**优先级**：🔵 P3（时间充裕再做）

### 8.1 分数线趋势图
- 爬取或手动录入历年分数线数据
- 使用ECharts/uCharts绘制折线图

### 8.2 AI生成文书
- 调用大模型生成个人陈述、研究计划
- 提供模板和修改建议

### 8.3 社区论坛
- 用户发帖、评论、点赞
- 需考虑内容审核风险（课设可简化）

### 8.4 消息推送
- 关键时间节点提醒（报名、确认、考试）
- 微信小程序订阅消息

---

## 开发检查清单（Checklist）

### 代码规范
- [ ] 统一使用驼峰命名（Java类、方法、变量）
- [ ] RESTful API路径规范（/api/{resource}/{action}）
- [ ] 所有实体类添加注释
- [ ] Controller方法添加@ApiOperation注解（如使用Swagger）

### 测试验证
- [ ] 每个接口用Postman/Apifox测试通过
- [ ] 前端每个页面在微信开发者工具中测试通过
- [ ] 数据库查询性能测试（major表大数据量查询）

### 文档输出
- [ ] 接口文档（推荐使用Swagger自动生成）
- [ ] 数据库设计文档（ER图）
- [ ] 部署文档（如何启动项目）
- [ ] 用户操作手册（演示PPT）

---

## 技术难点与解决方案

### 1. 大数据量查询优化（major表32万+）
**问题**：全表扫描导致查询慢  
**解决**：
- 添加索引（major_name、school_name、province）
- 使用分页查询，限制每页数量
- 前端添加加载动画，提升体验

### 2. 微信登录配置复杂
**问题**：需要企业资质或测试号  
**解决**：
- 课设阶段可用手机号+验证码模拟登录
- 或使用微信开放平台测试号

### 3. AI接口调用成本
**问题**：频繁调用可能产生费用  
**解决**：
- 设置单用户每日调用次数限制
- 使用免费额度的API（DeepSeek有免费额度）
- 缓存常见问题的回答

### 4. 跨域问题
**问题**：前端调用后端接口被浏览器拦截  
**解决**：
- 后端添加CORS配置类
- 或使用Nginx反向代理

---

## 时间规划建议（总计约4-6周）

| 阶段 | 预计耗时 | 备注 |
|------|---------|------|
| 阶段0：环境搭建 | 1-2天 | 确保基础设施就绪 |
| 阶段1：用户认证 | 2-3天 | 可简化微信登录 |
| 阶段2：核心查询 | 5-7天 | 最复杂，需仔细测试 |
| 阶段3：资讯管理 | 3-4天 | 逻辑相似，可复用代码 |
| 阶段4：学习伴侣 | 4-5天 | 包含动画和日历组件 |
| 阶段5：个人中心 | 3-4天 | 依赖前面模块 |
| 阶段6：AI智能 | 4-5天 | 需调试API调用 |
| 阶段7：管理后台 | 3-5天 | 可选，时间紧可省略 |
| 阶段8：扩展功能 | 按需 | 时间充裕再做 |

---

## 常用命令速查

### 后端启动
```bash
# IDEA中直接运行GradMateSpringBootApplication
# 或命令行
mvn spring-boot:run
```

### 前端启动（小程序）
```bash
# 微信开发者工具中导入项目
# 或使用HBuilderX运行
```

### 数据库连接测试
```bash
mysql -u root -p
use gradmate;
show tables;
select count(*) from major;
```

---

## 参考资源

- [MyBatis Plus官方文档](https://baomidou.com/)
- [Uniapp官方文档](https://uniapp.dcloud.net.cn/)
- [Element Plus组件库](https://element-plus.org/)
- [DeepSeek API文档](https://platform.deepseek.com/docs)
- [微信小程序开发文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)

---

## 更新日志

- **2026-02-13**：初始版本，规划6大核心阶段
- **待更新**：根据实际开发进度调整优先级

---

**开发建议**：不要追求一次性完美，先把核心流程跑通（阶段0-5），再逐步优化和添加高级功能。每完成一个模块就提交代码，养成良好的版本管理习惯。学习伴侣模块的动画效果可以先用简单的CSS实现，后期再优化为Lottie动画。

**遇到问题时**：先查日志、打断点调试，再搜索文档，最后再问AI助手。自己解决问题的过程也是学习的过程。

祝开发顺利！🚀
