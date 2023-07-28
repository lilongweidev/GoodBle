package com.llw.goodble

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.llw.goodble.ble.BleCore

class BleApp : Application() {

    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    @SuppressLint("StaticFieldLeak")
    private var bleCore: BleCore? = null

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //初始化Ble核心库
        bleCore = BleCore.getInstance(this)
    }

    fun getBleCore() = bleCore

}