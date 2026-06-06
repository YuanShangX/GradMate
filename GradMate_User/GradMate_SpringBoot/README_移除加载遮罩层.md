# 移除全屏加载遮罩层

## 问题描述
在研小伴页面发送消息时，会显示全屏的"加载中..."遮罩层，影响用户体验。用户希望移除这个遮罩层，只在消息列表中显示加载动画。

## 问题原因
在`utils/request.js`中，默认会为所有API请求显示`uni.showLoading`遮罩层：

```javascript
function request(options) {
  return new Promise((resolve, reject) => {
    // 显示加载提示
    if (options.loading !== false) {
      uni.showLoading({
        title: '加载中...',
        mask: true  // 全屏遮罩
      });
    }
    // ...
  });
}
```

## 解决方案

### 方案说明
在chat.vue中调用API时，传入`loading: false`参数，禁用全屏加载遮罩层。页面已经有自己的加载动画（消息列表中的"思考中"动画），不需要额外的遮罩层。

### 修改内容

#### 1. 修改loadSessions方法

**修改前:**
```javascript
async loadSessions() {
  try {
    const res = await get(`/ai/session/list?userId=${this.userId}`);
    // ...
  }
}
```

**修改后:**
```javascript
async loadSessions() {
  try {
    const res = await get(`/ai/session/list?userId=${this.userId}`, {}, false);
    //                                                              ↑  ↑   ↑
    //                                                            url data loading
    // ...
  }
}
```

#### 2. 修改sendMessage方法

**修改前:**
```javascript
async sendMessage() {
  // ...
  try {
    const res = await post('/ai/chat', {
      userId: this.userId,
      sessionId: this.currentSessionId,
      message: userMessage
    });
    // ...
  }
}
```

**修改后:**
```javascript
async sendMessage() {
  // ...
  try {
    const res = await post('/ai/chat', {
      userId: this.userId,
      sessionId: this.currentSessionId,
      message: userMessage
    }, false);  // ← 禁用加载遮罩层
    // ...
  }
}
```

## 加载状态显示

### 页面自带的加载动画
页面已经有完善的加载状态显示，不需要额外的遮罩层：

#### 1. 消息列表加载动画
```vue
<!-- 加载动画 -->
<view v-if="loading" class="message-item ai">
  <view class="avatar ai-avatar"></view>
  <view class="message-bubble ai loading">
    <text class="loading-text">思考中</text>
    <view class="loading-dots">
      <view class="dot"></view>
      <view class="dot"></view>
      <view class="dot"></view>
    </view>
  </view>
</view>
```

**视觉效果:**
```
◉ 思考中 ● ● ●
   ↑      ↑ ↑ ↑
  AI头像  弹跳动画
```

#### 2. 发送按钮禁用状态
```vue
<view 
  class="send-btn" 
  :class="{ disabled: !inputText.trim() || loading }"
  @click="sendMessage"
>
  <view class="send-icon"></view>
</view>
```

**状态变化:**
- 正常状态：蓝色渐变 + 阴影
- 禁用状态：灰色 + 无阴影

#### 3. 输入框禁用状态
```vue
<input 
  class="input" 
  v-model="inputText" 
  placeholder="输入你的问题..."
  :disabled="loading"
  @confirm="sendMessage"
/>
```

## API调用参数说明

### request.js中的loading参数
```javascript
/**
 * GET请求
 * @param {string} url - 请求URL
 * @param {object} data - 请求参数
 * @param {boolean} loading - 是否显示加载遮罩层，默认true
 */
export function get(url, data = {}, loading = true) {
  return request({
    url,
    method: 'GET',
    data,
    loading
  });
}

/**
 * POST请求
 * @param {string} url - 请求URL
 * @param {object} data - 请求数据
 * @param {boolean} loading - 是否显示加载遮罩层，默认true
 */
export function post(url, data = {}, loading = true) {
  return request({
    url,
    method: 'POST',
    data,
    loading
  });
}
```

### 使用示例

#### 需要加载遮罩层（默认）
```javascript
// 方式1：不传loading参数（默认true）
const res = await get('/api/data');

// 方式2：显式传true
const res = await get('/api/data', {}, true);
```

#### 不需要加载遮罩层
```javascript
// 传false禁用遮罩层
const res = await get('/api/data', {}, false);
const res = await post('/api/data', { key: 'value' }, false);
```

