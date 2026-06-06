# 阶段5：个人中心模块 - 开发规划

## 开发时间
2026-02-14

---

## 模块目标

实现用户个人中心功能,包括:
- 目标院校/专业设定
- 收藏功能(院校/专业/资讯)
- 浏览历史记录
- 个人信息管理

为后续的AI推荐功能提供数据基础。

---

## 数据库设计

### 1. user_favorite 表(收藏表)

```sql
CREATE TABLE `user_favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '收藏类型：school/major/brochure/adjust/information',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_name` VARCHAR(200) COMMENT '目标名称(冗余字段,方便查询)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_user_type_target` (`user_id`, `type`, `target_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_type` (`type`)
) COMMENT='用户收藏表';
```

### 2. user_history 表(浏览历史表)

```sql
CREATE TABLE `user_history` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `type` VARCHAR(20) NOT NULL COMMENT '浏览类型：school/major/brochure/adjust/information',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `target_name` VARCHAR(200) COMMENT '目标名称',
  `view_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_view_time` (`view_time`)
) COMMENT='用户浏览历史表';
```

### 3. user 表更新(已存在,需确认字段)

确认user表包含以下字段:
- `target_school` VARCHAR(100) - 目标院校
- `target_major` VARCHAR(100) - 目标专业
- `target_school_id` BIGINT - 目标院校ID(新增)
- `target_major_id` BIGINT - 目标专业ID(新增)

```sql
-- 如果字段不存在,执行以下SQL
ALTER TABLE `user`
ADD COLUMN `target_school_id` BIGINT COMMENT '目标院校ID',
ADD COLUMN `target_major_id` BIGINT COMMENT '目标专业ID';
```

---

## 后端开发任务

### 1. 实体类创建

#### UserFavorite.java
```java
@Data
@TableName("user_favorite")
public class UserFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private Long targetId;
    private String targetName;
    private LocalDateTime createTime;
}
```

#### UserHistory.java
```java
@Data
@TableName("user_history")
public class UserHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String type;
    private Long targetId;
    private String targetName;
    private LocalDateTime viewTime;
}
```

### 2. Mapper接口创建

#### UserFavoriteMapper.java
```java
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
```

#### UserHistoryMapper.java
```java
@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistory> {
}
```

### 3. Service层开发

#### FavoriteService.java
**核心方法**:
- `addFavorite(userId, type, targetId, targetName)` - 添加收藏
- `removeFavorite(userId, type, targetId)` - 取消收藏
- `listFavorites(userId, type, pageNum, pageSize)` - 查询收藏列表(分页)
- `isFavorited(userId, type, targetId)` - 判断是否已收藏
- `getFavoriteCount(userId)` - 获取收藏总数

#### HistoryService.java
**核心方法**:
- `addHistory(userId, type, targetId, targetName)` - 添加浏览记录
- `listHistory(userId, type, pageNum, pageSize)` - 查询浏览历史(分页)
- `clearHistory(userId)` - 清空浏览历史
- `deleteHistory(userId, id)` - 删除单条历史记录

#### UserService更新
**新增方法**:
- `updateTarget(userId, schoolId, schoolName, majorId, majorName)` - 更新目标院校/专业
- `getTarget(userId)` - 获取用户目标设定

### 4. Controller层开发

#### FavoriteController.java
**接口列表**:
- `POST /api/favorite/add` - 添加收藏
  - 请求体: `{ userId, type, targetId, targetName }`
  - 返回: `Result<String>`
  
- `DELETE /api/favorite/remove` - 取消收藏
  - 请求参数: `userId, type, targetId`
  - 返回: `Result<String>`
  
- `GET /api/favorite/list` - 收藏列表
  - 请求参数: `userId, type, pageNum, pageSize`
  - 返回: `Result<Page<UserFavorite>>`
  
- `GET /api/favorite/check` - 检查是否已收藏
  - 请求参数: `userId, type, targetId`
  - 返回: `Result<Boolean>`
  
- `GET /api/favorite/count` - 获取收藏总数
  - 请求参数: `userId`
  - 返回: `Result<Integer>`

#### HistoryController.java
**接口列表**:
- `POST /api/history/add` - 添加浏览记录
  - 请求体: `{ userId, type, targetId, targetName }`
  - 返回: `Result<String>`
  
- `GET /api/history/list` - 浏览历史列表
  - 请求参数: `userId, type, pageNum, pageSize`
  - 返回: `Result<Page<UserHistory>>`
  
- `DELETE /api/history/clear` - 清空浏览历史
  - 请求参数: `userId`
  - 返回: `Result<String>`
  
- `DELETE /api/history/{id}` - 删除单条历史
  - 路径参数: `id`
  - 返回: `Result<String>`

#### UserController更新
**新增接口**:
- `PUT /api/user/target` - 更新目标设定
  - 请求体: `{ userId, targetSchoolId, targetSchoolName, targetMajorId, targetMajorName }`
  - 返回: `Result<String>`
  
- `GET /api/user/target/{userId}` - 获取目标设定
  - 路径参数: `userId`
  - 返回: `Result<Map<String, Object>>`

---

## 前端开发任务

### 1. 个人中心主页优化

**文件**: `pages/profile/profile.vue`

**页面结构**:
```
- 用户信息卡片
  - 头像
  - 昵称
  - 目标院校/专业(点击可修改)
  
- 功能入口网格
  - 我的收藏(显示收藏数量)
  - 浏览历史
  - 目标设定
  - 学习统计(跳转到学习伴侣)
  
