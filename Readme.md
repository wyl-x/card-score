# 打牌记账工具

一个用于打牌记分和记账的工具，支持实时转账记录和语音播报。

## 主要功能

### 1. 用户管理
- ✅ 快速创建用户，只需要输入名称
- ✅ 用户信息持久存储在本地

### 2. 房间管理
- ✅ 房间列表展示
- ✅ 支持根据名称搜索房间
- ✅ 创建房间
- ✅ 加入房间

### 3. 转账系统
- ✅ 支持转账给其他用户
- ✅ 快捷转账按钮（1, 2, 3, 4, 5, 10）
- ✅ 手动输入自定义转账金额
- ✅ 每次转账发生时语音播报："谁向谁转了多少分"

### 4. 实时更新
- ✅ 房间详情实时显示转账记录
- ✅ 记录时间和转账详情
- ✅ 显示每人总得分
- ✅ 自动刷新（每3秒）

### 5. 数据持久化
- ✅ 用户退出房间后可继续加入房间
- ✅ 保留之前的转账记录
- ✅ 所有数据保存在本地文件

## 技术栈

### 服务端
- Java 17+
- Spark Java (轻量级REST框架)
- Gson (JSON处理)
- 本地文件存储

### 客户端
- Flutter 3.0+
- HTTP客户端
- Provider (状态管理)
- Flutter TTS (语音播报)
- Shared Preferences (本地存储)

## 快速开始

### 📖 文档导航
- **⚡ 新手必读**: [QUICKSTART.md](QUICKSTART.md) - 5分钟快速入门
- **📋 详细指南**: [BUILD_AND_RUN.md](BUILD_AND_RUN.md) - 完整构建和运行说明
- **⚙️ 配置说明**: [CONFIG.md](CONFIG.md) - 配置选项
- **📊 项目总览**: [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) - 技术架构和功能清单

### 简要步骤

#### 1. 构建服务端
```bash
cd server
mvn clean package
```

#### 2. 启动服务端
```bash
java -jar target/card-score-server-1.0.0-jar-with-dependencies.jar
```

#### 3. 运行客户端
```bash
cd client
flutter pub get
flutter pub run build_runner build
flutter run
```

## 项目结构

```
card-score/
├── server/                 # Java服务端
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/
│   │               └── cardscore/
│   │                   ├── Application.java      # 应用入口
│   │                   ├── controller/          # REST API控制器
│   │                   │   └── ApiController.java
│   │                   ├── model/               # 数据模型
│   │                   │   ├── User.java
│   │                   │   ├── Room.java
│   │                   │   ├── Transaction.java
│   │                   │   └── ApiResponse.java
│   │                   ├── service/             # 业务逻辑
│   │                   │   └── CardScoreService.java
│   │                   └── storage/             # 数据存储
│   │                       └── LocalStorage.java
│   └── pom.xml
│
├── client/                 # Flutter客户端
│   ├── lib/
│   │   ├── main.dart                  # 应用入口
│   │   ├── models/                    # 数据模型
│   │   │   ├── user.dart
│   │   │   ├── room.dart
│   │   │   ├── transaction.dart
│   │   │   └── api_response.dart
│   │   ├── screens/                   # 页面
│   │   │   ├── login_screen.dart      # 登录页
│   │   │   ├── room_list_screen.dart  # 房间列表页
│   │   │   └── room_detail_screen.dart # 房间详情页
│   │   └── services/                  # 服务层
│   │       ├── api_service.dart       # API调用
│   │       ├── tts_service.dart       # 语音播报
│   │       └── storage_service.dart   # 本地存储
│   └── pubspec.yaml
│
├── Readme.md              # 中文说明文档
├── README_EN.md           # 英文说明文档
├── BUILD_AND_RUN.md       # 构建运行指南
└── CONFIG.md              # 配置说明
```

## 使用说明

### 1. 登录/注册
- 打开应用
- 设置服务器地址（默认: http://localhost:8080）
- 输入用户名
- 点击"登录/注册"

### 2. 房间管理
- 查看所有房间
- 使用搜索功能查找房间
- 点击"+"按钮创建新房间
- 点击房间卡片加入房间

### 3. 转账操作
- 进入房间后查看所有成员和总分
- 点击其他成员的"转账"按钮
- 选择快捷金额或输入自定义金额
- 确认转账，系统自动语音播报

### 4. 查看记录
- 房间详情页显示所有转账记录
- 记录按时间倒序排列
- 显示转账人、收款人、金额和时间
- 自动计算每人总分

## API文档

服务端提供REST API，详细文档请查看 [BUILD_AND_RUN.md](BUILD_AND_RUN.md)

主要端点：
- `POST /api/users` - 创建用户
- `GET /api/rooms` - 获取房间列表
- `POST /api/rooms` - 创建房间
- `POST /api/rooms/:id/join` - 加入房间
- `POST /api/transactions` - 创建转账记录
- `GET /api/transactions/room/:roomId/details` - 获取转账详情

## 注意事项

1. 确保服务端先启动再运行客户端
2. 客户端和服务端需要在同一网络或服务端有公网访问
3. 手机连接电脑服务端时，使用电脑局域网IP地址
4. 首次运行需要执行 `flutter pub run build_runner build` 生成代码

## 许可证

MIT License