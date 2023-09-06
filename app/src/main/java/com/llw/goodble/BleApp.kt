package com.llw.goodble

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.llw.goodble.ble.BleCore
import com.tencent.mmkv.MMKV

class BleApp : Application() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    @SuppressLint("StaticFieldLeak")
    private lateinit var bleCore: BleCore

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //初始化Ble核心库
        bleCore = BleCore.getInstance(this)
        //mmkv初始化
        MMKV.initialize(this)
    }

    fun getBleCore() = bleCore

}