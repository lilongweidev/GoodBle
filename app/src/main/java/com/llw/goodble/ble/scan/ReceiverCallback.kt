package com.llw.goodble.ble.scan

/**
 * 广播回调，通知栏蓝牙关闭和定位关闭
 */
interface ReceiverCallback {
    fun bluetoothClose()
    fun locationClose()
}