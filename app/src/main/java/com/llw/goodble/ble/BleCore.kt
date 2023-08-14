package com.llw.goodble.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import com.llw.goodble.ble.scan.BleScan
import com.llw.goodble.ble.scan.BleScanCallback
import java.util.*

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

        /**
         * 是否开启通知
         */
        private var mIsEnabled = false

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
        private fun deviceInfo(info: String) {
            Log.d(TAG, "deviceInfo: $info")
            mBleCallback?.deviceInfo(info)
        }

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

    fun isConnected() = mIsConnected

    fun getDevice() = mGatt?.device

    /**
     * 连接蓝牙设备
     */
    fun connect(device: BluetoothDevice) {
        deviceInfo("连接中...")
        if (mIsEnabled) mIsEnabled = false
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
     * 读取特性
     */
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        deviceInfo("读取特性: ${BleUtils.getShortUUID(characteristic.uuid)}")
        mGatt?.readCharacteristic(characteristic)
    }

    /**
     * 写入特性
     * @param characteristic 特性
     * @param data Hex数据
     * @param operateName 操作名，决定写入的是 Write 还是 Write No Response
     */
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, data: String, operateName: String) {
        deviceInfo("写入特性：${BleUtils.getShortUUID(characteristic.uuid)}，value：0x$data")
        //写入类型
        val writeType = if (operateName == BleConstant.WRITE) BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT else BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        //写入数据
        val byteArray = BleUtils.hexToBytes(data)
        //根据Android版本进行不同的写入方式 Android 13及以上和以下不同
        val executionResult = if (isAndroid13()) {
            mGatt?.writeCharacteristic(characteristic, byteArray, writeType) == BluetoothStatusCodes.SUCCESS
        } else {
            characteristic.writeType = writeType
            characteristic.value = byteArray
            mGatt?.writeCharacteristic(characteristic)
        }
        //执行写入动作成功不代表写入数据成功，执行写入动作失败，写入数据一定失败
        deviceInfo((if (executionResult == true)  "执行写入动作成功" else "执行写入动作失败") + "，value: ${BleUtils.bytesToHex(byteArray, true)}" )
    }

    /**
     * 开启或者关闭通知
     * @param characteristic 特性
     * @param descriptorUuid 描述UUID
     * @param operateName 操作名， 决定通过那种方式开启通知
     */
    fun notifyEnable(characteristic: BluetoothGattCharacteristic, descriptorUuid: UUID, operateName: String) {
        //设置特性通知，这一点很重要
        if (mGatt?.setCharacteristicNotification(characteristic,true) == false) return
        //描述
        val descriptor = characteristic.getDescriptor(descriptorUuid)
        //写入描述值
        val value = if (!mIsEnabled) {
            if (operateName == BleConstant.INDICATE) BluetoothGattDescriptor.ENABLE_INDICATION_VALUE else BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        } else {
            BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
        }
        val executionResult = if (isAndroid13()) {
            mGatt?.writeDescriptor(descriptor, value) == BluetoothStatusCodes.SUCCESS
        } else {
            descriptor.value = value
            mGatt?.writeDescriptor(descriptor)
        }
        deviceInfo((if (executionResult == true)  "执行启用动作成功" else "执行启用动作失败") + "，value: ${BleUtils.bytesToHex(value, true)}" )
    }

    private fun isAndroid13() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /**
     * GATT回调
     */
    class BleGattCallback : BluetoothGattCallback() {

        /**
         * 连接状态改变回调
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            mGatt = gatt
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
         * 发现服务回调
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                deviceInfo("发现了 ${gatt.services.size} 个服务")
                gatt.services?.let { mBleCallback?.onServicesDiscovered(it) }
            }
        }

        /**
         * 读取特性回调 Android 13及以上使用
         */
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            deviceInfo("读取特性值(Android 13及以上)：${BleUtils.bytesToHex(value, true)}")
        }

        /**
         * 读取特性回调 Android 12及以下使用
         */
        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            deviceInfo("读取特性值(Android 12及以下)：${BleUtils.bytesToHex(characteristic.value, true)}")
        }

        /**
         * 写入特性回调
         */
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            if (BleUtils.isAndroid13()) {
                gatt.readCharacteristic(characteristic)//读取特性
            } else {
                deviceInfo("写入成功：${BleUtils.bytesToHex(characteristic.value, true)}")
            }
        }

        /**
         * 描述符写入回调
         */
        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            if (BleUtils.isAndroid13()) {
                gatt.readDescriptor(descriptor) //读取描述符
            } else {
                mIsEnabled = !descriptor.value.contentEquals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
                deviceInfo("写入描述符成功：${BleUtils.bytesToHex(descriptor.value, true)}")
            }
        }

        /**
         * 读取描述符回调 Android 13及以上使用
         */
        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            mIsEnabled = !value.contentEquals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
            deviceInfo("读取描述符成功(Android 13及以上使用)：${BleUtils.bytesToHex(value, true)}")
        }

        /**
         * 读取描述符回调 Android 12及以上下使用
         */
        @Deprecated("Deprecated in Java")
        override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) return
            mIsEnabled = !descriptor.value.contentEquals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
            deviceInfo("读取描述符成功(Android 12及以下使用)：${BleUtils.bytesToHex(descriptor.value, true)}")
        }

        /**
         * 收到数据回调 Android 13及以上使用
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            deviceInfo("收到特性值(Android 13及以上)：${BleUtils.getShortUUID(characteristic.uuid)}：${BleUtils.bytesToHex(value, true)}")
        }

        /**
         * 收到数据回调 Android 12及以下使用
         */

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            deviceInfo("收到特性值(Android 12及以下)：${BleUtils.getShortUUID(characteristic.uuid)}：${BleUtils.bytesToHex(characteristic.value, true)}")
        }

    }
}