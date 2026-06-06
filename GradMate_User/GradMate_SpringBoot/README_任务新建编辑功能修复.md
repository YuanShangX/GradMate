# 任务新建和编辑功能修复

## 修复时间
2026-02-20

## 问题描述
用户反馈无法新建和编辑任务

## 问题分析

### 问题1: editTask方法未实现
**位置**: `GradMate_Uniapp/pages/study/task.vue`

**问题代码**:
```javascript
editTask() {
    // TODO: 实现编辑功能
    this.hideTaskMenu();
    uni.showToast({
        title: '编辑功能开发中',
        icon: 'none'
    });
}
```

**原因**: 之前的修改没有正确保存，editTask方法还是占位符代码

### 问题2: updateTask方法缺少estimatedDuration参数
**位置**: `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/StudyService.java`

**问题代码**:
```java
public boolean updateTask(Long taskId, String taskContent, Integer priority) {
    // 缺少 estimatedDuration 参数
}
```

**原因**: Service层的updateTask方法只支持更新taskContent和priority，不支持更新estimatedDuration

### 问题3: Controller层未传递estimatedDuration
**位置**: `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/StudyController.java`

**问题代码**:
```java
@PutMapping("/task/{id}")
public Result<Boolean> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
    boolean success = studyService.updateTask(id, request.getTaskContent(), request.getPriority());
    // 未传递 estimatedDuration
}
```

## 修复内容

### 1. 前端 - 实现editTask和updateTask方法

**文件**: `GradMate_Uniapp/pages/study/task.vue`

```javascript
editTask() {
    if (!this.selectedTask) return;
    
    this.isEditMode = true;
    this.newTask = {
        id: this.selectedTask.id,
        taskContent: this.selectedTask.taskContent,
        estimatedDuration: this.selectedTask.estimatedDuration || 25,
        priority: this.selectedTask.priority
    };
    this.hideTaskMenu();
    this.showModal = true;
},

async updateTask() {
    if (!this.newTask.taskContent.trim()) {
        uni.showToast({
            title: '请输入任务内容',
            icon: 'none'
        });
        return;
    }
    
    try {
        const res = await put(`/study/task/${this.newTask.id}`, {
            taskContent: this.newTask.taskContent,
            priority: this.newTask.priority,
            estimatedDuration: this.newTask.estimatedDuration
        });
        
        if (res.code === 200) {
            uni.showToast({
                title: '更新成功',
                icon: 'success'
            });
            this.hideAddModal();
            this.loadTasks();
        }
    } catch (error) {
        console.error('更新任务失败:', error);
        uni.showToast({
            title: '更新失败',
            icon: 'none'
        });
    }
}
```

### 2. 后端Service - 添加estimatedDuration参数

**文件**: `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/StudyService.java`

```java
public boolean updateTask(Long taskId, String taskContent, Integer priority, Integer estimatedDuration) {
    DailyTask task = dailyTaskMapper.selectById(taskId);
    if (task == null) {
        return false;
    }
    
    task.setTaskContent(taskContent);
    task.setPriority(priority);
    if (estimatedDuration != null) {
        task.setEstimatedDuration(estimatedDuration);
    }
    task.setUpdateTime(LocalDateTime.now());
    
    return dailyTaskMapper.updateById(task) > 0;
}
```

### 3. 后端Controller - 传递estimatedDuration参数

**文件**: `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/StudyController.java`

```java
@PutMapping("/task/{id}")
public Result<Boolean> updateTask(@PathVariable Long id, @RequestBody TaskRequest request) {
    boolean success = studyService.updateTask(
        id, 
        request.getTaskContent(), 
        request.getPriority(), 
        request.getEstimatedDuration()
    );
    return success ? Result.success(true) : Result.error("更新失败");
}
```

## 修复效果

✅ 可以正常新建任务
✅ 可以编辑任务内容、优先级和预计时长
✅ 编辑后的数据正确保存到数据库
✅ 前端显示更新后的数据

## 测试步骤

### 1. 后端测试
运行测试脚本：
```powershell
.\test_task_api.ps1
```

### 2. 前端测试
1. 点击"+"按钮创建新任务
2. 输入任务内容、选择时长和优先级
3. 点击确定，验证任务创建成功
4. 长按任务卡片，点击"编辑"
5. 修改任务内容、时长、优先级
6. 点击确定，验证任务更新成功
7. 刷新页面，确认数据持久化

## 相关文件
- `GradMate_Uniapp/pages/study/task.vue` - 前端任务页面
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/StudyController.java` - 控制器
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/StudyService.java` - 服务层
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/dto/TaskRequest.java` - DTO
- `GradMate_SpringBoot/test_task_api.ps1` - API测试脚本

## 注意事项
1. 确保后端已重启以加载新代码
2. 前端需要清除缓存或重新编译
3. TaskRequest DTO已包含estimatedDuration字段，无需修改
