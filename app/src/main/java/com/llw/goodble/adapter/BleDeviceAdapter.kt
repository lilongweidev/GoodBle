package com.llw.goodble.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.ble.BleDevice
import com.llw.goodble.databinding.ItemDeviceRvBinding
import java.util.*

/**
 * Ble蓝牙适配器
 */
class BleDeviceAdapter private constructor(
    private val mContext: Context,
    private val mDevices: List<BleDevice>
) : RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemDeviceRvBinding.inflate(LayoutInflater.from(mContext), parent, false))
        viewHolder.binding.itemDevice.setOnClickListener { v ->
            if (mOnItemClickListener != null) {
                val position: Int = viewHolder.adapterPosition
                mOnItemClickListener!!.onItemClick(v, position)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bleDevice: BleDevice = mDevices[position]
        val rssi: Int = bleDevice.rssi
        holder.binding.tvRssi.text = String.format(Locale.getDefault(), "%d dBm", rssi)
        //设备名称
        holder.binding.tvDeviceName.text = bleDevice.realName
        //Mac地址
        holder.binding.tvMacAddress.text = bleDevice.macAddress
    }

    override fun getItemCount() = mDevices.size

    class ViewHolder(itemView: ItemDeviceRvBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemDeviceRvBinding

        init {
            binding = itemView
        }
    }
}