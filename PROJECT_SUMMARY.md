# 打牌记账工具 - 项目总览

## ✅ 已完成功能

### 服务端 (Java 17)
- ✅ 用户管理系统
  - 创建用户（用户名唯一性验证）
  - 查询用户
  - 用户信息持久化存储
  
- ✅ 房间管理系统
  - 创建房间
  - 房间列表
  - 房间搜索（按名称）
  - 加入房间
  - 房间详情查询
  
- ✅ 转账系统
  - 创建转账记录
  - 转账历史查询
  - 转账详情（含用户名）
  - 自动计算用户总分
  
- ✅ 数据持久化
  - 本地文件存储（JSON格式）
  - 用户数据保存
  - 房间数据保存
  
- ✅ REST API
  - 完整的RESTful接口
  - CORS支持
  - 错误处理
  - 健康检查端点

### 客户端 (Flutter)
- ✅ 登录注册页面
  - 用户创建/登录
  - 服务器地址配置
  - 本地用户信息保存
  
- ✅ 房间列表页面
  - 显示所有房间
  - 房间搜索功能
  - 创建新房间
  - 加入房间
  
- ✅ 房间详情页面
  - 显示所有成员
  - 实时显示每人总分
  - 转账操作
  - 快捷转账按钮（1, 5, 10, 20, 50, 100分）
  - 自定义金额输入
  - 转账历史记录
  - 自动刷新（每5秒）
  
- ✅ 语音播报
  - 转账时自动语音播报
  - 中文语音支持
  
- ✅ 本地存储
  - 用户信息持久化
  - 服务器地址保存

## 📁 项目文件清单

### 服务端文件 (server/)
```
server/
├── pom.xml                                    # Maven配置文件
├── .gitignore                                 # Git忽略文件
└── src/main/java/com/cardscore/
    ├── Application.java                       # 应用主入口
    ├── controller/
    │   └── ApiController.java                 # REST API控制器
    ├── model/
    │   ├── User.java                          # 用户实体
    │   ├── Room.java                          # 房间实体
    │   ├── Transaction.java                   # 转账记录实体
    │   └── ApiResponse.java                   # API响应包装类
    ├── service/
    │   └── CardScoreService.java              # 业务逻辑服务
    └── storage/
        └── LocalStorage.java                  # 本地文件存储管理
```

### 客户端文件 (client/)
```
client/
├── pubspec.yaml                               # Flutter项目配置
├── analysis_options.yaml                      # 代码分析配置
├── .gitignore                                 # Git忽略文件
└── lib/
    ├── main.dart                              # 应用入口
    ├── models/                                # 数据模型
    │   ├── user.dart                          # 用户模型
    │   ├── user.g.dart                        # 用户模型序列化代码
    │   ├── room.dart                          # 房间模型
    │   ├── room.g.dart                        # 房间模型序列化代码
    │   ├── transaction.dart                   # 转账模型
    │   ├── transaction.g.dart                 # 转账模型序列化代码
    │   └── api_response.dart                  # API响应模型
    ├── screens/                               # 页面
    │   ├── login_screen.dart                  # 登录页面
    │   ├── room_list_screen.dart              # 房间列表页面
    │   └── room_detail_screen.dart            # 房间详情页面
    └── services/                              # 服务层
        ├── api_service.dart                   # API调用服务
        ├── tts_service.dart                   # 语音播报服务
        └── storage_service.dart               # 本地存储服务
```

### 文档和脚本
```
card-score/
├── Readme.md                                  # 中文主文档
├── README_EN.md                               # 英文文档
├── BUILD_AND_RUN.md                           # 构建运行指南
├── CONFIG.md                                  # 配置说明
├── PROJECT_SUMMARY.md                         # 项目总览（本文件）
├── start-server.bat                           # Windows服务端启动脚本
├── start-server.sh                            # Linux/Mac服务端启动脚本
├── start-client.bat                           # Windows客户端启动脚本
└── start-client.sh                            # Linux/Mac客户端启动脚本
```

## 🎯 核心功能实现

