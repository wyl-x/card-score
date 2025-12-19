import 'package:json_annotation/json_annotation.dart';

part 'transaction.g.dart';

@JsonSerializable()
class Transaction {
  final String id;
  final String roomId;
  final String fromUserId;
  final String toUserId;
  final int amount;
  final int timestamp;

  Transaction({
    required this.id,
    required this.roomId,
    required this.fromUserId,
    required this.toUserId,
    required this.amount,
    required this.timestamp,
  });

  factory Transaction.fromJson(Map<String, dynamic> json) =>
      _$TransactionFromJson(json);
  Map<String, dynamic> toJson() => _$TransactionToJson(this);
}
