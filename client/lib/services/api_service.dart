import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/user.dart';
import '../models/room.dart';
import '../models/transaction.dart';
import '../models/api_response.dart';

class ApiService {
  final String baseUrl;

  ApiService({required this.baseUrl});

  // ============= User API =============

  Future<User> createUser(String name) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/users'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'name': name}),
    );

    if (response.statusCode == 201) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => User.fromJson(data as Map<String, dynamic>),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to create user');
  }

  Future<List<User>> getAllUsers() async {
    final response = await http.get(Uri.parse('$baseUrl/api/users'));

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => (data as List).map((e) => User.fromJson(e)).toList(),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to load users');
  }

  // ============= Room API =============

  Future<Room> createRoom(String name, String creatorId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/rooms'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'name': name, 'creatorId': creatorId}),
    );

    if (response.statusCode == 201) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => Room.fromJson(data as Map<String, dynamic>),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to create room');
  }

  Future<List<Room>> getAllRooms() async {
    final response = await http.get(Uri.parse('$baseUrl/api/rooms'));

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => (data as List).map((e) => Room.fromJson(e)).toList(),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to load rooms');
  }

  Future<List<Room>> searchRooms(String keyword) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/rooms/search?keyword=$keyword'),
    );

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => (data as List).map((e) => Room.fromJson(e)).toList(),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to search rooms');
  }

  Future<Room> getRoom(String roomId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/rooms/$roomId'),
    );

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => Room.fromJson(data as Map<String, dynamic>),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to get room');
  }

  Future<Room> joinRoom(String roomId, String userId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/rooms/$roomId/join'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'userId': userId}),
    );

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => Room.fromJson(data as Map<String, dynamic>),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to join room');
  }

  Future<void> leaveRoom(String roomId, String userId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/rooms/$roomId/leave'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'userId': userId}),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to leave room');
    }
  }

  Future<Map<String, dynamic>> getRoomDetail(String roomId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/rooms/$roomId/detail'),
    );

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => data as Map<String, dynamic>,
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to load room detail');
  }

  // ============= Transaction API =============

  Future<Transaction> createTransaction({
    required String roomId,
    required String fromUserId,
    required String toUserId,
    required int amount,
  }) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/transactions'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'roomId': roomId,
        'fromUserId': fromUserId,
        'toUserId': toUserId,
        'amount': amount,
      }),
    );

    if (response.statusCode == 201) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => Transaction.fromJson(data as Map<String, dynamic>),
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      }
    }
    throw Exception('Failed to create transaction');
  }

  Future<List<Map<String, dynamic>>> getTransactionDetails(
      String roomId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/transactions/room/$roomId/details'),
    );

    if (response.statusCode == 200) {
      final apiResponse = ApiResponse.fromJson(
        json.decode(utf8.decode(response.bodyBytes)),
        (data) => data as List<dynamic>,
      );
      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!
            .map((e) => e as Map<String, dynamic>)
            .toList();
      }
    }
    throw Exception('Failed to load transaction details');
  }
}
