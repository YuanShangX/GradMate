# 个人中心模块完善方案

> 生成时间: 2026-02-20  
> 目标: 根据数据库表结构完善个人中心模块功能

---

## 📊 当前状态分析

### 已完成功能
✅ 用户信息展示(昵称、头像、手机号)  
✅ 目标设定(院校、专业)  
✅ 收藏功能(院校、专业、资讯)  
✅ 浏览历史记录  
✅ 用户信息编辑  
✅ 退出登录  

### 数据库表结构
- **user** - 用户基本信息(id, username, password, openid, nickname, avatar, phone, target_school_id, target_major_id)
- **user_favorite** - 收藏记录(id, user_id, type, target_id, target_name, create_time)
- **user_history** - 浏览历史(id, user_id, type, target_id, target_name, view_time)

---

## 🎯 完善目标

### 1. 增强用户统计数据展示
- 学习统计(总学习时长、连续打卡天数)
- 收藏统计(院校、专业、资讯分类统计)
- 浏览统计(今日浏览、本周浏览)

### 2. 优化目标设定功能
- 显示目标院校的详细信息(985/211/双一流标签)
- 显示目标专业的详细信息(学位类型、拟招生人数)
- 快速跳转到目标院校/专业详情页

### 3. 完善收藏和历史功能
- 收藏列表显示更多详细信息
- 历史记录支持搜索和筛选
- 批量删除功能

### 4. 新增个人成就系统
- 学习天数徽章
- 收藏数量徽章
- 任务完成徽章

---

## 🔧 具体实现方案

### 方案1: 增强个人中心首页统计数据

#### 后端改造

**新增接口: `/api/user/stats/{userId}`**

```java
/**
 * 获取用户统计数据
 */
@GetMapping("/stats/{userId}")
public Result<Map<String, Object>> getUserStats(@PathVariable Long userId) {
    try {
        Map<String, Object> stats = new HashMap<>();
        
        // 1. 学习统计
        StudyStatsResponse studyStats = studyService.getStudyStats(userId);
        stats.put("totalStudyDuration", studyStats.getTotalDuration());
        stats.put("continuousDays", studyStats.getContinuousDays());
        stats.put("totalCheckInDays", studyStats.getTotalCheckInDays());
        
        // 2. 收藏统计
        long totalFavorites = favoriteService.getFavoriteCount(userId);
        long schoolFavorites = favoriteService.getFavoriteCountByType(userId, "school");
        long majorFavorites = favoriteService.getFavoriteCountByType(userId, "major");
        long infoFavorites = favoriteService.getFavoriteCountByType(userId, "brochure") +
                            favoriteService.getFavoriteCountByType(userId, "adjust") +
                            favoriteService.getFavoriteCountByType(userId, "information");
        
        stats.put("totalFavorites", totalFavorites);
        stats.put("schoolFavorites", schoolFavorites);
        stats.put("majorFavorites", majorFavorites);
        stats.put("infoFavorites", infoFavorites);
        
        // 3. 浏览统计
        long totalHistory = historyService.getHistoryCount(userId);
        stats.put("totalHistory", totalHistory);
        
        // 4. 任务统计
        long totalTasks = studyService.getTotalTaskCount(userId);
        long completedTasks = studyService.getCompletedTaskCount(userId);
        stats.put("totalTasks", totalTasks);
        stats.put("completedTasks", completedTasks);
        
        return Result.success(stats);
    } catch (Exception e) {
        return Result.error("获取统计数据失败: " + e.getMessage());
    }
}
```

#### 前端改造

**优化 `profile.vue` 页面**

```vue
<!-- 新增统计卡片 -->
<view class="stats-section">
    <view class="section-title">我的数据</view>
    <view class="stats-grid">
        <view class="stat-item">
            <text class="stat-value">{{ stats.totalStudyDuration || 0 }}</text>
            <text class="stat-label">学习时长(分钟)</text>
        </view>
        <view class="stat-item">
            <text class="stat-value">{{ stats.continuousDays || 0 }}</text>
            <text class="stat-label">连续打卡(天)</text>
        </view>
        <view class="stat-item">
            <text class="stat-value">{{ stats.totalFavorites || 0 }}</text>
            <text class="stat-label">收藏数量</text>
        </view>
        <view class="stat-item">
            <text class="stat-value">{{ stats.completedTasks || 0 }}</text>
            <text class="stat-label">完成任务</text>
        </view>
    </view>
</view>
```

