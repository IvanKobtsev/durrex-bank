package com.example.shift_project.presentation

import android.app.Application
import com.example.shift_project.presentation.di.AppGraph
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.google.crypto.tink.aead.AeadConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class App : Application(), MetroApplication {

    private val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override fun onCreate() {
        super.onCreate()
        AeadConfig.register()
        INSTANCE = this
    }

    companion object {
        internal lateinit var INSTANCE: App
            private set
    }
}
