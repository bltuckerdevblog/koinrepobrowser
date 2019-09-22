package com.abnormallydriven.koinrepobrowser.common

import androidx.databinding.DataBindingComponent

class AppDatabindingComponent(private val appDatabindingAdapter: AppDatabindingAdapter) : DataBindingComponent {
    override fun getAppDatabindingAdapter() = appDatabindingAdapter
}