- 其他功能
  - 设置
  - 关于我们
  - 退出登录
```

**交互逻辑**:
- 点击目标院校/专业 → 跳转到目标设定页面
- 点击我的收藏 → 跳转到收藏列表页面
- 点击浏览历史 → 跳转到历史记录页面

### 2. 目标设定页面

**文件**: `pages/profile/target.vue`

**页面结构**:
```
- 目标院校选择
  - 搜索框(输入院校名称)
  - 院校列表(搜索结果)
  - 已选择院校显示
  
- 目标专业选择
  - 搜索框(输入专业名称)
  - 专业列表(搜索结果)
  - 已选择专业显示
  
- 保存按钮
```

**交互逻辑**:
- 输入院校名称 → 调用`/api/school/list`搜索
- 选择院校 → 显示在已选择区域
- 输入专业名称 → 调用`/api/major/search`搜索
- 选择专业 → 显示在已选择区域
- 点击保存 → 调用`/api/user/target`更新

### 3. 收藏列表页面

**文件**: `pages/profile/favorite.vue`

**页面结构**:
```
- Tab切换
  - 院校(school)
  - 专业(major)
  - 资讯(brochure/adjust/information)
  
- 列表展示
  - 院校卡片(名称、位置、标签)
  - 专业卡片(名称、院校、学位类型)
  - 资讯卡片(标题、发布时间)
  
- 操作按钮
  - 取消收藏(左滑或长按)
  - 查看详情(点击卡片)
```

**交互逻辑**:
- 切换Tab → 调用`/api/favorite/list`加载对应类型收藏
- 点击卡片 → 跳转到详情页面
- 左滑/长按 → 显示取消收藏按钮
- 取消收藏 → 调用`/api/favorite/remove`

### 4. 浏览历史页面

**文件**: `pages/profile/history.vue`

**页面结构**:
```
- Tab切换(同收藏列表)
  
- 列表展示(按时间倒序)
  - 显示浏览时间
  - 卡片样式同收藏列表
  
- 操作按钮
  - 清空历史(顶部按钮)
  - 删除单条(左滑)
```

**交互逻辑**:
- 切换Tab → 调用`/api/history/list`
- 点击卡片 → 跳转到详情页面
- 清空历史 → 弹出确认框 → 调用`/api/history/clear`
- 删除单条 → 调用`/api/history/{id}`

### 5. 详情页面更新

需要在以下页面添加收藏按钮和浏览记录:

#### school/detail.vue
- 右上角添加收藏按钮(心形图标)
- 进入页面时调用`/api/favorite/check`检查收藏状态
- 进入页面时调用`/api/history/add`添加浏览记录
- 点击收藏按钮 → 调用`/api/favorite/add`或`/api/favorite/remove`

#### major/detail.vue
- 同上

#### info/index.vue(资讯列表)
- 点击资讯时添加浏览记录

---

## 技术要点

### 1. 收藏状态管理
- 使用本地缓存优化(避免频繁请求)
- 收藏/取消收藏后立即更新UI
- 使用事件总线或Pinia同步状态

### 2. 搜索优化
- 输入防抖(debounce 500ms)
- 显示搜索历史(可选)
- 支持模糊搜索

### 3. 列表性能优化
- 虚拟列表(数据量大时)
- 分页加载
- 下拉刷新、上拉加载更多

### 4. 数据冗余
- `target_name`字段冗余存储,避免关联查询
- 收藏列表直接显示名称,无需再查询原表

### 5. 防重复收藏
- 数据库唯一索引约束
- 前端按钮状态控制(已收藏时显示"已收藏")

---

## 开发流程

### 第一步: 数据库准备
1. 创建`user_favorite`表
2. 创建`user_history`表
3. 更新`user`表(添加目标院校/专业ID字段)
4. 验证表结构

### 第二步: 后端开发
1. 创建实体类(UserFavorite, UserHistory)
2. 创建Mapper接口
3. 开发FavoriteService
4. 开发HistoryService
5. 更新UserService
6. 创建FavoriteController
7. 创建HistoryController
8. 更新UserController
9. 测试所有接口

### 第三步: 前端开发
1. 优化个人中心主页
2. 开发目标设定页面
3. 开发收藏列表页面
4. 开发浏览历史页面
5. 更新详情页面(添加收藏按钮)
6. 前后端联调测试

### 第四步: 测试验收
1. 功能测试
2. 性能测试
3. 用户体验测试

---

## 验收标准

### 功能验收
- [ ] 能成功设定目标院校和专业
- [ ] 能添加和取消收藏
- [ ] 收藏列表正确显示
- [ ] 浏览历史正确记录
- [ ] 能清空浏览历史
- [ ] 详情页面收藏按钮状态正确
- [ ] 收藏数量统计准确

### 性能验收
- [ ] 收藏列表加载时间 < 1秒
- [ ] 搜索响应时间 < 500ms
- [ ] 收藏/取消收藏响应及时

### 用户体验验收
- [ ] 操作流畅,无卡顿
- [ ] 收藏状态实时更新
- [ ] 错误提示友好
- [ ] 空状态提示清晰

---

## 时间估算

- 数据库设计: 0.5天
- 后端开发: 2天
- 前端开发: 2.5天
- 联调测试: 0.5天
- 优化完善: 0.5天

**总计**: 约6天

---

## 下一步

完成阶段5后,将进入:
- **阶段6**: AI智能模块(推荐/问答)
- **阶段7**: 管理后台模块(数据维护)

---

**开始开发!** 🚀
