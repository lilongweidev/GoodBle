package com.llw.goodble

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.llw.goodble.adapter.OnItemClickListener
import com.llw.goodble.adapter.OperateCallback
import com.llw.goodble.adapter.ServiceAdapter
import com.llw.goodble.base.BaseActivity
import com.llw.goodble.base.viewBinding
import com.llw.goodble.ble.BleCallback
import com.llw.goodble.ble.BleCore
import com.llw.goodble.databinding.ActivityMainBinding


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

        bleCore = (application as BleApp).getBleCore()
        bleCore.setBleCallback(this@MainActivity)
        //进入扫描页面
        binding.toolbar.setNavigationOnClickListener { scanIntent.launch(Intent(this,ScanActivity::class.java)) }
        //断开连接
        binding.tvDisconnect.setOnClickListener {
            binding.tvDisconnect.visibility = View.GONE
            bleCore.disconnect()
        }
    }

    override fun deviceInfo(info: String) {
        runOnUiThread {
            binding.tvDeviceInfo.text = info
        }
    }

    override fun onConnectionStateChange(state: Boolean) {
        runOnUiThread {
            if (state) binding.tvDisconnect.visibility = View.VISIBLE
        }
    }

    override fun onServicesDiscovered(services: List<BluetoothGattService>) {
        runOnUiThread {
            mServiceList.clear()
            mServiceList.addAll(services)
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
        showMsg(operateName)
    }
}