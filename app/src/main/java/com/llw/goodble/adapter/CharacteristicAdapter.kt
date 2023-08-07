package com.llw.goodble.adapter

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.ble.BleUtils
import com.llw.goodble.databinding.ItemCharacteristicBinding

/**
 * 特性适配器
 */
class CharacteristicAdapter(
    private val characteristics: List<BluetoothGattCharacteristic>,
    private val callback: OperateCallback
) : RecyclerView.Adapter<CharacteristicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCharacteristicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvCharacterName.text = BleUtils.getCharacteristicsName(characteristics[position].uuid)
        holder.binding.tvCharacterUuid.text = BleUtils.getShortUUID(characteristics[position].uuid)
        //加载特性下的属性
        holder.binding.rvProperty.apply {
            layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            val properties: List<String> = BleUtils.getProperties(characteristics[position].properties)
            adapter = PropertyAdapter(properties, object : OnItemClickListener {
                //点击属性
                override fun onItemClick(view: View?, position: Int) { callback.onPropertyOperate(characteristics[position], properties[position]) }
            })
        }
    }

    override fun getItemCount() = characteristics.size

    class ViewHolder(itemView: ItemCharacteristicBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemCharacteristicBinding

        init {
            binding = itemView
        }
    }
}