---

### 方案2: 优化目标设定显示

#### 后端改造

**新增接口: `/api/user/target/detail/{userId}`**

```java
/**
 * 获取目标详细信息
 */
@GetMapping("/target/detail/{userId}")
public Result<Map<String, Object>> getTargetDetail(@PathVariable Long userId) {
    try {
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 获取目标院校详细信息
        if (user.getTargetSchoolId() != null) {
            School school = schoolService.getSchoolById(user.getTargetSchoolId());
            if (school != null) {
                Map<String, Object> schoolInfo = new HashMap<>();
                schoolInfo.put("id", school.getId());
                schoolInfo.put("name", school.getSchoolName());
                schoolInfo.put("location", school.getLocation());
                schoolInfo.put("subordinate", school.getSubordinate());
                
                // 获取院校特性
                SchoolCharacter character = schoolService.getSchoolCharacter(school.getId());
                if (character != null) {
                    schoolInfo.put("is985", character.getSyl() == 1);
                    schoolInfo.put("is211", character.getYjsy() == 1);
                    schoolInfo.put("isDoubleFirstClass", character.getZhx() == 1);
                }
                
                result.put("targetSchool", schoolInfo);
            }
        }
        
        // 获取目标专业详细信息
        if (user.getTargetMajorId() != null) {
            Major major = majorService.getMajorById(user.getTargetMajorId());
            if (major != null) {
                Map<String, Object> majorInfo = new HashMap<>();
                majorInfo.put("id", major.getId());
                majorInfo.put("name", major.getMajorName());
                majorInfo.put("code", major.getMajorCode());
                majorInfo.put("degreeType", major.getDegreeType());
                majorInfo.put("studyMode", major.getStudyMode());
                majorInfo.put("plannedEnrollment", major.getPlannedEnrollment());
                
                result.put("targetMajor", majorInfo);
            }
        }
        
        return Result.success(result);
    } catch (Exception e) {
        return Result.error("获取目标详情失败: " + e.getMessage());
    }
}
```

#### 前端改造

**优化目标卡片显示**

```vue
<view class="target-card-enhanced">
    <!-- 目标院校 -->
    <view v-if="targetDetail.targetSchool" class="target-school">
        <view class="target-header">
            <text class="target-label">目标院校</text>
            <view class="target-tags">
                <text v-if="targetDetail.targetSchool.is985" class="tag tag-985">985</text>
                <text v-if="targetDetail.targetSchool.is211" class="tag tag-211">211</text>
                <text v-if="targetDetail.targetSchool.isDoubleFirstClass" class="tag tag-double">双一流</text>
            </view>
        </view>
        <text class="target-name">{{ targetDetail.targetSchool.name }}</text>
        <text class="target-location">📍 {{ targetDetail.targetSchool.location }}</text>
        <button class="detail-btn" @click="goToSchoolDetail">查看详情</button>
    </view>
    
    <!-- 目标专业 -->
    <view v-if="targetDetail.targetMajor" class="target-major">
        <text class="target-label">目标专业</text>
        <text class="target-name">{{ targetDetail.targetMajor.name }}</text>
        <view class="major-info">
            <text class="info-item">代码: {{ targetDetail.targetMajor.code }}</text>
            <text class="info-item">类型: {{ targetDetail.targetMajor.degreeType }}</text>
            <text class="info-item">招生: {{ targetDetail.targetMajor.plannedEnrollment }}人</text>
        </view>
        <button class="detail-btn" @click="goToMajorDetail">查看详情</button>
    </view>
    
    <button class="set-target-btn" @click="goToTargetPage">重新设置目标</button>
</view>
```

---

### 方案3: 完善收藏列表功能

#### 后端改造

