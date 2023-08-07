package com.llw.goodble.adapter

import android.bluetooth.BluetoothGattCharacteristic

/**
 * 操作回调
 */
interface OperateCallback {
    /**
     * 属性操作
     */
    fun onPropertyOperate(characteristic: BluetoothGattCharacteristic, operateName: String)
}