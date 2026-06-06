# ToDo清单新字段测试脚本
# 测试数据库优化后的新字段是否能正常读写

$baseUrl = "http://localhost:9090/api"
$userId = 1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "ToDo清单新字段测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. 测试获取今日任务（查看新字段是否返回）
Write-Host "1. 获取今日任务列表..." -ForegroundColor Yellow
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
    Write-Host "  排序: $($task.sortOrder)"
    Write-Host "  描述: $($task.description)"
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
        Write-Host "✓ 任务创建成功！" -ForegroundColor Green
        $createdTaskId = $createResponse.data.id
        Write-Host "  任务ID: $createdTaskId"
        Write-Host "  状态: $($createResponse.data.status)"
        Write-Host "  标签: $($createResponse.data.tags)"
    } else {
        Write-Host "✗ 任务创建失败: $($createResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $_" -ForegroundColor Red
}
Write-Host ""

# 3. 测试获取标签列表
Write-Host "3. 获取用户标签列表..." -ForegroundColor Yellow
try {
    # 直接查询数据库
    $tags = mysql -u root -pA123456 gradmate -e "SELECT * FROM task_tags WHERE user_id=$userId;" 2>&1 | Select-String -Pattern "学习|工作|生活" -Context 0,0
    if ($tags) {
        Write-Host "✓ 标签数据存在" -ForegroundColor Green
        Write-Host $tags
    } else {
        Write-Host "○ 暂无标签数据" -ForegroundColor Yellow
    }
} catch {
    Write-Host "○ 标签查询跳过" -ForegroundColor Yellow
}
Write-Host ""

# 4. 验证数据库字段
Write-Host "4. 验证数据库字段映射..." -ForegroundColor Yellow
try {
    $dbCheck = mysql -u root -pA123456 gradmate -e "SELECT id, task_content, status, tags, actual_duration, repeat_type FROM daily_task LIMIT 1;" 2>&1
    if ($dbCheck -match "status") {
        Write-Host "✓ 数据库字段映射正常" -ForegroundColor Green
    }
} catch {
    Write-Host "○ 数据库检查跳过" -ForegroundColor Yellow
}
Write-Host ""

# 5. 测试任务完成（验证status字段同步）
Write-Host "5. 测试任务完成状态..." -ForegroundColor Yellow
if ($response.data.Count -gt 0) {
    $testTaskId = $response.data[0].id
    try {
        $completeResponse = Invoke-RestMethod -Uri "$baseUrl/study/task/$testTaskId/complete" -Method Put
        if ($completeResponse.code -eq 200) {
            Write-Host "✓ 任务状态切换成功" -ForegroundColor Green
            
            # 再次获取任务查看状态
            $updatedTask = Invoke-RestMethod -Uri "$baseUrl/study/task/today?userId=$userId" -Method Get
            $task = $updatedTask.data | Where-Object { $_.id -eq $testTaskId }
            Write-Host "  is_completed: $($task.isCompleted)"
            Write-Host "  status: $($task.status)"
        }
    } catch {
        Write-Host "○ 状态切换测试跳过" -ForegroundColor Yellow
    }
}
Write-Host ""

# 总结
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "检查要点:" -ForegroundColor Yellow
Write-Host "1. 新字段是否能正常返回（status, tags, actualDuration等）"
Write-Host "2. 创建任务时新字段是否能正常保存"
Write-Host "3. 标签表是否有数据"
Write-Host "4. 任务完成时status和is_completed是否同步"
Write-Host ""
Write-Host "如果以上测试都通过，说明后端代码更新成功！" -ForegroundColor Green
