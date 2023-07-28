package com.llw.goodble.ble.scan

import android.bluetooth.le.ScanResult

/**
 * 低功耗蓝牙扫描回调
 */
interface BleScanCallback {
    /**
     * 扫描结果
     */
    fun onScanResult(result: ScanResult)

    /**
     * 批量扫描结果
     */
    fun onBatchScanResults(results: List<ScanResult>) {}

    /**
     * 扫描错误
     */
    fun onScanFailed(failed: String) {}
}