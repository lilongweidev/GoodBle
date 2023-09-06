package com.llw.goodble

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.llw.goodble.adapter.BleDeviceAdapter
import com.llw.goodble.adapter.OnItemClickListener
import com.llw.goodble.base.BaseActivity
import com.llw.goodble.base.viewBinding
import com.llw.goodble.ble.BleConstant
import com.llw.goodble.ble.BleConstant.FILTER_MAC_FLAG
import com.llw.goodble.ble.BleConstant.FILTER_MAC_VALUE
import com.llw.goodble.ble.BleConstant.FILTER_NULL_FLAG
import com.llw.goodble.ble.BleConstant.FILTER_RSSI_FLAG
import com.llw.goodble.ble.BleConstant.FILTER_RSSI_VALUE
import com.llw.goodble.ble.BleCore
import com.llw.goodble.ble.BleDevice
import com.llw.goodble.ble.BleUtils
import com.llw.goodble.ble.scan.BleScanCallback
import com.llw.goodble.ble.scan.ReceiverCallback
import com.llw.goodble.ble.scan.ScanReceiver
import com.llw.goodble.databinding.ActivityScanBinding
import com.llw.goodble.databinding.DialogSettingMacBinding
import com.llw.goodble.databinding.DialogSettingRssiBinding
import com.llw.goodble.utils.MVUtils
import java.util.*

/**
 * 扫描页面
 */
