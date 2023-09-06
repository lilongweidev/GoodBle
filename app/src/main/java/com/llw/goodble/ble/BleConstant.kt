package com.llw.goodble.ble

/**
 * Ble常量
 */
object BleConstant {

    const val APP_NAME = "GoodBle"

    const val UNKNOWN_DEVICE = "Unknown device"

    const val UNKNOWN_SERVICE = "Unknown Service"

    const val UNKNOWN_CHARACTERISTICS = "Unknown Characteristics"

    const val UNKNOWN_DESCRIPTOR = "Unknown Descriptor"

    const val BROADCAST = "Broadcast"

    const val READ = "Read"

    const val WRITE_NO_RESPONSE = "Write No Response"

    const val WRITE = "Write"

    const val NOTIFY = "Notify"

    const val INDICATE = "Indicate"

    const val AUTHENTICATED_SIGNED_WRITES = "Authenticated Signed Writes"

    const val EXTENDED_PROPERTIES = "Extended Properties"

    //过滤RSSI
    const val FILTER_RSSI_FLAG = "filterRssiFlag"
    //RSSI 值
    const val FILTER_RSSI_VALUE = "filterRssiValue"
    //过滤空设备名
    const val FILTER_NULL_FLAG = "filterNullFlag"
    //是否过滤Mac地址
    const val FILTER_MAC_FLAG = "filterMacFlag"
    //需要过滤的Mac地址
    const val FILTER_MAC_VALUE = "filterMacValue"

}