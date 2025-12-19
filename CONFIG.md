# 打牌记账工具配置

## 快捷转账金额配置
可以在客户端代码中修改快捷转账金额，位置：
`client/lib/screens/room_detail_screen.dart` 第 32 行

```dart
final List<int> _quickAmounts = [1, 5, 10, 20, 50, 100];
```

根据需要修改为你想要的金额，例如：
```dart
final List<int> _quickAmounts = [1, 2, 5, 10, 15, 20];
```

## 服务器配置
服务器默认端口：8080

启动时可指定端口：
```bash
java -jar card-score-server.jar 9090
```

## 客户端配置
在登录界面可以设置服务器地址，默认为 `http://localhost:8080`

如果服务器在其他设备上运行，需要修改为对应的IP地址，例如：
- `http://192.168.1.100:8080`
- `http://10.0.0.50:8080`
