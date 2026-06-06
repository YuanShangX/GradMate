# 阶段6：AI智能模块 - 完成说明

## 完成时间
2026-02-14

---

## 技术栈

- **AI框架**: LangChain4j 0.35.0
- **AI模型**: 火山引擎豆包 `doubao-1.5-pro-32k-250115`
- **API Key**: 环境变量 `ARK_API_KEY`
- **Endpoint ID**: 环境变量 `ARK_ENDPOINT_ID`

---

## 完成内容

### 依赖配置 ✅
- 添加LangChain4j核心依赖
- 添加LangChain4j火山引擎集成依赖

### 配置文件 ✅
- application.properties新增AI配置
  - 火山引擎API配置
  - 聊天记忆配置
  - 系统提示词配置

### 后端开发 ✅

#### 配置类
- `AiConfig.java` - AI配置类
  - 配置火山引擎聊天模型
  - 配置聊天记忆存储
  - 配置记忆窗口大小

#### 工具类
- `GradMateTools.java` - AI工具类
  - `analyzeTargetUniversity()` - 分析推荐目标院校
  - `getScoreLine()` - 获取分数线
  - `getGraduationNews()` - 获取考研资讯
  - `getUserTarget()` - 获取用户目标设定

#### 服务类
- `GradMateAgentService.java` - AI助手服务
  - `chat()` - 处理用户消息
  - `clearHistory()` - 清除会话历史
  - `getMessageCount()` - 获取消息数量
  - 维护每个用户独立的会话上下文

#### 控制器
- `AiController.java` - AI控制器
  - `POST /api/ai/chat` - AI对话接口
  - `DELETE /api/ai/history` - 清除会话历史
  - `GET /api/ai/session` - 获取会话信息

### 前端开发 ✅

#### 新增页面
- `pages/ai/chat.vue` - AI聊天页面
  - 消息列表展示
  - 用户/AI消息气泡
  - 输入框和发送按钮
  - 快捷问题按钮
  - 加载动画
  - 清除历史功能

#### 首页更新
- `pages/index/index.vue` - 添加AI智囊入口卡片
  - 渐变背景设计
  - 功能描述
  - 点击跳转到AI聊天页面

---

## 核心功能

### 1. 多轮对话
- 使用ChatMemory维护会话上下文
- 每个用户独立的会话历史
- 保留最近10轮对话

### 2. 工具调用(Function Calling)
AI可以自动调用以下工具:
- 院校推荐 - 根据用户背景推荐院校
- 分数线查询 - 查询指定院校专业分数线
- 资讯获取 - 获取最新考研资讯
- 用户目标 - 获取用户设定的目标

### 3. 课业辅导
- 高等数学题目讲解
- 英语学习指导
- 专业课问题解答
- 循序渐进的教学方式

### 4. 情绪疏导
- 识别焦虑、压力、emo情绪
- 给予共情和理解
- 提供心理支持和鼓励
- 传递正能量

---

## API接口

### 1. AI对话
```
POST /api/ai/chat
请求体: { userId, message }
返回: { code, data: "AI回复内容" }
```

### 2. 清除历史
```
DELETE /api/ai/history?userId={userId}
返回: { code, data: "会话历史已清除" }
```

### 3. 获取会话信息
```
GET /api/ai/session?userId={userId}
返回: { 
  code, 
  data: { 
    userId, 
    messageCount, 
    hasHistory 
  } 
}
```

---

## 系统提示词

```
你是GradMate考研助手,一个专业、温暖、有共情能力的AI伙伴。

你的核心能力:
1. 考研咨询: 解答考研政策、备考策略、择校建议
2. 课业辅导: 讲解高数、英语、专业课题目
3. 情绪疏导: 识别用户情绪,给予鼓励和心理支持
4. 信息推荐: 基于用户目标推荐院校、专业、资讯

你的性格特点:
- 专业严谨: 提供准确的考研信息和学术指导
- 温暖共情: 理解考研压力,给予情感支持
- 积极乐观: 传递正能量,帮助用户建立信心
- 简洁清晰: 回答条理分明,易于理解

注意事项:
- 当用户表现出焦虑、压力、emo情绪时,优先给予情感支持
- 讲解题目时要循序渐进,从基础概念到解题思路
- 推荐院校时要考虑用户的实际情况和目标
- 保持专业性,不要给出不确定的信息
```

