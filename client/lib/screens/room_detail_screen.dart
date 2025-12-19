import 'package:flutter/material.dart';
import 'dart:async';
import '../models/user.dart';
import '../models/room.dart';
import '../services/api_service.dart';
import '../services/storage_service.dart';
import '../services/tts_service.dart';
import 'room_list_screen.dart';

class RoomDetailScreen extends StatefulWidget {
  final Room room;
  final User currentUser;

  const RoomDetailScreen({
    super.key,
    required this.room,
    required this.currentUser,
  });

  @override
  State<RoomDetailScreen> createState() => _RoomDetailScreenState();
}

class _RoomDetailScreenState extends State<RoomDetailScreen> {
  final _storageService = StorageService();
  final _ttsService = TtsService();
  late ApiService _apiService;

  Map<String, dynamic>? _roomDetail;
  List<Map<String, dynamic>> _transactions = [];
  bool _isLoading = false;
  Timer? _refreshTimer;

  // 快捷转账金额配置
  final List<int> _quickAmounts = [1, 2, 3, 4, 5, 10];

  @override
  void initState() {
    super.initState();
    _initApiService();
    // 每5秒自动刷新
    _refreshTimer = Timer.periodic(const Duration(seconds: 3), (_) {
      _loadRoomDetail();
    });
  }

  Future<void> _initApiService() async {
    final serverUrl = await _storageService.getServerUrl();
    _apiService = ApiService(baseUrl: serverUrl);
    _loadRoomDetail();
  }

  Future<void> _loadRoomDetail() async {
    try {
      final detail = await _apiService.getRoomDetail(widget.room.id);
      final transactions =
          await _apiService.getTransactionDetails(widget.room.id);

      if (mounted) {
        setState(() {
          _roomDetail = detail;
          _transactions = transactions;
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('加载失败: $e')),
        );
      }
    }
  }

  Future<void> _showTransferDialog(User toUser) async {
    int? selectedAmount;
    final customAmountController = TextEditingController();

    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('转账给 ${toUser.name}'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('快捷金额:'),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              children: _quickAmounts.map((amount) {
                return ElevatedButton(
                  onPressed: () {
                    selectedAmount = amount;
                    Navigator.pop(context);
                  },
                  child: Text('$amount'),
                );
              }).toList(),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: customAmountController,
              decoration: const InputDecoration(
                labelText: '自定义',
                border: OutlineInputBorder(),
                suffixText: '分',
              ),
              keyboardType: TextInputType.number,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              final customAmount = int.tryParse(customAmountController.text);
              if (customAmount != null && customAmount > 0) {
                selectedAmount = customAmount;
              }
              Navigator.pop(context);
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );

    if (selectedAmount != null && selectedAmount! > 0) {
      _createTransaction(toUser, selectedAmount!);
    }
  }

  Future<void> _createTransaction(User toUser, int amount) async {
    setState(() {
      _isLoading = true;
    });

    try {
      await _apiService.createTransaction(
        roomId: widget.room.id,
        fromUserId: widget.currentUser.id,
        toUserId: toUser.id,
        amount: amount,
      );

      // 语音播报 (不等待完成，避免阻塞)
      _ttsService.announceTransaction(
        fromUserName: widget.currentUser.name,
        toUserName: toUser.name,
        amount: amount,
      ).catchError((e) {
        debugPrint('语音播报失败: $e');
      });

      await _loadRoomDetail();

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('转账成功')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('转账失败: $e')),
        );
      }
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: Scaffold(
        appBar: AppBar(
          title: Text(widget.room.name),
          actions: [
            IconButton(
              icon: const Icon(Icons.list),
              tooltip: '返回房间列表',
              onPressed: _returnToRoomList,
            ),
            IconButton(
              icon: const Icon(Icons.refresh),
              onPressed: _loadRoomDetail,
            ),
          ],
        ),
        body: _roomDetail == null
            ? const Center(child: CircularProgressIndicator())
            : Column(
                children: [
                  // 成员列表和分数
                  _buildMembersSection(),
                  const Divider(),
                  // 转账记录
                  Expanded(child: _buildTransactionsSection()),
                ],
              ),
      ),
    );
  }

  Future<bool> _onWillPop() async {
    // 用户点击返回按钮时，调用退出房间API
    try {
      await _apiService.leaveRoom(widget.room.id, widget.currentUser.id);
      return true; // 允许返回
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('退出房间失败: $e')),
        );
      }
      return true; // 即使失败也允许返回
    }
  }

  Future<void> _returnToRoomList() async {
    // 退出房间并返回房间列表
    try {
      await _apiService.leaveRoom(widget.room.id, widget.currentUser.id);
      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => RoomListScreen(currentUser: widget.currentUser),
          ),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('退出房间失败: $e')),
        );
      }
    }
  }

  Widget _buildMembersSection() {
    final members = (_roomDetail!['members'] as List)
        .map((e) => User.fromJson(e))
        .toList();
    final scores = _roomDetail!['scores'] as Map<String, dynamic>;

    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '成员',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          ...members.map((member) {
            final score = scores[member.id] ?? 0;
            final isCurrentUser = member.id == widget.currentUser.id;
            return Card(
              color: isCurrentUser ? Colors.blue.shade50 : null,
              child: ListTile(
                leading: Icon(
                  Icons.person,
                  color: isCurrentUser ? Colors.blue : null,
                ),
                title: Text(
                  member.name,
                  style: TextStyle(
                    fontWeight:
                        isCurrentUser ? FontWeight.bold : FontWeight.normal,
                  ),
                ),
                subtitle: Text('总分: $score'),
                trailing: isCurrentUser
                    ? null
                    : ElevatedButton(
                        onPressed:
                            _isLoading ? null : () => _showTransferDialog(member),
                        child: const Text('转账'),
                      ),
              ),
            );
          }).toList(),
        ],
      ),
    );
  }

  Widget _buildTransactionsSection() {
    if (_transactions.isEmpty) {
      return const Center(child: Text('暂无转账记录'));
    }

    return ListView.builder(
      itemCount: _transactions.length,
      reverse: true,
      itemBuilder: (context, index) {
        final detail = _transactions[_transactions.length - 1 - index];
        final transaction = detail['transaction'];
        final fromUserName = detail['fromUserName'];
        final toUserName = detail['toUserName'];
        final amount = transaction['amount'];
        final timestamp = DateTime.fromMillisecondsSinceEpoch(
          transaction['timestamp'] as int,
        );

        return Card(
          margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
          child: ListTile(
            leading: const Icon(Icons.payment),
            title: Text('$fromUserName → $toUserName'),
            subtitle: Text(
              '${timestamp.hour}:${timestamp.minute.toString().padLeft(2, '0')}',
            ),
            trailing: Text(
              '$amount分',
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Colors.green,
              ),
            ),
          ),
        );
      },
    );
  }

  @override
  void dispose() {
    _refreshTimer?.cancel();
    _ttsService.stop();
    super.dispose();
  }
}
