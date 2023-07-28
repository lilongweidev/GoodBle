package com.llw.goodble.base

import android.app.Activity
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <VB : ViewBinding> viewBinding(viewInflater: (LayoutInflater) -> VB):
        ReadOnlyProperty<Activity, VB> = ActivityViewBindingProperty(viewInflater)

class ActivityViewBindingProperty<VB : ViewBinding>(
    private val viewInflater: (LayoutInflater) -> VB
) : ReadOnlyProperty<Activity, VB> {

    private var binding: VB? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): VB {
        return binding ?: viewInflater(thisRef.layoutInflater).also {
            thisRef.setContentView(it.root)
            binding = it
        }
    }
}