# ToDo清单后端代码更新完成

## 更新时间
2026-02-20

## 已完成的工作

### 1. 更新实体类

#### ✅ DailyTask.java（已更新）
新增字段：
- `actualDuration` - 实际学习时长
- `tags` - 标签（逗号分隔）
- `parentTaskId` - 父任务ID
- `repeatType` - 重复类型
- `sortOrder` - 排序顺序
- `description` - 任务描述
- `reminderTime` - 提醒时间
- `status` - 任务状态

#### ✅ TaskTag.java（新建）
任务标签实体类，包含：
- `id` - 主键
- `userId` - 用户ID
- `tagName` - 标签名称
- `tagColor` - 标签颜色
- `icon` - 图标
- `sortOrder` - 排序顺序
- `createTime` - 创建时间
- `updateTime` - 更新时间

#### ✅ TaskSubtask.java（新建）
子任务实体类，包含：
- `id` - 主键
- `parentTaskId` - 父任务ID
- `subtaskContent` - 子任务内容
- `isCompleted` - 是否完成
- `sortOrder` - 排序顺序
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `completeTime` - 完成时间

#### ✅ TaskReminder.java（新建）
任务提醒实体类，包含：
- `id` - 主键
- `taskId` - 任务ID
- `reminderTime` - 提醒时间
- `reminderType` - 提醒类型
- `isSent` - 是否已发送
- `createTime` - 创建时间

### 2. 创建Mapper接口

#### ✅ TaskTagMapper.java（新建）
```java
@Mapper
public interface TaskTagMapper extends BaseMapper<TaskTag> {
}
```

#### ✅ TaskSubtaskMapper.java（新建）
```java
@Mapper
public interface TaskSubtaskMapper extends BaseMapper<TaskSubtask> {
}
```

#### ✅ TaskReminderMapper.java（新建）
```java
@Mapper
public interface TaskReminderMapper extends BaseMapper<TaskReminder> {
}
```

## 文件清单

### 实体类（Entity）
- ✅ `DailyTask.java` - 已更新
- ✅ `TaskTag.java` - 新建
- ✅ `TaskSubtask.java` - 新建
- ✅ `TaskReminder.java` - 新建

### Mapper接口
- ✅ `DailyTaskMapper.java` - 已存在
- ✅ `TaskTagMapper.java` - 新建
- ✅ `TaskSubtaskMapper.java` - 新建
- ✅ `TaskReminderMapper.java` - 新建

## 下一步工作

### 1. 扩展StudyService

需要在`StudyService.java`中添加以下方法：

#### 标签管理
```java
// 获取用户标签列表
List<TaskTag> getUserTags(Long userId);

// 创建标签
TaskTag createTag(Long userId, String tagName, String tagColor, String icon);

// 更新标签
boolean updateTag(Long tagId, String tagName, String tagColor);

// 删除标签
boolean deleteTag(Long tagId);
```

#### 子任务管理
```java
// 获取任务的子任务列表
List<TaskSubtask> getSubtasks(Long parentTaskId);

// 创建子任务
TaskSubtask createSubtask(Long parentTaskId, String content);

// 完成子任务
boolean completeSubtask(Long subtaskId);

// 删除子任务
boolean deleteSubtask(Long subtaskId);
```

#### 任务搜索和筛选
```java
// 搜索任务
List<DailyTask> searchTasks(Long userId, String keyword);

// 按标签筛选
List<DailyTask> getTasksByTag(Long userId, String tag);

// 按状态筛选
List<DailyTask> getTasksByStatus(Long userId, String status);

// 按优先级筛选
List<DailyTask> getTasksByPriority(Long userId, Integer priority);
```

#### 任务提醒
```java
// 创建提醒
TaskReminder createReminder(Long taskId, LocalDateTime reminderTime, String type);

// 获取待发送提醒
List<TaskReminder> getPendingReminders();

// 标记提醒已发送
boolean markReminderSent(Long reminderId);
```

### 2. 扩展StudyController

需要在`StudyController.java`中添加以下接口：

```java
// 标签管理
@GetMapping("/tags")
Result<List<TaskTag>> getUserTags(@RequestParam Long userId);

@PostMapping("/tags")
Result<TaskTag> createTag(@RequestBody TaskTagRequest request);

@PutMapping("/tags/{id}")
Result<Boolean> updateTag(@PathVariable Long id, @RequestBody TaskTagRequest request);

@DeleteMapping("/tags/{id}")
Result<Boolean> deleteTag(@PathVariable Long id);

// 子任务管理
@GetMapping("/task/{taskId}/subtasks")
Result<List<TaskSubtask>> getSubtasks(@PathVariable Long taskId);

@PostMapping("/task/{taskId}/subtasks")
Result<TaskSubtask> createSubtask(@PathVariable Long taskId, @RequestBody SubtaskRequest request);

@PutMapping("/subtask/{id}/complete")
Result<Boolean> completeSubtask(@PathVariable Long id);

@DeleteMapping("/subtask/{id}")
Result<Boolean> deleteSubtask(@PathVariable Long id);

// 任务搜索
@GetMapping("/task/search")
Result<List<DailyTask>> searchTasks(@RequestParam Long userId, @RequestParam String keyword);

// 任务筛选
@GetMapping("/task/filter")
Result<List<DailyTask>> filterTasks(
    @RequestParam Long userId,
    @RequestParam(required = false) String tag,
    @RequestParam(required = false) String status,
    @RequestParam(required = false) Integer priority
);
```

### 3. 创建DTO类

需要创建以下请求/响应DTO：

```java
// TaskTagRequest.java
public class TaskTagRequest {
    private Long userId;
    private String tagName;
    private String tagColor;
    private String icon;
}

// SubtaskRequest.java
public class SubtaskRequest {
    private String subtaskContent;
    private Integer sortOrder;
}

// TaskDetailResponse.java
public class TaskDetailResponse {
    private DailyTask task;
    private List<TaskSubtask> subtasks;
    private List<String> tags;
}
```

### 4. 更新现有方法

需要修改`StudyService.java`中的以下方法：

```java
// createTask方法 - 支持新字段
public DailyTask createTask(TaskRequest request) {
    DailyTask task = new DailyTask();
    // ... 原有字段
    task.setTags(request.getTags());
    task.setDescription(request.getDescription());
    task.setStatus("todo"); // 默认状态
    task.setRepeatType(request.getRepeatType());
    // ...
}

// completeTask方法 - 同时更新status和is_completed
public boolean completeTask(Long taskId) {
    DailyTask task = dailyTaskMapper.selectById(taskId);
    if (task == null) return false;
    
    task.setIsCompleted(task.getIsCompleted() == 1 ? 0 : 1);
    task.setStatus(task.getIsCompleted() == 1 ? "done" : "todo");
    task.setCompleteTime(task.getIsCompleted() == 1 ? LocalDateTime.now() : null);
    
    return dailyTaskMapper.updateById(task) > 0;
}
```

## 测试建议

1. 启动Spring Boot应用，检查是否有编译错误
2. 测试现有接口是否正常工作
3. 使用Postman测试新字段的读写
4. 验证数据库字段映射是否正确

## 注意事项

1. ⚠️ 所有新增的Mapper都需要被Spring扫描到
2. ⚠️ 确保`@MapperScan`配置正确
3. ⚠️ 新字段在前端使用前需要先在后端测试
4. ⚠️ 保持is_completed和status字段同步更新

---

**状态**: ✅ 实体类和Mapper已完成
**下一步**: 扩展Service和Controller层
**执行时间**: 2026-02-20