### 1. 用户管理
- **后端**: `CardScoreService.createUser()` - 创建用户并验证唯一性
- **前端**: `LoginScreen` - 用户登录/注册界面
- **存储**: `LocalStorage.saveUsers()` - JSON文件持久化

### 2. 房间管理
- **后端**: `CardScoreService.createRoom()` / `searchRooms()` - 房间创建和搜索
- **前端**: `RoomListScreen` - 房间列表和搜索
- **存储**: `LocalStorage.saveRooms()` - JSON文件持久化

### 3. 转账系统
- **后端**: `CardScoreService.createTransaction()` - 创建转账记录
- **前端**: `RoomDetailScreen._showTransferDialog()` - 转账对话框
- **计算**: `Room.calculateScores()` - 自动计算总分

### 4. 语音播报
- **服务**: `TtsService.announceTransaction()` - 中文语音播报
- **触发**: 转账成功后自动播报

### 5. 实时更新
- **机制**: Timer定时器（每5秒）
- **实现**: `RoomDetailScreen._refreshTimer` - 自动刷新房间数据

## 🔧 技术亮点

1. **本地文件存储**: 无需额外数据库，使用JSON文件存储数据
2. **RESTful API**: 标准的REST接口设计
3. **响应式UI**: Flutter Material Design
4. **实时更新**: 定时器自动刷新
5. **语音播报**: 提升用户体验
6. **跨平台**: 支持Android、iOS、Web
7. **并发安全**: ConcurrentHashMap保证线程安全
8. **代码生成**: JSON序列化自动生成

## 📊 数据流

```
用户操作 → Flutter UI → API Service → HTTP请求 
    → Spark Java → CardScoreService → LocalStorage 
    → JSON文件 → 响应 → Flutter UI 更新
```

## 🚀 快速启动命令

### Windows
```bash
# 启动服务端
start-server.bat

# 启动客户端（新终端）
start-client.bat
```

### Linux/Mac
```bash
# 启动服务端
chmod +x start-server.sh
./start-server.sh

# 启动客户端（新终端）
chmod +x start-client.sh
./start-client.sh
```

## 📝 API端点总结

| 方法 | 端点 | 功能 |
|------|------|------|
| POST | /api/users | 创建用户 |
| GET | /api/users | 获取所有用户 |
| GET | /api/users/:id | 获取指定用户 |
| POST | /api/rooms | 创建房间 |
| GET | /api/rooms | 获取所有房间 |
| GET | /api/rooms/search?keyword=xxx | 搜索房间 |
| GET | /api/rooms/:id | 获取指定房间 |
| POST | /api/rooms/:id/join | 加入房间 |
| GET | /api/rooms/:id/detail | 获取房间详情 |
| POST | /api/transactions | 创建转账记录 |
| GET | /api/transactions/room/:roomId | 获取房间转账记录 |
| GET | /api/transactions/room/:roomId/details | 获取转账详情 |
| GET | /health | 健康检查 |

## 🎨 UI页面流程

```
LoginScreen (登录页)
    ↓
RoomListScreen (房间列表)
    ↓
RoomDetailScreen (房间详情)
    ├── 成员列表 + 总分
    ├── 转账按钮
    └── 转账历史
```

## 📱 使用场景

1. 打麻将/扑克牌时记分
2. 朋友聚会AA记账
3. 小组活动费用跟踪
4. 任何需要多人记账的场景

## 🔐 数据安全

- 数据保存在服务端本地文件
- 客户端仅保存当前用户信息
- 无密码系统（适合内部使用）
- 可在此基础上添加认证系统

## 🌟 可扩展功能建议

1. 添加用户头像
2. 支持房间密码
3. 添加历史记录导出（Excel/PDF）
4. 支持撤销转账
5. 添加统计图表
6. 支持多币种
7. 添加用户认证
8. WebSocket实时推送
9. 云端存储同步
10. 支持群组聊天

## 📄 许可证

MIT License - 可自由使用、修改和分发

---

**项目创建时间**: 2025-12-18
**版本**: 1.0.0
**状态**: ✅ 完成并可用
