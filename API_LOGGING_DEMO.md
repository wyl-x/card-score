# API 日志功能说明

## 已实现功能

所有API接口现在都会记录详细的调用日志，包括：

### 日志内容
1. **请求时间** - 格式：`yyyy-MM-dd HH:mm:ss.SSS`
2. **HTTP方法** - GET/POST等
3. **请求路径** - 完整的API路径
4. **请求参数** - POST请求的body内容
5. **响应状态码** - 200/201/400/404等
6. **响应内容** - API返回的JSON（超过500字符会截断）

### 日志格式示例

**请求日志：**
```
[2025-12-19 10:20:30.123] POST /api/users | Body: {"name":"张三"}
```

**响应日志：**
```
[2025-12-19 10:20:30.456] POST /api/users | Status: 201 | Response: {"success":true,"data":{"id":"abc123","name":"张三","createdAt":1734582030000}}
```

## 涵盖的接口

### User API
- `POST /api/users` - 创建用户
- `GET /api/users` - 获取所有用户
- `GET /api/users/:id` - 获取指定用户

### Room API
- `POST /api/rooms` - 创建房间
- `GET /api/rooms` - 获取所有房间
- `GET /api/rooms/search?keyword=...` - 搜索房间
- `GET /api/rooms/:id` - 获取指定房间
- `POST /api/rooms/:id/join` - 加入房间
- `POST /api/rooms/:id/leave` - 离开房间
- `GET /api/rooms/:id/detail` - 获取房间详情

### Transaction API
- `POST /api/transactions` - 创建转账记录
- `GET /api/transactions/room/:roomId` - 获取房间转账记录
- `GET /api/transactions/room/:roomId/details` - 获取转账详情

## 测试方法

1. 启动服务器：
```bash
java -jar server/target/card-score-server-1.0.0-jar-with-dependencies.jar
```

2. 调用任意API，例如：
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"测试用户"}'
```

3. 在服务器控制台查看日志输出：
```
[2025-12-19 10:20:30.123] POST /api/users | Body: {"name":"测试用户"}
[2025-12-19 10:20:30.456] POST /api/users | Status: 201 | Response: {"success":true,"data":{"id":"...","name":"测试用户",...}}
```

## 实现细节

### 核心方法

1. **logRequest(String method, String path, String body)**
   - 记录请求信息
   - 参数：HTTP方法、路径、请求体

2. **logResponse(String method, String path, int statusCode, String response)**
   - 记录响应信息
   - 参数：HTTP方法、路径、状态码、响应内容
   - 响应内容超过500字符会自动截断

### 技术栈
- 使用 SLF4J Logger 进行日志记录
- 使用 Java 8 时间API格式化时间
- 集成到所有API控制器方法中

## 注意事项

1. 日志中包含的敏感信息（如用户数据）请在生产环境中根据需要进行脱敏处理
2. 长响应内容（>500字符）会被截断，避免日志过大
3. 建议在生产环境中配置日志框架将日志输出到文件
