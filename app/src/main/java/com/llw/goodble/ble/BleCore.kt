package com.llw.goodble.ble

import android.annotation.SuppressLint
import android.content.Context
import com.llw.goodble.ble.scan.BleScan
import com.llw.goodble.ble.scan.BleScanCallback

class BleCore private constructor(private val context: Context) {

    @SuppressLint("StaticFieldLeak")
    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: BleCore? = null

        @SuppressLint("StaticFieldLeak")
        private lateinit var bleScan: BleScan

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: BleCore(context).also {
                instance = it
                //蓝牙扫描
                bleScan = BleScan.getInstance(context)
            }
        }
    }

    fun setPhyScanCallback(bleScanCallback: BleScanCallback) {
        bleScan.setPhyScanCallback(bleScanCallback)
    }

    fun isScanning() = bleScan.isScanning()

    fun startScan() = bleScan.startScan()

    fun stopScan() = bleScan.stopScan()

}