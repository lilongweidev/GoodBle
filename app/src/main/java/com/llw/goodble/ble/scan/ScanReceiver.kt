package com.llw.goodble.ble.scan

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log

/**
 * 扫描接收器
 */
class ScanReceiver : BroadcastReceiver() {

    private var callback: ReceiverCallback? = null

    fun setCallback(callback: ReceiverCallback?) {
        this.callback = callback
    }

    private var isSend = 0
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                BluetoothAdapter.STATE_OFF -> Log.d(TAG, "STATE_OFF Phone bluetooth off")
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    callback!!.bluetoothClose()
                    Log.d(TAG, "STATE_TURNING_OFF Phone bluetooth is turning off")
                }
                BluetoothAdapter.STATE_ON -> Log.d(TAG, "STATE_ON Phone bluetooth turned on")
                BluetoothAdapter.STATE_TURNING_ON -> Log.d(TAG, "STATE_TURNING_ON Phone bluetooth is on")
            }
        } else if (action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            if (!isGPSOpen(context)) {
                isSend++
                if (isSend == 1) {
                    Log.d(TAG, "Positioning off")
                    callback!!.locationClose()
                } else if (isSend == 4) {
                    isSend = 0
                }
            }
        }
    }

    companion object {
        val TAG: String = ScanReceiver::class.java.simpleName
        fun isGPSOpen(context: Context): Boolean {
            val locationMode = try {
                Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
            } catch (e: SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF
        }
    }
}