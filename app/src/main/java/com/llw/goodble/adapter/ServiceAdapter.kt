package com.llw.goodble.adapter

import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.ble.BleUtils
import com.llw.goodble.databinding.ItemServiceBinding
import java.util.*

/**
 * 低功耗服务适配器
 */
class ServiceAdapter(
    private val services: List<BluetoothGattService>
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(mOnItemClickListener: OnItemClickListener?) {
        this.mOnItemClickListener = mOnItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        viewHolder.binding.itemService.setOnClickListener { mOnItemClickListener?.onItemClick(it, viewHolder.adapterPosition) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvServiceName.text = BleUtils.getServiceName(services[position].uuid)
        holder.binding.tvServiceUuid.text = BleUtils.getServiceUUID(services[position].uuid)
    }

    override fun getItemCount() = services.size

    class ViewHolder(itemView: ItemServiceBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemServiceBinding

        init {
            binding = itemView
        }
    }
}