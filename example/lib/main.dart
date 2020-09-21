import 'dart:math';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_plugin_head_set_plugin/flutter_plugin_head_set_plugin.dart';
import 'package:flutter_plugin_head_set_plugin_example/event/audio_status.dart';

import 'event/event_bus_utils.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  int _state = -1;

  @override
  void initState() {
    super.initState();
    initPlatformState();
    initListener();
    eventBus.on<HeadsetNotification>().listen((HeadsetNotification event) => {
      print('eventBus on HeadsetNotification = $event'),
          setState(() {
            if (HeadsetState.CONNECT == event.status) {
              _state = 1;
            } else {
              _state = 0;
            }
          })
        });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    int headState;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterPluginHeadSetPlugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    try {
      headState = await FlutterPluginHeadSetPlugin.getCurrentState;
    } on PlatformException {
      headState = -1;
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
      _state = headState;
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initListener() async {
    try {
      FlutterPluginHeadSetPlugin.setListener((payload) => {
            print('eventBus payload = $payload'),
            if (payload as int == 1)
              {
                print('eventBus fire = HeadsetStatus.connect'),
                eventBus.fire(HeadsetNotification(HeadsetStatus.connect)),
              }
            else
              {
                print('eventBus fire = HeadsetStatus.disconnect'),
                eventBus.fire(HeadsetNotification(HeadsetStatus.disconnect)),
              }
          });
    } on PlatformException {}
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('插件 demo:'),
        ),
        body: Column(children: <Widget>[
          Text('耳机连接: $_state\n'),
          Text('Running on: $_platformVersion\n'),
        ]),
      ),
    );
  }
}
