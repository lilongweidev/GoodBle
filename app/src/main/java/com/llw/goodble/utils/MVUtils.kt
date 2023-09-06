package com.llw.goodble.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import java.util.*

object MVUtils {

    val mmkv = MMKV.defaultMMKV()

    fun put(key: String, value: Any): Boolean {
        return when (value) {
            is String -> mmkv.encode(key, value)
            is Float -> mmkv.encode(key, value)
            is Boolean -> mmkv.encode(key, value)
            is Int -> mmkv.encode(key, value)
            is Long -> mmkv.encode(key, value)
            is Double -> mmkv.encode(key, value)
            is ByteArray -> mmkv.encode(key, value)
            is Parcelable -> mmkv.encode(key, value)
            else -> false
        }
    }

    fun put(key: String, sets: Set<String>?): Boolean {
        if (sets == null) {
            return false
        }
        return mmkv.encode(key, sets)
    }

    fun getInt(key: String, defaultValue: Int = 0) = mmkv.decodeInt(key, defaultValue)

    fun getDouble(key: String, defaultValue: Double = 0.00) = mmkv.decodeDouble(key, defaultValue)

    fun getLong(key: String, defaultValue: Long = 0L) = mmkv.decodeLong(key, defaultValue)

    fun getBoolean(key: String, defaultValue: Boolean = false) = mmkv.decodeBool(key, defaultValue)

    fun getFloat(key: String, defaultValue: Float = 0F) = mmkv.decodeFloat(key, defaultValue)

    fun getByteArray(key: String) = mmkv.decodeBytes(key)

    fun getString(key: String, defaultValue: String = "") = mmkv.decodeString(key, defaultValue)

    inline fun <reified T : Parcelable> getParcelable(key: String) =
        mmkv.decodeParcelable(key, T::class.java)

    fun getStringSet(key: String) = mmkv.decodeStringSet(key, Collections.emptySet())

    fun removeKey(key: String) = mmkv.removeValueForKey(key)

    fun clearAll() = mmkv.clearAll()
}