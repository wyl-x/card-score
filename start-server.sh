#!/bin/bash

echo "========================================"
echo "打牌记账工具 - 服务端启动脚本"
echo "========================================"
echo ""

cd server

if [ ! -f "target/card-score-server-1.0.0-jar-with-dependencies.jar" ]; then
    echo "未找到服务端jar文件，正在构建..."
    echo ""
    mvn clean package
    if [ $? -ne 0 ]; then
        echo ""
        echo "构建失败！请检查Maven是否已安装。"
        exit 1
    fi
fi

echo ""
echo "正在启动服务端..."
echo "默认端口: 8080"
echo "访问 http://localhost:8080/health 检查服务状态"
echo ""
echo "按 Ctrl+C 停止服务器"
echo "========================================"
echo ""

java -jar target/card-score-server-1.0.0-jar-with-dependencies.jar
