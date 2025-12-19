# 打牌记账工具 - 构建和运行指南

## 环境要求

### 服务端
- Java 17 或更高版本
- Maven 3.6 或更高版本

### 客户端
- Flutter 3.0 或更高版本
- Dart 3.0 或更高版本

## 构建步骤

### 1. 构建服务端

进入服务端目录：
```bash
cd server
```

使用Maven构建：
```bash
mvn clean package
```

构建完成后，会在 `target` 目录生成可执行jar文件：
- `card-score-server-1.0.0-jar-with-dependencies.jar`

### 2. 准备客户端

进入客户端目录：
```bash
cd client
```

获取依赖：
```bash
flutter pub get
```

生成JSON序列化代码：
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

## 运行步骤

### 1. 启动服务端

在 `server` 目录下运行：
```bash
java -jar target/card-score-server-1.0.0-jar-with-dependencies.jar
```

或指定端口：
```bash
java -jar target/card-score-server-1.0.0-jar-with-dependencies.jar 9090
```

服务器启动后，会显示可用的API端点。

### 2. 运行客户端

#### 在模拟器/设备上运行
```bash
cd client
flutter run
```

#### 构建Android APK
```bash
flutter build apk
```
生成的APK文件位于 `build/app/outputs/flutter-apk/app-release.apk`

#### 构建iOS应用
```bash
flutter build ios
```

## 使用说明

### 1. 登录/注册
- 打开应用
- 设置服务器地址（如果服务器在其他设备，输入对应IP和端口）
- 输入用户名
- 点击"登录/注册"

### 2. 房间管理
- 在房间列表页，可以：
  - 查看所有房间
  - 搜索房间
  - 创建新房间（点击右下角"+"按钮）
  - 点击房间卡片加入房间

### 3. 转账操作
- 进入房间后，可以看到所有成员和他们的总分
- 点击其他成员的"转账"按钮
- 选择快捷金额或输入自定义金额
- 确认转账
- 系统会语音播报转账信息

### 4. 查看记录
- 房间详情页下方显示所有转账记录
- 记录按时间倒序排列
- 显示转账人、收款人、金额和时间

## 数据存储

### 服务端
数据保存在服务端的 `data` 目录下：
- `users.json` - 用户数据
- `rooms.json` - 房间数据

### 客户端
- 当前用户信息保存在本地
- 服务器地址保存在本地
- 所有转账记录保存在服务端

## 注意事项

1. 确保服务端先启动再运行客户端
2. 客户端和服务端需要在同一网络下，或服务端有公网IP
3. 如果使用手机运行客户端连接电脑上的服务端：
   - 确保电脑和手机在同一WiFi
   - 服务器地址使用电脑的局域网IP，例如 `http://192.168.1.100:8080`
   - Windows可以用 `ipconfig` 查看IP
   - Mac/Linux可以用 `ifconfig` 或 `ip addr` 查看IP
4. 首次使用需要运行 `build_runner` 生成JSON序列化代码

## 故障排除

### 服务端无法启动
- 检查Java版本：`java -version`
- 检查端口是否被占用
- 查看错误日志

### 客户端连接失败
- 检查服务器地址是否正确
- 确保服务端已启动
- 检查网络连接
- 尝试使用 `http://服务器IP:8080/health` 测试连接

### 语音播报无法工作
- 检查设备音量
- 确保应用有麦克风权限（某些设备需要）
- iOS设备可能需要额外配置权限

## API文档

服务端提供以下REST API：

### 用户相关
- `POST /api/users` - 创建用户
- `GET /api/users` - 获取所有用户
- `GET /api/users/:id` - 获取指定用户

### 房间相关
- `POST /api/rooms` - 创建房间
- `GET /api/rooms` - 获取所有房间
- `GET /api/rooms/search?keyword=xxx` - 搜索房间
- `GET /api/rooms/:id` - 获取指定房间
- `POST /api/rooms/:id/join` - 加入房间
- `GET /api/rooms/:id/detail` - 获取房间详情

### 转账相关
- `POST /api/transactions` - 创建转账记录
- `GET /api/transactions/room/:roomId` - 获取房间转账记录
- `GET /api/transactions/room/:roomId/details` - 获取转账详情（含用户名）

### 健康检查
- `GET /health` - 服务健康检查
