package com.example.shift_project.presentation

import android.app.Application
import android.content.Context
import com.example.shift_project.presentation.di.AppDaggerComponent
import com.example.shift_project.presentation.di.DaggerAppDaggerComponent
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import com.google.crypto.tink.aead.AeadConfig
import com.google.i18n.phonenumbers.PhoneNumberUtil
import nekit.corporation.utils.PhoneNumberUtils

class App : Application() {
    private val cicerone = Cicerone.create(Router())
    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()
    lateinit var component: AppDaggerComponent

    override fun onCreate() {
        super.onCreate()
        AeadConfig.register()
        component = DaggerAppDaggerComponent.factory().create(this, router)
        INSTANCE = this
    }

    companion object {
        internal lateinit var INSTANCE: App
            private set
    }
}
