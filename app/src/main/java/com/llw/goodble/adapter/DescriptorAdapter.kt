package com.llw.goodble.adapter

import android.bluetooth.BluetoothGattDescriptor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.ble.BleConstant
import com.llw.goodble.ble.BleUtils
import com.llw.goodble.databinding.ItemDescriptorBinding

/**
 * 描述适配器
 */
class DescriptorAdapter(
    private val descriptors: List<BluetoothGattDescriptor>
) : RecyclerView.Adapter<DescriptorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDescriptorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val descriptorName = BleUtils.getDescriptorName(descriptors[position].uuid)
        holder.binding.tvDescriptorName.text = descriptorName
        holder.binding.tvDescriptorUuid.text = if (descriptorName != BleConstant.UNKNOWN_DESCRIPTOR) BleUtils.getShortUUID(descriptors[position].uuid) else descriptors[position].uuid.toString()
    }

    override fun getItemCount() = descriptors.size

    class ViewHolder(itemView: ItemDescriptorBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemDescriptorBinding

        init {
            binding = itemView
        }
    }
}