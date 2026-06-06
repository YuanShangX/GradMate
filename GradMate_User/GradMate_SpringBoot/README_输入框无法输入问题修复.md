# 输入框无法输入问题修复

## 修复时间
2026-02-20

## 问题描述
用户反馈无法在任务弹窗中输入任务内容和修改任务描述

## 问题分析

### 根本原因
之前的修复没有完全保存，导致：
1. **模板代码未更新** - 还在使用旧的 `class="modal"` 和 `class="modal-content"`
2. **CSS样式未更新** - 还在使用旧的 `.modal` 和 `.modal-content` 样式
3. **输入框结构错误** - 输入框没有包裹在 `form-item` 中，缺少label

### 具体问题

#### 问题1: 模板使用旧类名
```vue
<!-- 错误的代码 -->
<view class="modal" v-if="showModal">
  <view class="modal-content">
    <input class="task-input" v-model="newTask.taskContent" />
  </view>
</view>
```

**影响**: 
- CSS样式不匹配
- 弹窗行为不正确
- 可能导致输入框无法获得焦点

#### 问题2: CSS使用居中弹窗
```css
/* 错误的样式 */
.modal {
  align-items: center;  /* 居中显示 */
  justify-content: center;
}

.modal-content {
  width: 600rpx;
  overflow: hidden;  /* 可能裁剪内容 */
}
```

**影响**:
- 不是底部弹出样式
- overflow: hidden 可能影响输入框

#### 问题3: 输入框结构不完整
```vue
<!-- 错误的结构 -->
<input class="task-input" v-model="newTask.taskContent" />
```

**影响**:
- 缺少label说明
- 样式不统一
- 用户体验差

## 修复内容

### 1. 更新模板 - 使用正确的类名和结构

```vue
<!-- 添加/编辑任务弹窗 -->
<view class="modal-mask" v-if="showModal" @click="hideAddModal">
  <view class="modal-bottom" @click.stop>
    <view class="modal-header">
      <text class="modal-title">{{ isEditMode ? '编辑任务' : '添加任务' }}</text>
      <text class="modal-close" @click="hideAddModal">×</text>
    </view>
    <view class="modal-body">
      <view class="form-item">
        <text class="form-label">任务内容</text>
        <input 
          class="task-input" 
          v-model="newTask.taskContent" 
          placeholder="输入任务内容"
          maxlength="50"
        />
      </view>
      <view class="form-item">
        <text class="form-label">预计时长</text>
        <picker mode="selector" :range="durationOptions" :value="durationIndex" @change="onDurationChange">
          <view class="picker-view">
            <text class="picker-text">{{ newTask.estimatedDuration }}分钟</text>
            <text class="picker-arrow">›</text>
          </view>
        </picker>
      </view>
      <view class="form-item">
        <text class="form-label">优先级</text>
        <view class="priority-selector">
          <view 
            v-for="p in [1, 2, 3]" 
            :key="p"
            class="priority-option"
            :class="{ active: newTask.priority === p }"
            @click="newTask.priority = p"
          >
            {{ getPriorityText(p) }}
          </view>
        </view>
      </view>
    </view>
    <view class="modal-footer">
      <button class="modal-btn cancel-btn" @click="hideAddModal">取消</button>
      <button class="modal-btn confirm-btn" @click="isEditMode ? updateTask() : addTask()">确定</button>
    </view>
  </view>
</view>
```

### 2. 更新CSS - 底部弹出样式

```css
/* 弹窗样式 - 底部弹出 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;  /* 底部对齐 */
  z-index: 998;
}

.modal-bottom {
  width: 100%;
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;  /* 只有顶部圆角 */
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease-out;  /* 滑入动画 */
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.modal-body {
  padding: 30rpx;
  flex: 1;
  overflow-y: auto;  /* 内容可滚动 */
  overflow-x: visible;  /* 不裁剪横向内容 */
}

.task-input {
  width: 100%;
  padding: 20rpx;
  border: 2rpx solid #e5e5e5;
  border-radius: 12rpx;
  font-size: 28rpx;
  box-sizing: border-box;  /* 关键：包含padding在宽度内 */
  background: #fff;
}
```

### 3. 关键修复点

#### 输入框样式
- 添加 `box-sizing: border-box` 确保宽度计算正确
- 使用 `width: 100%` 而不是固定宽度
- 增加边框宽度到 2rpx 提高可见性

#### 弹窗布局
- 使用 `flex-direction: column` 确保垂直布局
- `modal-body` 设置 `overflow-y: auto` 允许滚动
- `overflow-x: visible` 不裁剪picker下拉菜单

#### z-index层级
- `action-sheet`: 997
- `modal-mask`: 998
- picker原生组件自动在最上层

## 修复效果

✅ 输入框可以正常获得焦点
✅ 可以输入和编辑任务内容
✅ 弹窗从底部滑入，体验更好
✅ Picker下拉菜单不被遮挡
✅ 所有表单元素都有清晰的label

## 测试步骤

1. 点击"+"按钮打开新建任务弹窗
2. 点击"任务内容"输入框
3. 输入文字，确认可以正常输入
4. 点击"预计时长"，确认picker正常弹出
5. 选择优先级，确认可以正常切换
6. 点击确定，确认任务创建成功
7. 长按任务卡片，点击"编辑"
8. 修改任务内容，确认可以正常编辑
9. 点击确定，确认修改保存成功

## 注意事项

1. **必须清除缓存** - 前端修改后需要清除缓存或重新编译
2. **检查类名** - 确保模板中使用 `modal-mask` 和 `modal-bottom`
3. **检查CSS** - 确保CSS中定义了对应的样式
4. **box-sizing** - 所有输入框都应该设置 `box-sizing: border-box`

## 相关文件
- `GradMate_Uniapp/pages/study/task.vue` - 已完全修复

## 调试技巧

如果输入框还是无法输入：
1. 检查浏览器控制台是否有错误
2. 检查元素的z-index是否正确
3. 检查是否有其他元素覆盖在输入框上
4. 尝试给输入框添加 `@click.stop` 阻止事件冒泡
5. 检查v-model绑定是否正确
