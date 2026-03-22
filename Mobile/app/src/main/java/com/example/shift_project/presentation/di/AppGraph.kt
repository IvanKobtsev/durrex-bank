package com.example.shift_project.presentation.di

import android.app.Application
import android.content.Context
import com.example.shift_project.presentation.MainActivity
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(AppScope::class)
interface AppGraph : MetroAppComponentProviders, ViewModelGraph {

    @Provides
    fun provideApplicationContext(application: Application): Context = application
    fun inject(activity: MainActivity)
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AppGraph
    }
}