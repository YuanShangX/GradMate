@echo off
chcp 65001 >nul
echo ========================================
echo 测试个人中心新增接口
echo ========================================
echo.

echo [测试1] 用户统计接口
echo URL: http://localhost:9090/api/user/stats/11
curl -s http://localhost:9090/api/user/stats/11
echo.
echo.

echo [测试2] 目标详情接口
echo URL: http://localhost:9090/api/user/target/detail/11
curl -s http://localhost:9090/api/user/target/detail/11
echo.
echo.

echo [测试3] 收藏统计接口(验证旧接口)
echo URL: http://localhost:9090/api/favorite/count?userId=11
curl -s http://localhost:9090/api/favorite/count?userId=11
echo.
echo.

echo ========================================
echo 测试完成
echo ========================================
pause
