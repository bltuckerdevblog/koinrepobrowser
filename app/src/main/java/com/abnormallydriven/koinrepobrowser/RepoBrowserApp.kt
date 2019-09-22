package com.abnormallydriven.koinrepobrowser

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.abnormallydriven.koinrepobrowser.common.AppDatabindingComponent
import com.abnormallydriven.koinrepobrowser.common.AppDatabindingAdapter

class RepoBrowserApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val bindingAdapter = AppDatabindingAdapter()
        val appDatabindingComponent = AppDatabindingComponent(bindingAdapter)
        DataBindingUtil.setDefaultComponent(appDatabindingComponent)

    }
}