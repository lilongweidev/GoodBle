package com.llw.goodble.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.llw.goodble.databinding.ItemPropertyBinding

/**
 * 属性适配器
 */
class PropertyAdapter(
    private val properties: List<String>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)).apply {
            binding.tvProperty.setOnClickListener { v -> listener.onItemClick(v, adapterPosition) }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvProperty.text = properties[position]
    }

    override fun getItemCount() = properties.size

    class ViewHolder(itemView: ItemPropertyBinding) : RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemPropertyBinding

        init {
            binding = itemView
        }
    }
}