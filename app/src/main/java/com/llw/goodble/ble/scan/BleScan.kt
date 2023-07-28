package com.llw.goodble.ble.scan

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

@SuppressLint("MissingPermission", "InlinedApi")
class BleScan private constructor(private val context: Context) {

    private var mScanFilters: List<ScanFilter>
    private var mScanSettings: ScanSettings
    private var bleScanCallback: BleScanCallback? = null
    var mIsScanning = false

    init {
        mScanFilters = ArrayList()
        mScanSettings = ScanSettings.Builder().build()
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BleScan? = null

        private var mBluetoothAdapter: BluetoothAdapter? = null

        private var mScanner: BluetoothLeScanner? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: BleScan(context).also {
                instance = it
                val manager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                mBluetoothAdapter = manager.adapter
                if (mBluetoothAdapter != null) {
                    mScanner = mBluetoothAdapter?.bluetoothLeScanner
                }
            }
        }
    }

    /**
     * 设置扫描过滤
     */
    fun setScanFilters(scanFilters: List<ScanFilter>) {
        mScanFilters = scanFilters
    }

    /**
     * 设置扫描设置选项
     */
    fun setScanSettings(scanSettings: ScanSettings) {
        mScanSettings = scanSettings
    }

    /**
     * 设置扫描回调
     */
    fun setPhyScanCallback(bleScanCallback: BleScanCallback?) {
        this.bleScanCallback = bleScanCallback
    }

    fun isScanning() = mIsScanning

    /**
     * 扫描回调
     */
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            bleScanCallback?.onScanResult(result)
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            bleScanCallback?.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            localScanFailed(
                when (errorCode) {
                    SCAN_FAILED_ALREADY_STARTED -> "Fails to start scan as BLE scan with the same settings is already started by the app."
                    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Fails to start scan as app cannot be registered."
                    SCAN_FAILED_INTERNAL_ERROR -> "Fails to start scan due an internal error"
                    SCAN_FAILED_FEATURE_UNSUPPORTED -> "Fails to start power optimized scan as this feature is not supported."
                    else -> "UNKNOWN_ERROR"
                }
            )
        }
    }

    /**
     * 显示本地扫描错误
     */
    private fun localScanFailed(failed: String) = bleScanCallback?.onScanFailed(failed)

    /**
     * 开始扫描
     */
    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!isOpenBluetooth()) {
            localScanFailed("Bluetooth is not turned on.")
            return
        }
        if (isAndroid12()) {
            if (!hasBluetoothScan()) {
                localScanFailed("Android 12 needs to dynamically request bluetooth scan permission.")
                return
            }
        } else {
            if (!hasAccessFineLocation()) {
                localScanFailed("Android 6 to 12 requires dynamic request location permission.")
                return
            }
        }
        if (mIsScanning) {
            localScanFailed("Currently scanning, please close the current scan and scan again.")
            return
        }
        if (mScanner == null) {
            localScanFailed("BluetoothLeScanner is Null.")
            return
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            localScanFailed("Bluetooth not turned on.")
            return
        }
        mScanner?.startScan(mScanFilters, mScanSettings, scanCallback)
        mIsScanning = true
    }

    /**
     * 停止扫描
     */
    fun stopScan() {
        if (!mIsScanning) {
            localScanFailed("Not currently scanning, your stop has no effect.")
            return
        }
        if (mScanner == null) {
            localScanFailed("BluetoothLeScanner is Null.")
            return
        }
        if (!mBluetoothAdapter!!.isEnabled) {
            localScanFailed("Bluetooth not turned on.")
            return
        }
        mIsScanning = false
        mScanner?.stopScan(scanCallback)
    }

    /**
     * 是否打开蓝牙
     */
    private fun isOpenBluetooth() = if (mBluetoothAdapter == null) {
        localScanFailed("BluetoothAdapter is Null."); false
    } else mBluetoothAdapter!!.isEnabled

    private fun isAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    private fun hasAccessFineLocation() = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun hasBluetoothConnect() = hasPermission(Manifest.permission.BLUETOOTH_CONNECT)

    private fun hasBluetoothScan() = hasPermission(Manifest.permission.BLUETOOTH_SCAN)

    private fun hasPermission(permission: String) = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}