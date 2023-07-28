package com.llw.goodble.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
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

    protected fun back(toolbar: Toolbar, finish: Boolean = false) =
        toolbar.setNavigationOnClickListener { if (finish) finish() else onBackPressed() }

    protected fun showMsg(msg: CharSequence) =
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    protected open fun isAndroid12() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

}