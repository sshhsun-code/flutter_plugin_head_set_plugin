import UIKit
import Flutter
import AVFoundation

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }


    // Test
    private func receiveBatteryLevel(result: FlutterResult) {
      let device = UIDevice.current
      device.isBatteryMonitoringEnabled = true
      if device.batteryState == UIDevice.BatteryState.unknown {
        result(FlutterError(code: "UNAVAILABLE",
                            message: "Battery info unavailable",
                            details: nil))
      } else {
        result(Int(device.batteryLevel * 100))
      }
    }
    
    
  
  
  func HeadsetIsConnect() -> Int  {
      let currentRoute = AVAudioSession.sharedInstance().currentRoute
      for output in currentRoute.outputs {
          if output.portType == AVAudioSession.Port.headphones {
              return 1
          } else if (output.portType == AVAudioSession.Port.bluetoothA2DP) {
              return 1
          } else {
              return 0
          }
      }
      return 0
  }
}
