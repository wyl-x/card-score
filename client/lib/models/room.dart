import 'package:json_annotation/json_annotation.dart';
import 'transaction.dart';

part 'room.g.dart';

@JsonSerializable()
class Room {
  final String id;
  final String name;
  final int createdAt;
  final List<String> memberIds;
  final List<Transaction> transactions;

  Room({
    required this.id,
    required this.name,
    required this.createdAt,
    required this.memberIds,
    required this.transactions,
  });

  factory Room.fromJson(Map<String, dynamic> json) => _$RoomFromJson(json);
  Map<String, dynamic> toJson() => _$RoomToJson(this);
}