@SuppressLint("NotifyDataSetChanged")
class ScanActivity : BaseActivity(), View.OnClickListener, BleScanCallback, ReceiverCallback,
    OnItemClickListener {

    private val TAG = ScanActivity::class.java.simpleName

    private val binding by viewBinding(ActivityScanBinding::inflate)

    private lateinit var bleCore: BleCore

    private var mAdapter: BleDeviceAdapter? = null

    //设备列表
    private val mList: MutableList<BleDevice> = mutableListOf()

    private lateinit var mMenu: Menu

    //蓝牙连接权限
    private val requestConnect =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            showMsg(if (it) "可以打开蓝牙" else "Android12 中不授予此权限无法打开蓝牙")
        }

    //启用蓝牙
    private val enableBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                showMsg("蓝牙已打开")
                Log.d(TAG, ": 蓝牙已打开")
                bleCore.setPhyScanCallback(this@ScanActivity)
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
                if (isOpenBluetooth()) bleCore.setPhyScanCallback(this@ScanActivity)
            }
        }

    //启用定位
    private val enableLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                showMsg("位置已打开")
                Log.d(TAG, ": 位置已打开")
                if (isOpenBluetooth()) bleCore.setPhyScanCallback(this@ScanActivity)
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
        setSupportActionBar(binding.toolbar)
        bleCore = (application as BleApp).getBleCore()

        initView()
        //设置扫描回调
        if (isOpenBluetooth()) bleCore.setPhyScanCallback(this@ScanActivity)
        //注册广播
        registerReceiver(
            ScanReceiver().apply { setCallback(this@ScanActivity) },
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
            })
    }

    private fun initView() {
        binding.requestBluetoothConnectLay.btnRequestConnectPermission.setOnClickListener(this)
        binding.enableBluetoothLay.btnEnableBluetooth.setOnClickListener(this)
        binding.requestLocationLay.btnRequestLocationPermission.setOnClickListener(this)
        binding.enableLocationLay.btnEnableLocation.setOnClickListener(this)
        binding.requestBluetoothScanLay.btnRequestScanPermission.setOnClickListener(this)
        binding.tvScanStatus.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        if (isAndroid12()) {
            //蓝牙连接
            binding.requestBluetoothConnectLay.root.visibility = if (hasBluetoothConnect()) View.GONE else View.VISIBLE
            if (!hasBluetoothConnect()) {
                Log.d(TAG, "onResume: 未获取蓝牙连接权限")
                return
            }
            //打开蓝牙开关
            binding.enableBluetoothLay.root.visibility = if (isOpenBluetooth()) View.GONE else View.VISIBLE
            if (!isOpenBluetooth()) {
                Log.d(TAG, "onResume: 未打开蓝牙")
                return
            }
            //蓝牙扫描
            binding.requestBluetoothScanLay.root.visibility = if (hasBluetoothScan()) View.GONE else View.VISIBLE
            if (!hasBluetoothScan()) {
                Log.d(TAG, "onResume: 未获取蓝牙扫描权限")
                return
            }
        }
        //打开蓝牙
        binding.enableBluetoothLay.root.visibility = if (isOpenBluetooth()) View.GONE else View.VISIBLE
        if (!isOpenBluetooth()) {
            Log.d(TAG, "onResume: 未打开蓝牙")
            return
        }
        //打开定位
        binding.enableLocationLay.root.visibility = if (isOpenLocation()) View.GONE else View.VISIBLE
        if (!isOpenLocation()) {
            Log.d(TAG, "onResume: 未打开位置")
            return
        }
        //请求定位
        binding.requestLocationLay.root.visibility = if (hasCoarseLocation() && hasAccessFineLocation()) View.GONE else View.VISIBLE
        if (!hasAccessFineLocation()) {
            Log.d(TAG, "onResume: 未获取定位权限")
            return
        }
        binding.tvScanStatus.visibility = View.VISIBLE
        //开始扫描
        if (!bleCore.isScanning()) startScan()
    }

    /**
     * 创建选项菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scan, menu)
        mMenu = menu
        mMenu.findItem(R.id.item_filter_null).isChecked = MVUtils.getBoolean(FILTER_NULL_FLAG)
        mMenu.findItem(R.id.item_filter_mac).isChecked = MVUtils.getBoolean(FILTER_MAC_FLAG)
        mMenu.findItem(R.id.item_filter_rssi).isChecked = MVUtils.getBoolean(FILTER_RSSI_FLAG)
        if (MVUtils.getBoolean(FILTER_RSSI_FLAG)) {
            mMenu.findItem(R.id.item_filter_rssi).title =
                "过滤RSSI：-" + MVUtils.getInt(FILTER_RSSI_VALUE, 100)
        }
        return true
    }

    /**
     * 选项菜单Item选中
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_filter_null -> { // 过滤空设备名称
                if (bleCore.isScanning()) stopScan()
                val filterNull = MVUtils.getBoolean(FILTER_NULL_FLAG)
                MVUtils.put(FILTER_NULL_FLAG, !filterNull)
                mMenu.findItem(R.id.item_filter_null).isChecked = MVUtils.getBoolean(FILTER_NULL_FLAG)
                showMsg(if (MVUtils.getBoolean(FILTER_NULL_FLAG)) "过滤空设备名称的设备" else "保留空设备名称的设备")
                if (!bleCore.isScanning()) startScan()
            }
            R.id.item_filter_mac -> { // 过滤Mac地址
                if (MVUtils.getBoolean(FILTER_MAC_FLAG)) {
                    mMenu.findItem(R.id.item_filter_mac).isChecked = false
                    MVUtils.put(FILTER_MAC_FLAG, false)
                    MVUtils.put(FILTER_MAC_VALUE, "")
                    showMsg("不过滤设备地址")
                } else {
                    showSettingMacDialog()
                }
            }
            R.id.item_filter_rssi -> { // 过滤RSSI
                if (MVUtils.getBoolean(FILTER_RSSI_FLAG)) {
                    if (bleCore.isScanning()) stopScan()
                    //关闭过滤RSSI
                    MVUtils.put(FILTER_RSSI_FLAG, false)
                    mMenu.findItem(R.id.item_filter_rssi).isChecked = false
                    MVUtils.put(FILTER_RSSI_VALUE, 100)
                    showMsg("取消过滤RSSI")
                    if (!bleCore.isScanning()) startScan()
                } else {
                    showSettingRssi()
                }
            }
        }
        return true
    }

    private fun showSettingRssi() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
        val rssiBinding = DialogSettingRssiBinding.inflate(layoutInflater)
        var progress = 100
        rssiBinding.sbRssi.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                rssiBinding.tvRssi.text = "-$progress dBm"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                progress = seekBar.progress
            }
        })
        val rssi: Int = MVUtils.getInt(FILTER_RSSI_VALUE, 100)
        rssiBinding.sbRssi.progress = rssi
        rssiBinding.tvRssi.text = String.format("-%s dBm", rssi)
        rssiBinding.btnPositive.setOnClickListener {
            //保存
            if (bleCore.isScanning()) stopScan()
            MVUtils.put(FILTER_RSSI_FLAG, true)
            //保存设置的RSSI值
            MVUtils.put(FILTER_RSSI_VALUE, progress)
            mMenu.findItem(R.id.item_filter_rssi).isChecked = true
            mMenu.findItem(R.id.item_filter_rssi).title = "过滤RSSI：-$progress"
            showMsg("过滤RSSI：-" + progress + "dBm")
            if (!bleCore.isScanning()) startScan()
            dialog.dismiss()
        }
        rssiBinding.btnNegative.setOnClickListener { dialog.dismiss() }
        dialog.setContentView(rssiBinding.root)
        dialog.show()
    }

    private fun showSettingMacDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
        val macBinding = DialogSettingMacBinding.inflate(layoutInflater)
        macBinding.btnPositive.setOnClickListener {
            val inputData = macBinding.etData.text.toString()
            if (inputData.isEmpty()) {
                macBinding.dataLayout.error = "请输入Mac地址"
                return@setOnClickListener
            }
            if (macBinding.cbFormatCheck.isChecked) {
                if (!BleUtils.isValidMac(inputData)) {
                    macBinding.dataLayout.error = "请输入正确的Mac地址"
                    return@setOnClickListener
                }
            }
            if (bleCore.isScanning()) stopScan()
            MVUtils.put(FILTER_MAC_VALUE, inputData)
            MVUtils.put(FILTER_MAC_FLAG, true)
            mMenu.findItem(R.id.item_filter_mac).isChecked = true
            showMsg("过滤Mac地址")
            if (!bleCore.isScanning()) startScan()
            dialog.dismiss()
        }
        macBinding.btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(macBinding.root)
        dialog.show()
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
            R.id.tv_scan_status -> if (bleCore.isScanning()) stopScan() else startScan()
            else -> {}
        }
    }

    private fun startScan() {
        mList.clear()
        mAdapter?.notifyDataSetChanged()
        bleCore.startScan()
        binding.tvScanStatus.text = "停止"
        binding.pbScanLoading.visibility = View.VISIBLE
        binding.emptyLay.root.visibility = View.VISIBLE
    }

    private fun stopScan() {
        bleCore.stopScan()
        binding.tvScanStatus.text = "搜索"
        binding.pbScanLoading.visibility = View.INVISIBLE
    }

    /**
     * 扫描回调
     */
    override fun onScanResult(result: ScanResult) {
        //过滤空设备名
        if (MVUtils.getBoolean(FILTER_NULL_FLAG)) {
            if (result.scanRecord!!.deviceName == null) {
                return
            }
            if (result.scanRecord!!.deviceName!!.isEmpty()) {
                return
            }
        }
        //过滤Mac地址
        if (MVUtils.getBoolean(FILTER_MAC_FLAG)) {
            val filterMac: String? = MVUtils.getString(FILTER_MAC_VALUE, "")
            if (filterMac!!.isNotEmpty()) {
                if (!result.device.address.contains(filterMac)) return
            }
        }
        //过滤RSSI
        if (MVUtils.getBoolean(FILTER_RSSI_FLAG)) {
            val rssi: Int = -MVUtils.getInt(FILTER_RSSI_VALUE, 100)
            if (result.rssi < rssi) {
                return
            }
        }
        //设备名称
        val realName = result.scanRecord?.deviceName?.let { it.ifEmpty { BleConstant.UNKNOWN_DEVICE } } ?: BleConstant.UNKNOWN_DEVICE
        val bleDevice = BleDevice(realName, result.device.address, result.rssi, result.device)
        if (mList.size == 0) {
            mList.add(bleDevice)
        } else {
            val index = findIndex(bleDevice, mList)
            if (index == -1) {
                //添加新设备
                mList.add(bleDevice)
            } else {
                //更新已有设备的rssi
                mList[index].rssi = bleDevice.rssi
            }
        }
        //如果未扫描到设备，则显示空内容布局
        binding.emptyLay.root.visibility = if (mList.size == 0) View.VISIBLE else View.GONE
        //如果mAdapter为空则会执行run{}中的代码，进行相关配置，最终返回配置的结果mAdapter
        mAdapter ?: run {
            mAdapter = BleDeviceAdapter(mList)
            binding.rvDevice.apply {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                layoutManager = LinearLayoutManager(this@ScanActivity)
                adapter = mAdapter
            }
            mAdapter!!.setOnItemClickListener(this@ScanActivity)
            mAdapter
        }
        mAdapter!!.notifyDataSetChanged()
    }

    override fun onItemClick(view: View?, position: Int) {
        if (bleCore.isScanning()) stopScan()
        //选中设备处理
        val intent = Intent()
        intent.putExtra("device", mList[position].device)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun findIndex(bleDevice: BleDevice, mList: MutableList<BleDevice>): Int {
        var index = 0
        for (devi in mList) {
            if (bleDevice.macAddress.contentEquals(devi.macAddress)) return index
            index += 1
        }
        return -1
    }

    /**
     * 蓝牙关闭
     */
    override fun bluetoothClose() {
        //蓝牙关闭时停止扫描
        if (bleCore.isScanning()) {
            stopScan()
            binding.enableBluetoothLay.root.visibility = View.VISIBLE
        }
    }

    /**
     * 位置关闭
     */
    override fun locationClose() {
        //位置关闭时停止扫描
        if (bleCore.isScanning()) {
            stopScan()
            binding.enableLocationLay.root.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        //页面停止时停止扫描
        if (bleCore.isScanning()) stopScan()
    }

}