package com.llw.goodble

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.llw.goodble.adapter.OperateCallback
import com.llw.goodble.adapter.ServiceAdapter
import com.llw.goodble.base.BaseActivity
import com.llw.goodble.base.viewBinding
import com.llw.goodble.ble.BleCallback
import com.llw.goodble.ble.BleConstant.APP_NAME
import com.llw.goodble.ble.BleConstant.AUTHENTICATED_SIGNED_WRITES
import com.llw.goodble.ble.BleConstant.BROADCAST
import com.llw.goodble.ble.BleConstant.EXTENDED_PROPERTIES
import com.llw.goodble.ble.BleConstant.INDICATE
import com.llw.goodble.ble.BleConstant.NOTIFY
import com.llw.goodble.ble.BleConstant.READ
import com.llw.goodble.ble.BleConstant.UNKNOWN_DEVICE
import com.llw.goodble.ble.BleConstant.WRITE
import com.llw.goodble.ble.BleConstant.WRITE_NO_RESPONSE
import com.llw.goodble.ble.BleCore
import com.llw.goodble.ble.BleUtils
import com.llw.goodble.databinding.ActivityMainBinding
import com.llw.goodble.databinding.DialogRequestMtuBinding
import com.llw.goodble.databinding.DialogWriteDataBinding
import java.util.*


/**
 * 主页面
 */
class MainActivity : BaseActivity(), BleCallback, OperateCallback {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    private lateinit var bleCore: BleCore

    private var mServiceAdapter: ServiceAdapter? = null

    private val mServiceList: MutableList<BluetoothGattService> = mutableListOf()

    @SuppressLint("MissingPermission")
    private val scanIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data == null) return@registerForActivityResult
                //获取选中的设备
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data!!.getParcelableExtra("device", BluetoothDevice::class.java)
                } else {
                    result.data!!.getParcelableExtra("device") as BluetoothDevice?
                }
                //连接设备
                if (device != null) bleCore.connect(device)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        bleCore = (application as BleApp).getBleCore()
        bleCore.setBleCallback(this@MainActivity)
        //进入扫描页面
        binding.toolbar.setNavigationOnClickListener {
            scanIntent.launch(
                Intent(
                    this,
                    ScanActivity::class.java
                )
            )
        }
        //断开连接
        binding.tvDisconnect.setOnClickListener {
            bleCore.disconnect()
        }
        //设备信息
        binding.tvDeviceInfo.setOnClickListener {  }
    }

    /**
     * 创建选项菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!bleCore.isConnected()) {
            showMsg("设备已断开连接")
            return false
        }
        when(item.itemId) {
            R.id.item_request_mtu -> showRequestMtuDialog()
        }
        return true
    }



    override fun deviceInfo(info: String) {
        runOnUiThread {
            binding.tvDeviceInfo.text = info
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(state: Boolean) {
        runOnUiThread {
            if (state) {
                binding.tvDisconnect.visibility = View.VISIBLE
                bleCore.getDevice()?.apply {
                    binding.toolbar.title = name ?: UNKNOWN_DEVICE
                    binding.toolbar.subtitle = address
                }
            } else {
                binding.tvDisconnect.visibility = View.GONE
                binding.toolbar.title = APP_NAME
                binding.toolbar.subtitle = ""
                binding.tvDeviceInfo.text = ""
                binding.emptyBleLay.root.visibility = View.VISIBLE
                mServiceList.clear()
                mServiceAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onServicesDiscovered(services: List<BluetoothGattService>) {
        runOnUiThread {
            mServiceList.clear()
            mServiceList.addAll(services)
            binding.emptyBleLay.root.visibility = if (services.isNotEmpty()) View.GONE else View.GONE
            mServiceAdapter ?: run {
                mServiceAdapter = ServiceAdapter(mServiceList, this@MainActivity)
                binding.rvService.apply {
                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = mServiceAdapter
                    //增加分隔线
                    addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                }
                mServiceAdapter
            }
            mServiceAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * 属性操作
     */
    override fun onPropertyOperate(characteristic: BluetoothGattCharacteristic, operateName: String) {
        if (!bleCore.isConnected()) showMsg("设备已断开连接")
        Log.d("TAG", "onPropertyOperate: ${characteristic.uuid}")
        when (operateName) {
            READ -> bleCore.readCharacteristic(characteristic)
            WRITE, WRITE_NO_RESPONSE -> showWriteDataDialog(characteristic, operateName)
            NOTIFY, INDICATE -> bleCore.notifyEnable(characteristic, characteristic.descriptors[0].uuid, operateName)
            BROADCAST, AUTHENTICATED_SIGNED_WRITES, EXTENDED_PROPERTIES -> showMsg(operateName)
        }
    }

    /**
     * 显示写入数据弹窗
     */
    private fun showWriteDataDialog(characteristic: BluetoothGattCharacteristic, operateName: String) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
        val writeDataBinding = DialogWriteDataBinding.inflate(layoutInflater)
        writeDataBinding.toolbar.title = if (operateName == WRITE) "写入数据" else "写入无需响应数据"
        writeDataBinding.btnPositive.setOnClickListener {
            val inputData = writeDataBinding.etData.text.toString()
            if (inputData.isEmpty()) {
                writeDataBinding.dataLayout.error = "请输入数据"
                return@setOnClickListener
            }
            if (!BleUtils.isHexFormat(inputData)) {
                writeDataBinding.dataLayout.error = "请输入有效数据"
                return@setOnClickListener
            }
            bleCore.writeCharacteristic(characteristic, inputData, operateName)
            dialog.dismiss()
        }
        writeDataBinding.btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(writeDataBinding.root)
        dialog.show()
    }

    /**
     * 显示请求Mtu弹窗
     */
    private fun showRequestMtuDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogStyle)
        val mtuBinding = DialogRequestMtuBinding.inflate(layoutInflater)
        mtuBinding.btnPositive.setOnClickListener {
            val inputData = mtuBinding.etData.text.toString()
            if (inputData.isEmpty()) {
                mtuBinding.dataLayout.error = "请输入MTU"
                return@setOnClickListener
            }
            val mtu = inputData.toInt()
            if (mtu !in 23..517) {
                mtuBinding.dataLayout.error = "请输入23 ~ 517之间的数字"
                return@setOnClickListener
            }
            bleCore.requestMtu(mtu)
            dialog.dismiss()
        }
        mtuBinding.btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(mtuBinding.root)
        dialog.show()
    }

}