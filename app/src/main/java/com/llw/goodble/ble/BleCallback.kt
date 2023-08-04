package com.llw.goodble.ble

import android.bluetooth.BluetoothGattService

interface BleCallback {

    /**
     * 设备的所有信息
     */
    fun deviceInfo(info: String)

    /**
     * 连接状态
     * @param state true or false
     */
    fun onConnectionStateChange(state: Boolean)

    /**
     * 发现服务
     */
    fun onServicesDiscovered(services: List<BluetoothGattService>)
}