package com.llw.goodble.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.ble.BleDevice
import com.llw.goodble.databinding.ItemDeviceInfoBinding
import java.util.*

/**
 * 设备信息适配器
 */
class InfoAdapter(
    private val mLists: List<String>
) : RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDeviceInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvDeviceInfo.text = mLists[position]
    }

    override fun getItemCount() = mLists.size

    class ViewHolder(itemView: ItemDeviceInfoBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemDeviceInfoBinding

        init {
            binding = itemView
        }
    }
}