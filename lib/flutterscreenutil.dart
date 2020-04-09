import 'dart:async';

import 'package:flutter/services.dart';

class FlutterScreenUtil {
  static const MethodChannel _channel =
      const MethodChannel('flutterscreenutil');

  static Future<int> get getNavigationBarHeight async {
    final int height = await _channel.invokeMethod('getNavigationBarHeight');
    return height;
  }
}
