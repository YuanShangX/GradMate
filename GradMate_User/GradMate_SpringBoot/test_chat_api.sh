#!/bin/bash

# 研小伴API测试脚本

echo "=========================================="
echo "研小伴功能测试"
echo "=========================================="
echo ""

BASE_URL="http://localhost:9090/api/ai"

# 测试1：发送第一条消息
echo "测试1：发送第一条消息"
echo "----------------------------------------"
curl -X POST ${BASE_URL}/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "你好，研小伴！"
  }' | jq .

echo ""
echo ""

# 等待2秒
sleep 2

# 测试2：发送第二条消息（测试上下文）
echo "测试2：发送第二条消息（测试上下文）"
echo "----------------------------------------"
curl -X POST ${BASE_URL}/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "我想了解考研院校推荐"
  }' | jq .

echo ""
echo ""

# 测试3：获取会话信息
echo "测试3：获取会话信息"
echo "----------------------------------------"
curl -X GET "${BASE_URL}/session?userId=1" | jq .

echo ""
echo ""

# 测试4：清除历史
echo "测试4：清除历史"
echo "----------------------------------------"
curl -X DELETE "${BASE_URL}/history?userId=1" | jq .

echo ""
echo ""

# 测试5：再次获取会话信息（应该为空）
echo "测试5：再次获取会话信息（应该为空）"
echo "----------------------------------------"
curl -X GET "${BASE_URL}/session?userId=1" | jq .

echo ""
echo ""
echo "=========================================="
echo "测试完成"
echo "=========================================="
