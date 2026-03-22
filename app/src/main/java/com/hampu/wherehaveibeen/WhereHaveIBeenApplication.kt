package com.hampu.wherehaveibeen

import android.app.Application
import com.hampu.wherehaveibeen.data.repository.AppContainer
import com.hampu.wherehaveibeen.data.repository.DefaultAppContainer

class WhereHaveIBeenApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
