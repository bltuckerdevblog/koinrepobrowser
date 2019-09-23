package com.abnormallydriven.koinrepobrowser

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.abnormallydriven.koinrepobrowser.common.AppDatabindingComponent
import com.abnormallydriven.koinrepobrowser.common.AppDatabindingAdapter
import com.abnormallydriven.koinrepobrowser.common.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RepoBrowserApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val bindingAdapter = AppDatabindingAdapter()
        val appDatabindingComponent = AppDatabindingComponent(bindingAdapter)
        DataBindingUtil.setDefaultComponent(appDatabindingComponent)

        startKoin{
            androidContext(this@RepoBrowserApp)
            modules(appModule)
        }

    }
}