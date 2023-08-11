package com.llw.goodble.adapter

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.ble.BleConstant.UNKNOWN_CHARACTERISTICS
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
        val characteristic = characteristics[position]
        val characteristicName = BleUtils.getCharacteristicsName(characteristic.uuid)
        holder.binding.tvCharacterName.text = characteristicName
        holder.binding.tvCharacterUuid.text = if (characteristicName != UNKNOWN_CHARACTERISTICS) BleUtils.getShortUUID(characteristic.uuid) else characteristic.uuid.toString()
        //加载特性下的属性
        holder.binding.rvProperty.apply {
            layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.HORIZONTAL }
            val properties: List<String> = BleUtils.getProperties(characteristic.properties)
            adapter = PropertyAdapter(properties, object : OnItemClickListener {
                //点击属性
                override fun onItemClick(view: View?, position: Int) { callback.onPropertyOperate(characteristic, properties[position]) }
            })
        }
        //加载特性下的描述
        if (characteristic.descriptors.isEmpty()) {
            holder.binding.layDescriptors.visibility = View.GONE
            return
        }
        holder.binding.rvDescriptor.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DescriptorAdapter(characteristic.descriptors)
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