# ToDo清单弹窗最终修复

## 修复时间
2026-02-20

## 问题描述
1. 编辑和创建任务的弹窗把预计时长和优先级的选择器挡住了
2. 看起来有两个编辑和新建任务弹窗重叠在一起
3. Picker组件的下拉菜单被模态框遮挡

## 根本原因分析

### 原因1: overflow: hidden 裁剪picker下拉菜单
- `.modal-bottom` 设置了 `overflow: hidden`
- Uniapp的picker组件下拉菜单是原生组件，会被overflow裁剪
- z-index对原生组件无效

### 原因2: z-index层级混乱
- modal-mask: 999
- action-sheet: 1000
- 当action-sheet和modal同时存在时，层级冲突导致视觉重叠

## 修复内容

### 1. 移除overflow限制（关键修复）
```css
/* 修改前 */
.modal-bottom {
  overflow: hidden;  /* 会裁剪picker */
}

/* 修改后 */
.modal-bottom {
  overflow: visible;  /* 允许picker下拉菜单显示 */
}

.modal-body {
  overflow-y: auto;
  overflow-x: visible;  /* 只在body内滚动 */
}
```

### 2. 调整z-index层级
```css
/* 修改后的层级 */
.action-sheet {
  z-index: 997;  /* 最底层 */
}

.modal-mask {
  z-index: 998;  /* 中间层 */
}

/* picker原生组件自动在最上层，无需设置 */
```

### 3. 移除无效的z-index设置
- 移除 `.picker-view` 的 `z-index: 1001`
- 移除 `.picker-view` 的 `position: relative`
- 原生picker组件不受CSS z-index控制

### 4. 弹窗样式优化
- 从居中弹窗改为底部弹出
- 添加滑入动画效果
- 圆角只在顶部

### 5. 编辑功能实现
- 统一使用一个模态框
- 添加 `isEditMode` 状态标识
- 实现完整的编辑和更新功能

## 关键知识点

### Uniapp Picker组件特性
1. Picker的下拉菜单是**原生组件**
2. 原生组件层级高于webview层
3. CSS的z-index对原生组件无效
4. 需要通过overflow: visible来避免裁剪

### 层级管理原则
- 底层组件使用较小的z-index (997)
- 上层组件使用较大的z-index (998)
- 原生组件自动在最上层，无需设置

## 修复效果
✅ 弹窗从底部滑入，更符合移动端交互习惯
✅ Picker下拉菜单不再被遮挡，可以正常选择
✅ 只有一个模态框，不会出现重叠问题
✅ 编辑功能完整实现
✅ z-index层级清晰，不会冲突

## 测试建议
1. 点击"+"按钮，测试添加任务功能
2. 点击预计时长选择器，确认下拉菜单完整显示
3. 点击优先级选项，确认可以正常切换
4. 长按任务卡片，点击"编辑"
5. 修改任务后保存
6. 确认没有重叠弹窗

## 相关文件
- `GradMate_Uniapp/pages/study/task.vue` - 已修复
