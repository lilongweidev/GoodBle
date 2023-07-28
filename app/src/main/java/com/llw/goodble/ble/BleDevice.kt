package com.llw.goodble.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanResult

/**
 * 自定义蓝牙设备
 */
data class BleDevice(
    var realName: String? = "Unknown device", //蓝牙设备真实名称
    var macAddress: String, //蓝牙设备Mac地址
    var rssi: Int, //信号强度
    var device: BluetoothDevice,//蓝牙设备
    var gatt: BluetoothGatt? = null//gatt
)