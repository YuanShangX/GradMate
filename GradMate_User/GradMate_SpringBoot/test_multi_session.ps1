# 多会话管理功能测试脚本

$baseUrl = "http://localhost:9090/api"
$userId = 1

Write-Host "========================================"
Write-Host "多会话管理功能测试"
Write-Host "========================================"
Write-Host ""

# 测试1: 创建第一个会话
Write-Host "测试1: 创建第一个会话"
$body1 = @{
    userId = $userId
} | ConvertTo-Json

$response1 = Invoke-RestMethod -Uri "$baseUrl/ai/session/create" -Method Post -Body $body1 -ContentType "application/json"
Write-Host "创建成功"
Write-Host "  会话ID: $($response1.data.sessionId)"
Write-Host "  标题: $($response1.data.title)"
$session1Id = $response1.data.sessionId
Write-Host ""

# 测试2: 在第一个会话中发送消息
Write-Host "测试2: 在第一个会话中发送消息"
$chatBody1 = @{
    userId = $userId
    sessionId = $session1Id
    message = "如何选择考研院校?"
} | ConvertTo-Json

$chatResponse1 = Invoke-RestMethod -Uri "$baseUrl/ai/chat" -Method Post -Body $chatBody1 -ContentType "application/json"
Write-Host "发送成功"
$preview = $chatResponse1.data.Substring(0, [Math]::Min(50, $chatResponse1.data.Length))
Write-Host "  AI回复: $preview..."
Write-Host ""

# 测试3: 创建第二个会话
Write-Host "测试3: 创建第二个会话"
$response2 = Invoke-RestMethod -Uri "$baseUrl/ai/session/create" -Method Post -Body $body1 -ContentType "application/json"
Write-Host "创建成功"
Write-Host "  会话ID: $($response2.data.sessionId)"
$session2Id = $response2.data.sessionId
Write-Host ""

# 测试4: 在第二个会话中发送消息
Write-Host "测试4: 在第二个会话中发送消息"
$chatBody2 = @{
    userId = $userId
    sessionId = $session2Id
    message = "考研英语怎么复习?"
} | ConvertTo-Json

$chatResponse2 = Invoke-RestMethod -Uri "$baseUrl/ai/chat" -Method Post -Body $chatBody2 -ContentType "application/json"
Write-Host "发送成功"
$preview2 = $chatResponse2.data.Substring(0, [Math]::Min(50, $chatResponse2.data.Length))
Write-Host "  AI回复: $preview2..."
Write-Host ""

# 测试5: 获取会话列表
Write-Host "测试5: 获取会话列表"
$sessionList = Invoke-RestMethod -Uri "$baseUrl/ai/session/list?userId=$userId" -Method Get
Write-Host "获取成功，共 $($sessionList.data.Count) 个会话"
foreach ($session in $sessionList.data) {
    Write-Host "  - [$($session.title)] 消息数: $($session.messageCount)"
}
Write-Host ""

# 测试6: 重命名第一个会话
Write-Host "测试6: 重命名第一个会话"
$renameBody = @{
    title = "院校选择咨询"
} | ConvertTo-Json

$renameResponse = Invoke-RestMethod -Uri "$baseUrl/ai/session/$session1Id/title" -Method Put -Body $renameBody -ContentType "application/json"
Write-Host "重命名成功: $($renameResponse.data)"
Write-Host ""

# 测试7: 验证重命名
Write-Host "测试7: 验证重命名后的会话列表"
$sessionList2 = Invoke-RestMethod -Uri "$baseUrl/ai/session/list?userId=$userId" -Method Get
Write-Host "获取成功"
foreach ($session in $sessionList2.data) {
    Write-Host "  - [$($session.title)] 消息数: $($session.messageCount)"
}
Write-Host ""

# 测试8: 删除第二个会话
Write-Host "测试8: 删除第二个会话"
$deleteResponse = Invoke-RestMethod -Uri "$baseUrl/ai/session/$session2Id" -Method Delete
Write-Host "删除成功: $($deleteResponse.data)"
Write-Host ""

# 测试9: 最终会话列表
Write-Host "测试9: 最终会话列表"
$sessionList3 = Invoke-RestMethod -Uri "$baseUrl/ai/session/list?userId=$userId" -Method Get
Write-Host "获取成功，剩余 $($sessionList3.data.Count) 个会话"
foreach ($session in $sessionList3.data) {
    Write-Host "  - [$($session.title)] 消息数: $($session.messageCount)"
}
Write-Host ""

Write-Host "========================================"
Write-Host "测试完成！"
Write-Host "========================================"
