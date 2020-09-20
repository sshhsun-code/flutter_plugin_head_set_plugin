package com.example.sunqi.head_set_plugin.headset

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.example.sunqi.flutter_plugin_head_set_plugin.HeadSetListener

import android.os.Build
import android.text.TextUtils


/**
 * Created by sunqi13 on 2020/9/11.
 */
class HeadSetManager {

    companion object {
        var instance: HeadSetManager = HeadSetManager()
        const val STATE_DIS_CONNECT = 0 // 耳机断接
        const val STATE_CONNECT = 1  // 耳机连接

        @TargetApi(Build.VERSION_CODES.ECLAIR)
        fun getHeadStatus(context: Context?): Int {
            val audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL)
                for (deviceInfo in audioDevices) {
                    println("deviceInfo.type" + deviceInfo.type)
                    if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                            || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                            || deviceInfo.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                            || deviceInfo.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                        println("onHeadSetStateChange 耳机连接")
                        return STATE_CONNECT
                    }
                }
                println("onHeadSetStateChange 耳机断连")
                return STATE_DIS_CONNECT
            } else {
                var isHeadSetOn = audioManager.isWiredHeadsetOn || audioManager.isBluetoothScoOn || audioManager.isBluetoothA2dpOn
                println("onHeadSetStateChange 耳机连接 = " + isHeadSetOn)
                return if (isHeadSetOn) STATE_CONNECT else STATE_DIS_CONNECT
            }
        }
    }

    var currentState = -1

    private var isRegisteredHeadsetReceiver = false

    private var isHeadsetOn = false

    var headsetEventListener: HeadSetListener? = null

    private var mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (TextUtils.equals(action, BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                println("mReceiver onHeadSetStateChange BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED")
                val state = intent?.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)
                when (state) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        currentState = STATE_CONNECT
                        println("mReceiver onHeadSetStateChange 蓝牙耳机连接")
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        currentState = STATE_DIS_CONNECT
                        println("mReceiver onHeadSetStateChange 蓝牙耳机断连")
                    }
                }
            } else if (TextUtils.equals(action, Intent.ACTION_HEADSET_PLUG)) {
                println("mReceiver onHeadSetStateChange Intent.ACTION_HEADSET_PLUG")
                if (intent?.hasExtra("state") == true) {
                    if (intent.getIntExtra("state", 2) == 0) {
                        currentState = STATE_DIS_CONNECT
                        if (isHeadsetOn) {
                            println("mReceiver onHeadSetStateChange 有线耳机断连")
                            isHeadsetOn = false
                        }
                    } else if (intent.getIntExtra("state", 2) == 1) {
                        currentState = STATE_CONNECT
                        if (!isHeadsetOn) {
                            println("mReceiver onHeadSetStateChange 有线耳机连接")
                            isHeadsetOn = true
                        }
                    }
                }
            } else if (TextUtils.equals(action, BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
                println("mReceiver onHeadSetStateChange BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED")
                val connectionState = intent?.extras?.getInt(BluetoothAdapter.EXTRA_CONNECTION_STATE)
                when (connectionState) {
                    BluetoothAdapter.STATE_CONNECTED -> {
                        currentState = STATE_CONNECT
                        println("mReceiver onHeadSetStateChange 蓝牙耳机连接")
                    }
                    BluetoothAdapter.STATE_DISCONNECTED -> {
                        currentState = STATE_DIS_CONNECT
                        println("mReceiver onHeadSetStateChange 蓝牙耳机断连")
                    }
                    else -> {

                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun isAudioHeadsetOn(context: Context?): Boolean {
        val audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL)
            for (deviceInfo in audioDevices) {
                println("deviceInfo.type" + deviceInfo.type)
                if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || deviceInfo.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || deviceInfo.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    println("onHeadSetStateChange 耳机连接")
                    return true
                }
            }
            println("onHeadSetStateChange 耳机断连")
            return false
        } else {
            var isHeadSetOn = audioManager.isWiredHeadsetOn || audioManager.isBluetoothScoOn || audioManager.isBluetoothA2dpOn
            println("onHeadSetStateChange 耳机连接 = " + isHeadSetOn)
            return isHeadSetOn
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun registerHeadSetManager(context: Context?) {
        if (!isRegisteredHeadsetReceiver) {
            try {
                val filter = IntentFilter()
                filter.addAction(Intent.ACTION_HEADSET_PLUG)
                filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)
                filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                context?.registerReceiver(mReceiver, filter)
                println("mReceiver context?.registerReceiver(mReceiver, filter)")
                isRegisteredHeadsetReceiver = true
            } catch (e: Exception) {
            }
        }
    }

    fun setEventListener(listener: HeadSetListener) {
        headsetEventListener = listener;
    }
    fun unRegisterHeadSetManager(context: Context?) {
        mReceiver.let {
            context!!.unregisterReceiver(it)
        }
    }

}