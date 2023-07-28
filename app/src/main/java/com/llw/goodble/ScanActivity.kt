package com.llw.goodble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.llw.goodble.adapter.BleDeviceAdapter
import com.llw.goodble.base.BaseActivity
import com.llw.goodble.base.viewBinding
import com.llw.goodble.ble.BleCore
import com.llw.goodble.ble.BleDevice
import com.llw.goodble.ble.scan.BleScanCallback
import com.llw.goodble.databinding.ActivityScanBinding
import kotlin.TODO
import kotlin.arrayOf

/**
 * 扫描页面
 */
@SuppressLint("NotifyDataSetChanged")
class ScanActivity : BaseActivity(), View.OnClickListener, BleScanCallback {

    private val binding by viewBinding(ActivityScanBinding::inflate)

    private val mAdapter: BleDeviceAdapter? = null

    //设备列表
    private val mList: MutableList<BleDevice> = mutableListOf()

    private var bleCore: BleCore? = null

    //蓝牙连接权限
    private val requestConnect =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            showMsg(if (it) "可以打开蓝牙" else "Android12 中不授予此权限无法打开蓝牙")
        }

    //启用蓝牙
    private val enableBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                showMsg("蓝牙已打开")
                bleCore?.setPhyScanCallback(this)
            }
        }

    //请求定位
    private val requestLocation =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val coarseLocation = result[Manifest.permission.ACCESS_COARSE_LOCATION]
            val fineLocation = result[Manifest.permission.ACCESS_FINE_LOCATION]
            if (coarseLocation == true && fineLocation == true) {
                //开始扫描设备
                showMsg("定位权限已获取")
            }
        }

    //启用定位
    private val enableLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                showMsg("位置已打开")
            }
        }

    //蓝牙连接权限
    private val requestScan =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            showMsg(if (it) "可以开始扫描设备了" else "Android12 Android12 中不授予此权限无法扫描蓝牙")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        bleCore = (application as BleApp).getBleCore()

        initView()
    }

    private fun initView() {
        binding.requestBluetoothConnectLay.btnRequestConnectPermission.setOnClickListener(this)
        binding.enableBluetoothLay.btnEnableBluetooth.setOnClickListener(this)
        binding.requestLocationLay.btnRequestLocationPermission.setOnClickListener(this)
        binding.enableLocationLay.btnEnableLocation.setOnClickListener(this)
        binding.requestBluetoothScanLay.btnRequestScanPermission.setOnClickListener(this)
        binding.toolbar.setOnClickListener(this)
        binding.tvScanStatus.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            //请求蓝牙连接权限
            R.id.btn_request_connect_permission -> if (isAndroid12()) requestConnect.launch(Manifest.permission.BLUETOOTH_CONNECT)
            //打开蓝牙开关
            R.id.btn_enable_bluetooth -> enableBluetooth.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            //请求定位权限
            R.id.btn_request_location_permission -> requestLocation.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
            //打开位置开关
            R.id.btn_enable_location -> enableLocation.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            //请求蓝牙扫描权限
            R.id.btn_request_scan_permission -> if (isAndroid12()) requestScan.launch(Manifest.permission.BLUETOOTH_SCAN)
            //扫描或停止扫描
            R.id.tv_scan_status -> if (bleCore?.isScanning() == true) stopScan() else startScan()
            else -> {}
        }
    }

    private fun startScan() {
        mList.clear()
        mAdapter?.notifyDataSetChanged()
        bleCore?.startScan()
        binding.tvScanStatus.text = "停止"
        binding.pbScanLoading.visibility = View.VISIBLE
    }

    private fun stopScan() {
        bleCore?.stopScan()
        binding.tvScanStatus.text = "搜索"
        binding.pbScanLoading.visibility = View.INVISIBLE
    }

    /**
     * 扫描回调
     */
    override fun onScanResult(result: ScanResult) {
        TODO("Not yet implemented")
    }
}