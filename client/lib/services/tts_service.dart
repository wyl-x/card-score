import 'package:flutter_tts/flutter_tts.dart';

class TtsService {
  final FlutterTts _flutterTts = FlutterTts();

  TtsService() {
    _init();
  }

  Future<void> _init() async {
    await _flutterTts.setLanguage('zh-CN');
    await _flutterTts.setSpeechRate(0.5);
    await _flutterTts.setVolume(1.0);
    await _flutterTts.setPitch(1.0);
  }

  Future<void> speak(String text) async {
    await _flutterTts.speak(text);
  }

  Future<void> announceTransaction({
    required String fromUserName,
    required String toUserName,
    required int amount,
  }) async {
    final message = '$fromUserName 向 $toUserName 转了 $amount 分';
    await speak(message);
  }

  Future<void> stop() async {
    await _flutterTts.stop();
  }
}
