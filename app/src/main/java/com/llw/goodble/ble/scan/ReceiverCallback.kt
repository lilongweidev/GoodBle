package com.llw.goodble.ble.scan

/**
 * 广播回调，通知栏蓝牙关闭和定位关闭
 */
interface ReceiverCallback {
    /**
     * 蓝牙关闭
     */
    fun bluetoothClose()

    /**
     * 位置关闭
     */
    fun locationClose()
}