**优化 FavoriteService - 返回详细信息**

```java
/**
 * 获取收藏列表(带详细信息)
 */
public Page<Map<String, Object>> listFavoritesWithDetail(Long userId, String type, int pageNum, int pageSize) {
    Page<UserFavorite> page = new Page<>(pageNum, pageSize);
    QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
    wrapper.eq("user_id", userId);
    
    if (type != null && !type.isEmpty()) {
        wrapper.eq("type", type);
    }
    
    wrapper.orderByDesc("create_time");
    Page<UserFavorite> favoritePage = favoriteMapper.selectPage(page, wrapper);
    
    // 转换为详细信息
    Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize);
    resultPage.setTotal(favoritePage.getTotal());
    
    List<Map<String, Object>> detailList = new ArrayList<>();
    for (UserFavorite favorite : favoritePage.getRecords()) {
        Map<String, Object> detail = new HashMap<>();
        detail.put("id", favorite.getId());
        detail.put("type", favorite.getType());
        detail.put("targetId", favorite.getTargetId());
        detail.put("targetName", favorite.getTargetName());
        detail.put("createTime", favorite.getCreateTime());
        
        // 根据类型获取额外信息
        if ("school".equals(favorite.getType())) {
            School school = schoolMapper.selectById(favorite.getTargetId());
            if (school != null) {
                detail.put("location", school.getLocation());
                detail.put("subordinate", school.getSubordinate());
            }
        } else if ("major".equals(favorite.getType())) {
            Major major = majorMapper.selectById(favorite.getTargetId());
            if (major != null) {
                detail.put("schoolName", major.getSchoolName());
                detail.put("majorCode", major.getMajorCode());
                detail.put("degreeType", major.getDegreeType());
            }
        }
        
        detailList.add(detail);
    }
    
    resultPage.setRecords(detailList);
    return resultPage;
}
```

#### 前端改造

**优化收藏列表显示**

```vue
<view class="list-item-enhanced">
    <view class="item-main">
        <text class="item-title">{{ item.targetName }}</text>
        
        <!-- 院校额外信息 -->
        <view v-if="item.type === 'school'" class="item-extra">
            <text class="extra-text">📍 {{ item.location }}</text>
            <text class="extra-text">🏛️ {{ item.subordinate }}</text>
        </view>
        
        <!-- 专业额外信息 -->
        <view v-if="item.type === 'major'" class="item-extra">
            <text class="extra-text">🏫 {{ item.schoolName }}</text>
            <text class="extra-text">📚 {{ item.degreeType }}</text>
        </view>
        
        <text class="item-time">{{ formatTime(item.createTime) }}</text>
    </view>
    
    <view class="item-actions">
        <view class="action-btn" @click.stop="goToDetail(item)">
            <text class="action-icon">👁️</text>
        </view>
        <view class="action-btn delete" @click.stop="removeFavorite(item)">
            <text class="action-icon">🗑️</text>
        </view>
    </view>
</view>
```

---

### 方案4: 新增个人成就系统

#### 数据库设计

**新增表: user_achievement**

```sql
CREATE TABLE `user_achievement` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `achievement_type` VARCHAR(50) NOT NULL COMMENT '成就类型',
  `achievement_level` INT DEFAULT 1 COMMENT '成就等级',
  `unlock_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户成就表';
```

#### 成就类型定义

```java
public enum AchievementType {
    STUDY_DAYS_7("连续学习7天", 7),
    STUDY_DAYS_30("连续学习30天", 30),
    STUDY_DAYS_100("连续学习100天", 100),
    
    FAVORITE_10("收藏达人", 10),
    FAVORITE_50("收藏专家", 50),
    FAVORITE_100("收藏大师", 100),
    
    TASK_10("任务新手", 10),
    TASK_50("任务达人", 50),
    TASK_100("任务大师", 100);
    
    private String name;
    private int threshold;
    
    AchievementType(String name, int threshold) {
        this.name = name;
        this.threshold = threshold;
    }
}
```

#### 后端实现

**AchievementService.java**

```java
@Service
public class AchievementService {
    
