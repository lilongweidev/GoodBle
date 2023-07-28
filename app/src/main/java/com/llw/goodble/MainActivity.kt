package com.llw.goodble

import android.os.Bundle
import com.llw.goodble.base.BaseActivity
import com.llw.goodble.base.viewBinding
import com.llw.goodble.databinding.ActivityMainBinding

/**
 * 主页面
 */
class MainActivity : BaseActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.toolbar.setNavigationOnClickListener { jumpActivity(ScanActivity::class.java) }
    }
}