# ToDo清单新字段简单测试
$baseUrl = "http://localhost:9090/api"
$userId = 1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ToDo清单新字段测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 测试获取今日任务
Write-Host "1. 获取今日任务列表..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/study/task/today?userId=$userId" -Method Get
    Write-Host "返回任务数量: $($response.data.Count)" -ForegroundColor Green
    
    if ($response.data.Count -gt 0) {
        $task = $response.data[0]
        Write-Host "第一个任务详情:" -ForegroundColor Green
        Write-Host "  ID: $($task.id)"
        Write-Host "  内容: $($task.taskContent)"
        Write-Host "  状态: $($task.status)"
        Write-Host "  标签: $($task.tags)"
        Write-Host "  预计时长: $($task.estimatedDuration)分钟"
        Write-Host "  实际时长: $($task.actualDuration)分钟"
        Write-Host "  重复类型: $($task.repeatType)"
        Write-Host "  描述: $($task.description)"
    }
} catch {
    Write-Host "错误: $_" -ForegroundColor Red
}
Write-Host ""

# 2. 测试创建带新字段的任务
Write-Host "2. 创建带新字段的任务..." -ForegroundColor Yellow
$newTask = @{
    userId = $userId
    taskContent = "测试新字段任务"
    taskDate = (Get-Date -Format "yyyy-MM-dd")
    priority = 2
    estimatedDuration = 30
    tags = "学习,考研"
    description = "这是一个测试任务，用于验证新字段功能"
    repeatType = "none"
    sortOrder = 1
} | ConvertTo-Json

try {
    $createResponse = Invoke-RestMethod -Uri "$baseUrl/study/task" -Method Post -Body $newTask -ContentType "application/json"
    if ($createResponse.code -eq 200) {
        Write-Host "任务创建成功！" -ForegroundColor Green
        Write-Host "  任务ID: $($createResponse.data.id)"
        Write-Host "  状态: $($createResponse.data.status)"
        Write-Host "  标签: $($createResponse.data.tags)"
    } else {
        Write-Host "任务创建失败: $($createResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "错误: $_" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
