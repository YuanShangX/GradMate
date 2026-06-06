# 阶段6：AI智能模块 - 快速启动指南

## 🚀 快速开始

### 1. 设置环境变量

#### Windows CMD
```cmd
set ARK_API_KEY=your_volcengine_api_key_here
```

#### Windows PowerShell
```powershell
$env:ARK_API_KEY="your_volcengine_api_key_here"
```

#### Linux/Mac
```bash
export ARK_API_KEY=your_volcengine_api_key_here
```

### 2. 重启后端
```bash
cd GradMate_SpringBoot
mvn spring-boot:run
```

### 3. 测试AI功能
- 打开小程序
- 点击首页"AI智囊"卡片
- 发送消息测试

---

## 📝 测试用例

### 基础对话
```
用户: 你好
AI: 你好!我是GradMate考研助手...
```

### 考研咨询
```
用户: 如何选择考研院校?
AI: 选择考研院校需要考虑以下几个因素...
```

### 课业辅导
```
用户: 高数极限怎么求?
AI: 求极限是高等数学的基础内容...
```

### 情绪疏导
```
用户: 我感觉压力好大
AI: 我理解你现在的感受,考研确实不容易...
```

### 多轮对话
```
用户: 我想考计算机专业
AI: 计算机专业是热门专业...
用户: 有什么推荐的学校吗?
AI: 基于你刚才提到的计算机专业... (记住上下文)
```

---

## ⚙️ 配置说明

### API Key 获取
1. 登录火山引擎控制台: https://console.volcengine.com/
2. 进入"模型推理"服务
3. 创建API Key
4. 复制API Key并设置到环境变量

### 模型配置
- 模型: `doubao-1.5-pro-32k-250115`
- 温度: `0.7` (创造性和准确性平衡)
- 最大Token: `2000` (约1500字)
- 会话记忆: `10轮` (20条消息)

---

## 🔧 故障排查

### 问题1: 编译错误 "package dev.langchain4j.model.volcengine does not exist"
**解决**: 已修复,使用火山引擎官方SDK替代

### 问题2: 启动报错 "ARK_API_KEY not found"
**解决**: 设置环境变量后重启IDE和终端

### 问题3: AI回复 "抱歉,我现在有点累了"
**原因**: API调用失败
**检查**:
- API Key是否正确
- 网络是否正常
- 火山引擎账户是否有余额
- 查看后端日志获取详细错误

### 问题4: 响应太慢
**优化**:
- 减少 `max-tokens` 参数
- 调整 `temperature` 参数
- 检查网络延迟

---

## 📊 API接口

### 1. AI对话
```http
POST http://localhost:9090/api/ai/chat
Content-Type: application/json

{
  "userId": 1,
  "message": "你好"
}
```

### 2. 清除历史
```http
DELETE http://localhost:9090/api/ai/history?userId=1
```

### 3. 获取会话信息
```http
GET http://localhost:9090/api/ai/session?userId=1
```

---

## 📚 相关文档

- `README_阶段6_AI智能模块开发规划.md` - 开发规划
- `README_阶段6_完成说明.md` - 完成说明
- `README_阶段6_问题修复.md` - 问题修复详情

---

## ✅ 验收清单

- [ ] 环境变量已设置
- [ ] 后端启动成功
- [ ] 基础对话正常
- [ ] 多轮对话上下文正确
- [ ] 情绪疏导有效
- [ ] 清除历史功能正常
- [ ] 响应时间 < 5秒

---

**祝测试顺利!** 🎉
