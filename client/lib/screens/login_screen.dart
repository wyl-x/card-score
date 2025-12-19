import 'package:flutter/material.dart';
import '../models/user.dart';
import '../models/room.dart';
import '../services/api_service.dart';
import '../services/storage_service.dart';
import 'room_list_screen.dart';
import 'room_detail_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _nameController = TextEditingController();
  final _serverUrlController = TextEditingController();
  final _storageService = StorageService();
  bool _isLoading = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadSavedData();
  }

  Future<void> _loadSavedData() async {
    final serverUrl = await _storageService.getServerUrl();
    _serverUrlController.text = serverUrl;

    final user = await _storageService.getCurrentUser();
    if (user != null) {
      // 检查用户是否在房间中
      if (user.currentRoomId != null && user.currentRoomId!.isNotEmpty) {
        await _autoJoinRoom(user);
      } else {
        _navigateToRoomList(user);
      }
    }
  }

  Future<void> _autoJoinRoom(User user) async {
    try {
      final apiService = ApiService(baseUrl: _serverUrlController.text);
      final room = await apiService.getRoom(user.currentRoomId!);
      
      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => RoomDetailScreen(
              room: room,
              currentUser: user,
            ),
          ),
        );
      }
    } catch (e) {
      // 如果房间不存在或出错，跳转到房间列表
      if (mounted) {
        _navigateToRoomList(user);
      }
    }
  }

  Future<void> _createUser() async {
    final name = _nameController.text.trim();
    if (name.isEmpty) {
      setState(() {
        _errorMessage = '请输入用户名';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      await _storageService.saveServerUrl(_serverUrlController.text);
      final apiService = ApiService(baseUrl: _serverUrlController.text);
      final user = await apiService.createUser(name);
      await _storageService.saveCurrentUser(user);
      _navigateToRoomList(user);
    } catch (e) {
      setState(() {
        _errorMessage = '创建用户失败: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  void _navigateToRoomList(User user) {
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (context) => RoomListScreen(currentUser: user),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('记账'),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Icon(
              Icons.casino,
              size: 80,
              color: Colors.blue,
            ),
            const SizedBox(height: 32),
            TextField(
              controller: _serverUrlController,
              decoration: const InputDecoration(
                labelText: '服务器地址',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.dns),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: '用户名',
                border: OutlineInputBorder(),
                prefixIcon: Icon(Icons.person),
              ),
              onSubmitted: (_) => _createUser(),
            ),
            const SizedBox(height: 16),
            if (_errorMessage != null)
              Text(
                _errorMessage!,
                style: const TextStyle(color: Colors.red),
                textAlign: TextAlign.center,
              ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _isLoading ? null : _createUser,
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.all(16),
              ),
              child: _isLoading
                  ? const CircularProgressIndicator()
                  : const Text(
                      '登录/注册',
                      style: TextStyle(fontSize: 18),
                    ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _nameController.dispose();
    _serverUrlController.dispose();
    super.dispose();
  }
}
