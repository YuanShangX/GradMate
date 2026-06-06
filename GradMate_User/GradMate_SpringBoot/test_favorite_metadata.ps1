# 测试专业收藏元数据功能
# 测试添加收藏、查询收藏、验证 metadata 字段

$baseUrl = "http://localhost:9090/api"
$userId = 1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "专业收藏元数据功能测试" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 测试1: 添加专业收藏
Write-Host "测试1: 添加专业收藏" -ForegroundColor Yellow
$addBody = @{
    userId = $userId
    schoolName = "清华大学"
    majorCode = "081200"
    majorName = "计算机科学与技术"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/favorite/addMajor" -Method Post -Body $addBody -ContentType "application/json"
    Write-Host "添加收藏结果: $($response.message)" -ForegroundColor Green
    Write-Host "返回数据: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
} catch {
    Write-Host "添加收藏失败: $_" -ForegroundColor Red
}

Write-Host ""

# 测试2: 查询收藏列表
Write-Host "测试2: 查询专业收藏列表" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/favorite/list?userId=$userId&type=major&pageNum=1&pageSize=10" -Method Get
    Write-Host "查询成功，共 $($response.data.records.Count) 条记录" -ForegroundColor Green
    
    if ($response.data.records.Count -gt 0) {
        Write-Host "`n收藏列表详情:" -ForegroundColor Cyan
        foreach ($item in $response.data.records) {
            Write-Host "  - ID: $($item.id)" -ForegroundColor White
            Write-Host "    名称: $($item.targetName)" -ForegroundColor White
            Write-Host "    元数据: $($item.metadata)" -ForegroundColor Yellow
            
            # 解析 metadata
            if ($item.metadata) {
                try {
                    $metadata = $item.metadata | ConvertFrom-Json
                    Write-Host "    学校名称: $($metadata.schoolName)" -ForegroundColor Green
                    Write-Host "    专业代码: $($metadata.majorCode)" -ForegroundColor Green
                    Write-Host "    专业名称: $($metadata.majorName)" -ForegroundColor Green
                } catch {
                    Write-Host "    元数据解析失败" -ForegroundColor Red
                }
            } else {
                Write-Host "    元数据为空" -ForegroundColor Red
            }
            Write-Host ""
        }
    }
} catch {
    Write-Host "查询失败: $_" -ForegroundColor Red
}

Write-Host ""

# 测试3: 检查收藏状态
Write-Host "测试3: 检查收藏状态" -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/favorite/checkMajor?userId=$userId&schoolName=清华大学&majorCode=081200" -Method Get
    Write-Host "收藏状态: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "检查失败: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
