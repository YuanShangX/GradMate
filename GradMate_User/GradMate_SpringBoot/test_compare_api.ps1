# 测试院校对比接口

Write-Host "测试院校对比接口..." -ForegroundColor Green

# 测试院校对比
$schoolBody = @{
    schoolIds = @(1, 2)
} | ConvertTo-Json

Write-Host "`n1. 测试院校对比 (POST /api/compare/schools)" -ForegroundColor Yellow
Write-Host "请求体: $schoolBody"

try {
    $response = Invoke-RestMethod -Uri "http://localhost:9090/api/compare/schools" `
        -Method Post `
        -Body $schoolBody `
        -ContentType "application/json" `
        -ErrorAction Stop
    
    Write-Host "响应成功:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode.value__)"
}

Write-Host "`n-----------------------------------`n"

# 测试专业对比
$majorBody = @{
    majorIds = @(1, 2)
} | ConvertTo-Json

Write-Host "2. 测试专业对比 (POST /api/compare/majors)" -ForegroundColor Yellow
Write-Host "请求体: $majorBody"

try {
    $response = Invoke-RestMethod -Uri "http://localhost:9090/api/compare/majors" `
        -Method Post `
        -Body $majorBody `
        -ContentType "application/json" `
        -ErrorAction Stop
    
    Write-Host "响应成功:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Host "请求失败: $_" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode.value__)"
}

Write-Host "`n测试完成!" -ForegroundColor Green
