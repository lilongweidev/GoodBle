package com.llw.goodble.base

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class BaseActivity : AppCompatActivity() {

    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
    }

    protected fun jumpActivity(clazz: Class<*>?, finish: Boolean = false) {
        startActivity(Intent(context, clazz))
        if (finish) finish()
    }

    protected fun back(toolbar: Toolbar, finish: Boolean = false) = toolbar.setNavigationOnClickListener { if (finish) finish() else onBackPressed() }

    protected fun showMsg(msg: CharSequence) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    protected open fun isAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    protected open fun hasAccessFineLocation() =
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    protected open fun hasCoarseLocation() =
        hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.S)
    protected open fun hasBluetoothConnect() = hasPermission(Manifest.permission.BLUETOOTH_CONNECT)

    @RequiresApi(Build.VERSION_CODES.S)
    protected open fun hasBluetoothScan() = hasPermission(Manifest.permission.BLUETOOTH_SCAN)

    /**
     * 检查是有拥有某权限
     *
     * @param permission 权限名称
     * @return true 有  false 没有
     */
    protected open fun hasPermission(permission: String) = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    /**
     * 蓝牙是否打开
     *
     * @return true or false
     */
    protected open fun isOpenBluetooth(): Boolean {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).also {
            it.adapter ?: return false
            return it.adapter.isEnabled
        }
    }

    /**
     * 位置是否打开
     */
    protected open fun isOpenLocation(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val locationEnabled = isLocationEnabled()
        Log.d("TAG", "gps: $gps，network：$network，locationEnabled：$locationEnabled")
        return gps || network || locationEnabled
    }

    open fun isLocationEnabled(): Boolean {
        val locationMode = try {
            Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            return false
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }
}