@echo off
echo ========================================
echo 打牌记账工具 - 客户端启动脚本
echo ========================================
echo.

cd client

echo 检查依赖...
call flutter pub get

echo.
echo 生成JSON序列化代码...
call flutter pub run build_runner build --delete-conflicting-outputs

echo.
echo 正在启动客户端...
echo ========================================
echo.

flutter run

pause
