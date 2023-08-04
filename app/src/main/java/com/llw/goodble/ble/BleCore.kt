package com.llw.goodble.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.llw.goodble.ble.scan.BleScan
import com.llw.goodble.ble.scan.BleScanCallback

/**
 * 低功耗蓝牙核心类
 */
@SuppressLint("MissingPermission")
class BleCore private constructor(private val context: Context) {

    @SuppressLint("StaticFieldLeak")
    companion object {

        private val TAG = BleCore::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BleCore? = null

        @SuppressLint("StaticFieldLeak")
        private lateinit var bleScan: BleScan

        private var mGatt: BluetoothGatt? = null

        private var mBleCallback: BleCallback? = null

        private lateinit var mBleGattCallback: BleGattCallback
        /**
         * 是否连接
         */
        private var mIsConnected = false

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: BleCore(context).also {
                instance = it
                //蓝牙扫描
                bleScan = BleScan.getInstance(context)
                mBleGattCallback = BleGattCallback()
            }
        }

        /**
         * 设备信息
         */
        private fun deviceInfo(info: String) = mBleCallback?.deviceInfo(info)
        /**
         * 连接状态
         */
        private fun connectState(state: Boolean) {
            mIsConnected = state
            mBleCallback?.onConnectionStateChange(state)
        }
    }


    fun setPhyScanCallback(bleScanCallback: BleScanCallback) {
        bleScan.setPhyScanCallback(bleScanCallback)
    }

    fun setBleCallback(bleCallback: BleCallback) {
        mBleCallback = bleCallback
    }

    fun isScanning() = bleScan.isScanning()

    fun startScan() = bleScan.startScan()

    fun stopScan() = bleScan.stopScan()

    /**
     * 连接蓝牙设备
     */
    fun connect(device: BluetoothDevice) {
        deviceInfo("连接中...")
        mGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            device.connectGatt(context, false, mBleGattCallback, BluetoothDevice.TRANSPORT_LE, BluetoothDevice.PHY_LE_2M_MASK)
        } else {
            device.connectGatt(context, false, mBleGattCallback)
        }
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        deviceInfo("断开连接...")
        mGatt?.disconnect()
    }

    /**
     * GATT回调
     */
    class BleGattCallback : BluetoothGattCallback() {

        /**
         * 连接状态改变
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val address = gatt.device.address
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    deviceInfo("已连接：$address")
                    connectState(true)
                    deviceInfo("发现服务中...")
                    //发现服务
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    deviceInfo("已断开连接：$address")
                    connectState(false)
                }
                else -> {
                    Log.d(TAG, "onConnectionStateChange: $status")
                    connectState(false)
                    mGatt?.close()
                    mGatt = null
                }
            }
        }

        /**
         * 发现服务
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                deviceInfo("发现了 ${gatt.services.size} 个服务")
                gatt.services?.let { mBleCallback?.onServicesDiscovered(it) }
            }
        }
    }
}