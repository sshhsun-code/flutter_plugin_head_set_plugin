package com.example.sunqi.flutter_plugin_head_set_plugin

import androidx.annotation.NonNull;
import com.example.sunqi.head_set_plugin.headset.HeadSetManager

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterPluginHeadSetPlugin */
public class FlutterPluginHeadSetPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_plugin_head_set_plugin")
        channel.setMethodCallHandler(this);

        HeadSetManager.instance.currentState = HeadSetManager.getHeadStatus(flutterPluginBinding.applicationContext)
        HeadSetManager.instance.registerHeadSetManager(flutterPluginBinding.applicationContext)
        HeadSetManager.instance.setEventListener(headsetEventListener)
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    companion object {
        private lateinit var channel: MethodChannel

        var headsetEventListener = object : HeadSetListener {

            override fun onHeadsetConnect() {
                channel?.invokeMethod("connect", "true")
            }

            override fun onHeadsetDisconnect() {
                channel?.invokeMethod("disconnect", "true")
            }
        }

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_plugin_head_set_plugin")
            channel.setMethodCallHandler(FlutterPluginHeadSetPlugin())

            HeadSetManager.instance.currentState = HeadSetManager.getHeadStatus(registrar.activeContext())
            HeadSetManager.instance.registerHeadSetManager(registrar.activeContext())
            HeadSetManager.instance.setEventListener(headsetEventListener)
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "getCurrentState") {
            result.success(HeadSetManager.instance.currentState)
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