---

## 工具方法实现

### 1. analyzeTargetUniversity
- 解析用户背景(985/211/普通本科)
- 查询匹配的院校
- 返回推荐列表(最多5所)
- 包含院校特点说明

### 2. getScoreLine
- 查询major表
- 按年份倒序
- 返回近3年分数线
- 包含学位类型、学习方式、招生人数

### 3. getGraduationNews
- 查询最新简章(2条)
- 查询最新调剂信息(2条)
- 返回资讯摘要
- 包含发布时间

### 4. getUserTarget
- 查询用户表
- 返回目标院校和专业
- 提示用户设置目标

---

## 前端UI设计

### 消息气泡
- AI消息: 左侧,白色背景,灰色边框
- 用户消息: 右侧,蓝色背景,白色文字
- 头像: AI🤖, 用户👤

### 快捷问题
- 如何选择考研院校?
- 考研英语怎么复习?
- 高数极限怎么求?
- 我感觉压力好大...

### 交互动画
- 加载动画: "思考中..."闪烁效果
- 消息滚动: 自动滚动到最新消息
- 发送按钮: 禁用状态灰色

---

## 环境变量配置

### Windows (CMD)
```cmd
set ARK_API_KEY=your_api_key_here
set ARK_ENDPOINT_ID=ep-20250115-xxxxx
```

### Windows (PowerShell)
```powershell
$env:ARK_API_KEY="your_api_key_here"
$env:ARK_ENDPOINT_ID="ep-20250115-xxxxx"
```

### Linux/Mac
```bash
export ARK_API_KEY=your_api_key_here
export ARK_ENDPOINT_ID=ep-20250115-xxxxx
```

---

## 测试建议

### 功能测试
1. 测试基础对话
   - 发送"你好"
   - 发送"考研是什么?"

2. 测试工具调用
   - 发送"我是985学生,推荐一些院校"
   - 发送"清华大学计算机专业分数线"
   - 发送"最新考研资讯"

3. 测试课业辅导
   - 发送"高数极限怎么求?"
   - 发送"考研英语怎么复习?"

4. 测试情绪疏导
   - 发送"我感觉压力好大"
   - 发送"我想放弃了"
   - 发送"好焦虑啊"

5. 测试多轮对话
   - 连续发送多条消息
   - 验证上下文保持

6. 测试清除历史
   - 点击清除按钮
   - 验证会话重置

### 性能测试
- 响应时间 < 5秒
- 并发用户隔离正确
- 会话历史正确保存

---

## 常见问题

### Q1: API Key从哪里获取?
A: 登录火山引擎控制台,在API管理页面创建API Key。

### Q2: Endpoint ID是什么?
A: 火山引擎模型的接入点ID,在模型管理页面可以找到。

### Q3: 如何调试AI对话?
A: 查看后端日志,LangChain4j会输出请求和响应详情。

### Q4: 工具调用不生效?
A: 检查@Tool注解是否正确,参数描述是否清晰。

### Q5: 响应太慢怎么办?
A: 调整temperature和max-tokens参数,或升级模型。

---

## 文件清单

### 后端文件(5个)
- `pom.xml` (更新)
- `application.properties` (更新)
- `config/AiConfig.java` (新增)
- `service/GradMateTools.java` (新增)
- `service/GradMateAgentService.java` (新增)
- `controller/AiController.java` (新增)

### 前端文件(3个)
- `pages/ai/chat.vue` (新增)
- `pages/index/index.vue` (更新)
- `pages.json` (更新)

---

## 下一步计划

完成阶段6后,可以:
- **优化AI回复**: 添加打字机效果
- **增强工具**: 添加更多工具方法
- **数据分析**: 统计AI使用情况
- **阶段7**: 管理后台模块(可选)

---

**阶段6开发完成!** 🤖

AI智能模块已实现,具备考研问答、课业辅导、情绪疏导、智能推荐等核心能力。
