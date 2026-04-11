package com.example.shift_project.presentation.di

import android.app.Application
import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import com.example.shift_project.presentation.App
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import nekit.corporation.common.di.MetroWorkerFactory
import nekit.corporation.push.PushSubGraph
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders, ViewModelGraph, PushSubGraph.Factory {

    @Provides
    fun provideApplicationContext(application: Application): Context = application

    fun inject(app: App)

    val workManager: WorkManager

    @Provides
    fun providesWorkManager(application: Context): WorkManager {
        return WorkManager.getInstance(application)
    }

    @Multibinds
    val workerProviders:
            Map<KClass<out ListenableWorker>, Provider<MetroWorkerFactory.WorkerInstanceFactory<*>>>

    val workerFactory: MetroWorkerFactory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}