    @Autowired
    private UserAchievementMapper achievementMapper;
    
    @Autowired
    private StudyService studyService;
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 检查并解锁成就
     */
    public void checkAndUnlockAchievements(Long userId) {
        // 检查学习天数成就
        StudyStatsResponse stats = studyService.getStudyStats(userId);
        checkStudyAchievement(userId, stats.getContinuousDays());
        
        // 检查收藏数量成就
        long favoriteCount = favoriteService.getFavoriteCount(userId);
        checkFavoriteAchievement(userId, favoriteCount);
        
        // 检查任务完成成就
        long completedTasks = studyService.getCompletedTaskCount(userId);
        checkTaskAchievement(userId, completedTasks);
    }
    
    /**
     * 获取用户成就列表
     */
    public List<UserAchievement> getUserAchievements(Long userId) {
        QueryWrapper<UserAchievement> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("unlock_time");
        
        return achievementMapper.selectList(wrapper);
    }
}
```

#### 前端实现

**新增成就展示页面**

```vue
<view class="achievement-section">
    <view class="section-title">我的成就</view>
    <view class="achievement-grid">
        <view 
            v-for="achievement in achievements" 
            :key="achievement.id"
            class="achievement-item"
        >
            <view class="achievement-icon">🏆</view>
            <text class="achievement-name">{{ achievement.achievementType }}</text>
            <text class="achievement-time">{{ formatTime(achievement.unlockTime) }}</text>
        </view>
    </view>
</view>
```

---

## 📝 实施步骤

### 第一阶段: 基础完善(1-2天)
1. ✅ 新增用户统计接口 `/api/user/stats/{userId}`
2. ✅ 优化个人中心首页,显示统计数据
3. ✅ 新增目标详情接口 `/api/user/target/detail/{userId}`
4. ✅ 优化目标卡片显示

### 第二阶段: 功能增强(2-3天)
1. ✅ 优化收藏列表,返回详细信息
2. ✅ 优化历史记录,支持搜索筛选
3. ✅ 新增批量删除功能
4. ✅ 优化前端UI展示

### 第三阶段: 成就系统(3-4天)
1. ⚠️ 创建成就表和实体类
2. ⚠️ 实现成就检查和解锁逻辑
3. ⚠️ 开发成就展示页面
4. ⚠️ 集成到个人中心

---

## 🎨 UI优化建议

### 1. 统计卡片设计
- 使用渐变色背景
- 数字大字体显示
- 添加图标装饰
- 卡片阴影效果

### 2. 目标卡片设计
- 院校和专业分开显示
- 添加标签(985/211/双一流)
- 快速操作按钮
- 卡片可点击跳转

### 3. 列表优化
- 增加缩略图
- 显示更多元信息
- 滑动操作(删除/分享)
- 下拉刷新/上拉加载

---

## 🔍 测试清单

### 功能测试
- [ ] 统计数据准确性
- [ ] 目标详情显示正确
- [ ] 收藏列表详细信息完整
- [ ] 历史记录搜索功能
- [ ] 成就解锁逻辑正确

### 性能测试
- [ ] 大数据量下的加载速度
- [ ] 分页加载流畅性
- [ ] 接口响应时间<500ms

### 兼容性测试
- [ ] 微信小程序兼容性
- [ ] 不同屏幕尺寸适配
- [ ] 数据为空时的显示

---

## 📊 预期效果

### 用户体验提升
- 数据可视化,一目了然
- 目标信息详细,便于决策
- 收藏管理更便捷
- 成就系统增加粘性

### 数据价值提升
- 用户行为数据更完整
- 为AI推荐提供数据基础
- 用户画像更精准

---

## 🚀 后续扩展方向

1. **社交功能**: 分享成就到朋友圈
2. **数据分析**: 学习报告生成
3. **智能推荐**: 基于收藏和浏览推荐相关内容
4. **目标管理**: 学习计划与目标关联
5. **消息通知**: 目标院校资讯推送

---

**完成时间预估**: 5-7个工作日  
**优先级**: P1(高优先级)  
**依赖模块**: 学习模块、收藏模块、历史模块
