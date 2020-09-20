import 'dart:async';

import 'package:flutter/services.dart';

typedef DetectPluggedCallback = Function(HeadsetState payload);

enum HeadsetState {
  CONNECT,
  DISCONNECT,
}

class FlutterPluginHeadSetPlugin {
  static const MethodChannel _channel =
      const MethodChannel('flutter_plugin_head_set_plugin');
  static DetectPluggedCallback detectPluggedCallback;

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<int> get getCurrentState async {
    final int state = await _channel.invokeMethod('getCurrentState');
    return state;
  }

  static setListener(DetectPluggedCallback onPlugged) {
    detectPluggedCallback = onPlugged;
    _channel.setMethodCallHandler(_handleMethod);
  }

  static Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case "connect":
        return detectPluggedCallback(HeadsetState.CONNECT);
      case "disconnect":
        return detectPluggedCallback(HeadsetState.DISCONNECT);
      default:
        print('No idea');
        return detectPluggedCallback(HeadsetState.DISCONNECT);
    }
  }
}
