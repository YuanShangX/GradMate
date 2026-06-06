# 任务弹窗最终完整修复

## 修复时间
2026-02-20

## 问题历史

### 问题1: Picker被遮挡
- 原因: `overflow: hidden` 裁剪了picker下拉菜单
- 解决: 改为 `overflow: visible`

### 问题2: 弹窗重叠
- 原因: z-index层级混乱
- 解决: 调整层级 (action-sheet: 997, modal: 998)

### 问题3: 无法编辑任务
- 原因: editTask方法未实现
- 解决: 实现完整的编辑功能

### 问题4: 无法输入任务内容
- 原因: 模板和CSS不匹配，使用了错误的类名
- 解决: 统一使用正确的类名和结构

## 最终解决方案

### 1. 弹窗设计 - 居中模态框

```vue
<view class="modal-mask" v-if="showModal" @click.stop="hideAddModal">
  <view class="modal-content" @click.stop>
    <!-- 内容 -->
  </view>
</view>
```

**关键点**:
- `@click.stop` 防止事件冒泡
- `z-index: 9999` 确保在最上层
- 居中显示，不是底部弹出

### 2. 输入框结构

```vue
<!-- 任务内容 -->
<view class="input-wrapper">
  <input 
    class="task-input" 
    v-model="newTask.taskContent" 
    placeholder="任务内容"
    maxlength="50"
    :focus="showModal"
  />
</view>

<!-- 任务描述 -->
<view class="input-wrapper">
  <textarea 
    class="task-textarea" 
    v-model="newTask.description" 
    placeholder="任务描述（选填）"
    maxlength="200"
    :auto-height="true"
  />
</view>
```

**关键点**:
- 使用 `input-wrapper` 包裹
- `:focus="showModal"` 自动聚焦
- textarea支持自动高度

### 3. CSS样式

```css
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;  /* 最高层级 */
}

.modal-content {
  width: 680rpx;
  background: #fff;
  border-radius: 24rpx;
  overflow: visible;  /* 关键：不裁剪内容 */
}

.task-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 30rpx;
  color: #1a1a1a;
  box-sizing: border-box;  /* 关键：包含padding */
}

.task-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx 24rpx;
  background: #f5f7fa;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #1a1a1a;
  box-sizing: border-box;
  line-height: 1.6;
}
```

### 4. 数据结构

```javascript
data() {
  return {
    showModal: false,
    isEditMode: false,
    newTask: {
      taskContent: '',
      description: '',
      estimatedDuration: 25,
      priority: 2
    }
  }
}
```

### 5. 方法实现

```javascript
// 显示新建弹窗
showAddModal() {
  this.isEditMode = false;
  this.newTask = {
    taskContent: '',
    description: '',
    estimatedDuration: 25,
    priority: 2
  };
  this.showModal = true;
},

// 显示编辑弹窗
editTask() {
  if (!this.selectedTask) return;
  
  this.isEditMode = true;
  this.newTask = {
    id: this.selectedTask.id,
    taskContent: this.selectedTask.taskContent,
    description: this.selectedTask.description || '',
    estimatedDuration: this.selectedTask.estimatedDuration || 25,
    priority: this.selectedTask.priority
  };
  this.hideTaskMenu();
  this.showModal = true;
},

// 新建任务
async addTask() {
  if (!this.newTask.taskContent.trim()) {
    uni.showToast({
      title: '请输入任务内容',
      icon: 'none'
    });
    return;
  }
  
  try {
    const res = await post('/study/task', {
      userId: this.userId,
      taskContent: this.newTask.taskContent,
      description: this.newTask.description,
      taskDate: this.currentDate,
      priority: this.newTask.priority,
      estimatedDuration: this.newTask.estimatedDuration
    });
    
    if (res.code === 200) {
      uni.showToast({
        title: '添加成功',
        icon: 'success'
      });
      this.hideAddModal();
      this.loadTasks();
    }
  } catch (error) {
    console.error('添加任务失败:', error);
  }
},

// 更新任务
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
      description: this.newTask.description,
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
  }
}
```

## 完整功能清单

✅ 可以点击"+"按钮打开新建弹窗
✅ 可以输入任务内容
✅ 可以输入任务描述（选填）
✅ 可以选择预计时长
✅ 可以查看优先级（暂不支持修改）
✅ 可以点击确定按钮创建任务
✅ 可以长按任务卡片打开菜单
✅ 可以点击编辑打开编辑弹窗
✅ 可以修改任务内容和描述
✅ 可以点击确定保存修改
✅ 可以点击遮罩层或×关闭弹窗

## 测试步骤

### 新建任务测试
1. 点击右下角"+"按钮
2. 确认弹窗打开，标题显示"新建任务"
3. 点击"任务内容"输入框
4. 输入文字，确认可以正常输入
5. 点击"任务描述"输入框
6. 输入描述文字（可选）
7. 点击"预计时长"，选择时长
8. 查看优先级显示
9. 点击"确定"按钮
10. 确认任务创建成功，弹窗关闭
11. 确认任务出现在列表中

### 编辑任务测试
1. 长按任务卡片
2. 确认菜单弹出
3. 点击"编辑"
4. 确认弹窗打开，标题显示"编辑任务"
5. 确认输入框预填充了任务数据
6. 修改任务内容
7. 修改任务描述
8. 修改预计时长
9. 点击"确定"按钮
10. 确认任务更新成功
11. 确认列表中的任务已更新

### 关闭弹窗测试
1. 打开新建/编辑弹窗
2. 点击右上角"×"按钮
3. 确认弹窗关闭
4. 再次打开弹窗
5. 点击弹窗外的遮罩层
6. 确认弹窗关闭

## 常见问题排查

### 问题: 无法输入
**检查**:
1. 确认 `v-model` 绑定正确
2. 确认 `newTask.taskContent` 存在
3. 确认没有其他元素覆盖在输入框上
4. 检查z-index是否正确

### 问题: 点击确定无反应
**检查**:
1. 确认 `@click` 事件绑定正确
2. 确认方法名拼写正确
3. 检查控制台是否有错误
4. 确认后端接口正常

### 问题: 弹窗无法关闭
**检查**:
1. 确认 `hideAddModal` 方法存在
2. 确认 `@click.stop` 正确使用
3. 确认 `showModal` 状态正确更新

## 后端支持

确保后端接口支持description字段：

```java
// TaskRequest.java
@Data
public class TaskRequest {
    private String taskContent;
    private String description;  // 添加此字段
    private Integer estimatedDuration;
    private Integer priority;
    // ...
}

// StudyService.java
public DailyTask createTask(TaskRequest request) {
    DailyTask task = new DailyTask();
    task.setTaskContent(request.getTaskContent());
    task.setDescription(request.getDescription());  // 设置描述
    task.setEstimatedDuration(request.getEstimatedDuration());
    task.setPriority(request.getPriority());
    // ...
}
```

## 相关文件
- `GradMate_Uniapp/pages/study/task.vue` - 任务页面（已完整修复）
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/controller/StudyController.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/service/StudyService.java`
- `GradMate_SpringBoot/src/main/java/com/gradmate/gradmate_springboot/dto/TaskRequest.java`

## 注意事项

1. **必须清除缓存** - 前端修改后需要清除缓存或重新编译
2. **重启后端** - 如果修改了后端代码，必须重启服务
3. **检查数据库** - 确保daily_task表有description字段
4. **测试环境** - 在微信开发者工具中测试，不要只在浏览器中测试