## 其他API调用

### 保留加载遮罩层的场景
以下场景仍然保留加载遮罩层，因为没有自定义的加载状态显示：

1. **创建新会话** - createNewSession()
   ```javascript
   const res = await post('/ai/session/create', {
     userId: this.userId
   });  // 保留遮罩层
   ```

2. **重命名会话** - confirmRename()
   ```javascript
   const res = await put(`/ai/session/${this.renameSessionId}/title`, {
     title: this.renameTitle
   });  // 保留遮罩层
   ```

3. **删除会话** - deleteSession()
   ```javascript
   const res = await del(`/ai/session/${sessionId}`);
   // 保留遮罩层
   ```

### 原因说明
这些操作：
- 执行时间短（通常<500ms）
- 没有自定义的加载状态显示
- 需要阻止用户重复操作
- 完成后有Toast提示

因此保留加载遮罩层是合理的。

## 用户体验对比

### 优化前
```
用户发送消息
    ↓
显示全屏遮罩层 "加载中..."  ← 遮挡整个页面
    ↓
等待AI回复
    ↓
隐藏遮罩层
    ↓
显示AI回复
```

**问题:**
- 全屏遮罩层遮挡页面内容
- 用户无法看到之前的对话
- 视觉体验不连贯

### 优化后
```
用户发送消息
    ↓
消息列表显示用户消息
    ↓
消息列表显示"思考中"动画  ← 不遮挡页面
    ↓
等待AI回复
    ↓
隐藏"思考中"动画
    ↓
显示AI回复
```

**优点:**
- 不遮挡页面内容
- 用户可以看到之前的对话
- 视觉体验连贯流畅
- 加载状态清晰明确

## 视觉效果

### 加载动画效果
```
┌─────────────────────────────┐
│ 用户消息                     │
├─────────────────────────────┤
│ ◉ 思考中 ● ● ●              │ ← 弹跳动画
│   ↑      ↑ ↑ ↑               │
│  AI头像  三个圆点依次弹跳     │
└─────────────────────────────┘
```

### 按钮状态变化
```
正常状态:
╭─────╮
│  ➤  │  蓝色渐变 + 阴影
╰─────╯

禁用状态:
╭─────╮
│  ➤  │  灰色 + 无阴影
╰─────╯
```

## 修改文件清单

- ✅ `GradMate_Uniapp/pages/ai/chat.vue`
  - 修改 `loadSessions()` 方法
  - 修改 `sendMessage()` 方法

## 测试验证

### 测试步骤
1. 打开研小伴页面
2. 发送一条消息
3. 观察加载状态

### 预期效果
- ✅ 不显示全屏"加载中..."遮罩层
- ✅ 消息列表显示"思考中"动画
- ✅ 发送按钮变为禁用状态（灰色）
- ✅ 输入框禁用，无法输入
- ✅ 可以看到之前的对话内容
- ✅ AI回复后，加载动画消失

### 测试结果
- [ ] 不显示全屏遮罩层
- [ ] 加载动画正常显示
- [ ] 按钮状态正确切换
- [ ] 用户体验流畅

## 注意事项

### 1. loading参数位置
```javascript
// GET请求
get(url, data, loading)
//      ↑    ↑    ↑
//     URL  参数  是否显示遮罩层

// POST请求
post(url, data, loading)
//       ↑    ↑    ↑
//      URL  数据  是否显示遮罩层
```

### 2. 默认值
如果不传loading参数，默认为`true`，会显示遮罩层。

### 3. 其他页面
如果其他页面也需要移除加载遮罩层，可以使用相同的方法：
```javascript
const res = await get('/api/xxx', {}, false);
const res = await post('/api/xxx', { data }, false);
```

## 总结

通过在API调用时传入`loading: false`参数，成功移除了全屏加载遮罩层。页面使用自带的"思考中"动画显示加载状态，用户体验更加流畅自然。

### 优化效果
- ✅ 移除了全屏遮罩层
- ✅ 保留了加载状态显示
- ✅ 不遮挡页面内容
- ✅ 视觉体验更流畅
- ✅ 用户体验更友好

---

**修复时间**: 2026-02-19  
**修复人**: Kiro AI Assistant  
**状态**: ✅ 已完成
