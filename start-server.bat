@echo off
echo ========================================
echo 打牌记账工具 - 服务端启动脚本
echo ========================================
echo.

cd server

if not exist "target\card-score-server-1.0.0-jar-with-dependencies.jar" (
    echo 未找到服务端jar文件，正在构建...
    echo.
    call mvn clean package
    if errorlevel 1 (
        echo.
        echo 构建失败！请检查Maven是否已安装。
        pause
        exit /b 1
    )
)

echo.
echo 正在启动服务端...
echo 默认端口: 8080
echo 访问 http://localhost:8080/health 检查服务状态
echo.
echo 按 Ctrl+C 停止服务器
echo ========================================
echo.

java -jar target\card-score-server-1.0.0-jar-with-dependencies.jar

pause
