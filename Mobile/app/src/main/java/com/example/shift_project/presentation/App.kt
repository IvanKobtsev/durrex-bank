package com.example.shift_project.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import com.example.shift_project.presentation.di.AppGraph
import com.google.crypto.tink.aead.AeadConfig
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import nekit.corporation.crash.data.model.fromThrowable
import nekit.corporation.crash.data.worker.CrashSender
import nekit.corporation.crash.domain.usecase.AddCrashLogUseCase
import nekit.corporation.crash.domain.usecase.SendCrashLogsUseCase
import nekit.corporation.user.domain.usecase.GetUserUseCase
import java.util.concurrent.TimeUnit

class App : Application(), MetroApplication, Configuration.Provider {
    private val appScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()

    @Inject
    lateinit var getUserUseCase: GetUserUseCase

    init {
        AeadConfig.register()
    }

    @Inject
    lateinit var addCrashLogUseCase: AddCrashLogUseCase

    @Inject
    lateinit var sendCrashLogsUseCase: SendCrashLogsUseCase

    override fun onCreate() {
        super.onCreate()
        appGraph.inject(this)
        createNotificationChannels(this)
        INSTANCE = this
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // scheduleBackgroundWork()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runBlocking {
                try {
                    val userId = try { getUserUseCase().id.toString() } catch (e: Exception) { "anonymous" }
                    val log = fromThrowable(
                        throwable = throwable,
                        threadName = thread.name,
                        userId = userId
                    )
                    sendCrashLogsUseCase(log)
                } catch (e: Exception) {
                    Log.e("App", "Failed to save crash log", e)
                }
            }

            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }

    companion object {
        internal lateinit var INSTANCE: App
            private set
        private const val TAG = "MainAppTag"

    }

    private fun createNotificationChannels(context: Context) {
        val defaultChannel = NotificationChannel(
            "default_channel",
            "Общие уведомления",
            NotificationManager.IMPORTANCE_HIGH
        )
        val actionableChannel = NotificationChannel(
            "actionable_channel",
            "Уведомления с действиями",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannels(listOf(defaultChannel, actionableChannel))
    }

    /* private fun scheduleBackgroundWork() {
         val workRequest = PeriodicWorkRequestBuilder<CrashSender>(15, TimeUnit.MINUTES)
             .setInputData(Data.Builder().putString("workName", "onCreate").build())
             .build()
         appGraph.workManager.enqueue(workRequest)
     }*/

}
