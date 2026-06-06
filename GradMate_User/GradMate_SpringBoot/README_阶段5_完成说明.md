# 阶段5：个人中心模块 - 完成说明

## 完成时间
2026-02-14

---

## 完成内容

### 数据库 ✅
1. 创建`user_favorite`表 - 用户收藏表
2. 创建`user_history`表 - 用户浏览历史表
3. 更新`user`表 - 添加`target_school_id`和`target_major_id`字段

### 后端开发 ✅

#### 实体类
- `UserFavorite.java` - 收藏实体类
- `UserHistory.java` - 浏览历史实体类
- `User.java` - 更新,添加目标院校/专业ID字段

#### Mapper接口
- `UserFavoriteMapper.java` - 收藏Mapper
- `UserHistoryMapper.java` - 浏览历史Mapper

#### Service层
- `FavoriteService.java` - 收藏服务
  - addFavorite() - 添加收藏
  - removeFavorite() - 取消收藏
  - listFavorites() - 收藏列表(分页)
  - isFavorited() - 检查是否已收藏
  - getFavoriteCount() - 获取收藏总数
  - getFavoriteCountByType() - 获取指定类型收藏数

- `HistoryService.java` - 浏览历史服务
  - addHistory() - 添加浏览记录
  - listHistory() - 浏览历史列表(分页)
  - clearHistory() - 清空浏览历史
  - deleteHistory() - 删除单条历史
  - getHistoryCount() - 获取历史记录总数

#### Controller层
- `FavoriteController.java` - 收藏控制器
  - POST /api/favorite/add - 添加收藏
  - DELETE /api/favorite/remove - 取消收藏
  - GET /api/favorite/list - 收藏列表
  - GET /api/favorite/check - 检查是否已收藏
  - GET /api/favorite/count - 获取收藏数量统计

- `HistoryController.java` - 浏览历史控制器
  - POST /api/history/add - 添加浏览记录
  - GET /api/history/list - 浏览历史列表
  - DELETE /api/history/clear - 清空浏览历史
  - DELETE /api/history/{id} - 删除单条历史
  - GET /api/history/count - 获取历史记录总数

- `UserController.java` - 更新用户控制器
  - PUT /api/user/target - 更新目标设定
  - GET /api/user/target/{userId} - 获取目标设定

### 前端开发 ✅

#### 页面更新
- `pages/profile/profile.vue` - 个人中心主页
  - 添加收藏数量显示
  - 添加收藏、历史、学习统计入口
  - 优化UI布局

#### 新增页面
- `pages/profile/favorite.vue` - 收藏列表页面
  - Tab切换(院校/专业/资讯)
  - 收藏列表展示
  - 取消收藏功能
  - 点击跳转详情

- `pages/profile/history.vue` - 浏览历史页面
  - Tab切换(全部/院校/专业/资讯)
  - 历史记录列表
  - 清空历史功能
  - 删除单条记录
  - 时间格式化显示

---

## 功能特点

### 1. 收藏功能
- 支持收藏院校、专业、资讯
- 防重复收藏(数据库唯一索引)
- 分类统计收藏数量
- 分页加载收藏列表

### 2. 浏览历史
- 自动记录浏览行为
- 按时间倒序排列
- 支持清空和删除
- 智能时间显示(刚刚/分钟前/小时前/日期)

### 3. 目标设定
- 支持设定目标院校和专业
- 存储院校/专业ID和名称
- 为AI推荐提供数据基础

---

## API接口列表

### 收藏相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/favorite/add | POST | 添加收藏 |
| /api/favorite/remove | DELETE | 取消收藏 |
| /api/favorite/list | GET | 收藏列表 |
| /api/favorite/check | GET | 检查是否已收藏 |
| /api/favorite/count | GET | 收藏数量统计 |

### 浏览历史相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/history/add | POST | 添加浏览记录 |
| /api/history/list | GET | 浏览历史列表 |
| /api/history/clear | DELETE | 清空浏览历史 |
| /api/history/{id} | DELETE | 删除单条历史 |
| /api/history/count | GET | 历史记录总数 |

### 用户目标相关
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/user/target | PUT | 更新目标设定 |
| /api/user/target/{userId} | GET | 获取目标设定 |

---

## 数据库表结构

### user_favorite表
```sql
CREATE TABLE `user_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `target_id` BIGINT NOT NULL,
  `target_name` VARCHAR(200),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_type_target` (`user_id`, `type`, `target_id`)
);
```

### user_history表
```sql
CREATE TABLE `user_history` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `target_id` BIGINT NOT NULL,
  `target_name` VARCHAR(200),
  `view_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_view_time` (`view_time`)
);
```

---

## 技术要点

### 1. 数据冗余设计
- `target_name`字段冗余存储,避免关联查询
- 提升列表查询性能

### 2. 唯一索引约束
- `user_favorite`表使用唯一索引防止重复收藏
- 组合索引: (user_id, type, target_id)

### 3. 分页查询
- 使用MyBatis Plus的Page对象
- 前端支持上拉加载更多

### 4. 时间格式化
- 浏览历史智能显示时间
- 刚刚/分钟前/小时前/日期

---

## 测试建议

### 功能测试
1. 测试添加收藏功能
2. 测试取消收藏功能
3. 测试收藏列表分页
4. 测试浏览历史记录
5. 测试清空历史功能
6. 测试目标设定功能

### 数据验证
```bash
# 查看收藏数据
mysql -u root -pA123456 -e "USE gradmate; SELECT * FROM user_favorite LIMIT 10;"

# 查看浏览历史
mysql -u root -pA123456 -e "USE gradmate; SELECT * FROM user_history ORDER BY view_time DESC LIMIT 10;"

# 查看用户目标设定
mysql -u root -pA123456 -e "USE gradmate; SELECT id, nickname, target_school, target_major FROM user LIMIT 10;"
```

---

## 待完善功能

### 详情页面集成
需要在以下页面添加收藏按钮和浏览记录:
- [ ] `pages/school/detail.vue` - 院校详情页
- [ ] `pages/major/detail.vue` - 专业详情页
- [ ] `pages/info/index.vue` - 资讯列表页

### 目标设定优化
- [ ] 添加院校/专业搜索选择器
- [ ] 支持快速选择(从收藏中选择)
- [ ] 目标变更历史记录

---

## 文件清单

### 后端文件(8个)
- `entity/UserFavorite.java`
- `entity/UserHistory.java`
- `entity/User.java` (更新)
- `mapper/UserFavoriteMapper.java`
- `mapper/UserHistoryMapper.java`
- `service/FavoriteService.java`
- `service/HistoryService.java`
- `controller/FavoriteController.java`
- `controller/HistoryController.java`
- `controller/UserController.java` (更新)

### 前端文件(3个)
- `pages/profile/profile.vue` (更新)
- `pages/profile/favorite.vue` (新增)
- `pages/profile/history.vue` (新增)
- `pages.json` (更新)

---

## 下一步计划

完成阶段5后,将进入:
- **阶段6**: AI智能模块(推荐/问答)
  - 集成大模型API
  - 实现智能推荐算法
  - 开发AI问答功能

---

**阶段5开发完成!** ✅

个人中心模块已实现收藏、浏览历史、目标设定等核心功能,为后续的AI推荐功能提供了数据基础。
