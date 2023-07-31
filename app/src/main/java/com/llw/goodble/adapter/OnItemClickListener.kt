package com.llw.goodble.adapter

import android.view.View

/**
 * 适配器Item点击监听
 */
interface OnItemClickListener {
    fun onItemClick(view: View?, position: Int)
}