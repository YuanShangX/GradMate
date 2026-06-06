# 测试任务API
$baseUrl = "http://localhost:9090/api"

Write-Host "=== 测试任务API ===" -ForegroundColor Green

# 1. 创建任务
Write-Host "`n1. 测试创建任务..." -ForegroundColor Yellow
$createBody = @{
    userId = 1
    taskContent = "测试任务"
    taskDate = "2026-02-20"
    priority = 2
    estimatedDuration = 30
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/study/task" -Method Post -Body $createBody -ContentType "application/json"
    Write-Host "创建任务成功:" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 3)
    $taskId = $response.data.id
} catch {
    Write-Host "创建任务失败: $_" -ForegroundColor Red
    exit 1
}

# 2. 获取今日任务
Write-Host "`n2. 测试获取今日任务..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/study/task/today?userId=1" -Method Get
    Write-Host "获取任务成功:" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "获取任务失败: $_" -ForegroundColor Red
}

# 3. 更新任务
if ($taskId) {
    Write-Host "`n3. 测试更新任务..." -ForegroundColor Yellow
    $updateBody = @{
        taskContent = "更新后的任务"
        priority = 3
        estimatedDuration = 45
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/study/task/$taskId" -Method Put -Body $updateBody -ContentType "application/json"
        Write-Host "更新任务成功:" -ForegroundColor Green
        Write-Host ($response | ConvertTo-Json -Depth 3)
    } catch {
        Write-Host "更新任务失败: $_" -ForegroundColor Red
    }
}

# 4. 再次获取任务验证更新
Write-Host "`n4. 验证更新结果..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/study/task/today?userId=1" -Method Get
    Write-Host "任务列表:" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "获取任务失败: $_" -ForegroundColor Red
}

# 5. 删除任务
if ($taskId) {
    Write-Host "`n5. 测试删除任务..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/study/task/$taskId" -Method Delete
        Write-Host "删除任务成功:" -ForegroundColor Green
        Write-Host ($response | ConvertTo-Json -Depth 3)
    } catch {
        Write-Host "删除任务失败: $_" -ForegroundColor Red
    }
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
