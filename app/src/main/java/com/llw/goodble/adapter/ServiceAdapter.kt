package com.llw.goodble.adapter

import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.R
import com.llw.goodble.ble.BleUtils
import com.llw.goodble.databinding.ItemServiceBinding

/**
 * 低功耗服务适配器
 */
class ServiceAdapter(
    private val services: List<BluetoothGattService>,
    private val callback: OperateCallback
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        viewHolder.binding.itemService.setOnClickListener {
            //显示特性列表
            viewHolder.binding.rvCharacteristics.visibility = if (viewHolder.binding.rvCharacteristics.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            //更换图标
            viewHolder.binding.ivState.setImageDrawable(
                if (viewHolder.binding.rvCharacteristics.visibility == View.VISIBLE) ContextCompat.getDrawable(parent.context, R.drawable.ic_down_24)
                else ContextCompat.getDrawable(parent.context, R.drawable.ic_right_24)
            )
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvServiceName.text = BleUtils.getServiceName(services[position].uuid)
        holder.binding.tvServiceUuid.text = BleUtils.getShortUUID(services[position].uuid)
        //加载服务下的特性
        holder.binding.rvCharacteristics.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CharacteristicAdapter(services[position].characteristics, callback)
        }
    }

    override fun getItemCount() = services.size

    class ViewHolder(itemView: ItemServiceBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemServiceBinding

        init {
            binding = itemView
        }
    }
}