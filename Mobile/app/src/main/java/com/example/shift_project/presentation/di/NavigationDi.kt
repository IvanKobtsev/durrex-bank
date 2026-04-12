package com.example.shift_project.presentation.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import nekit.corporation.common.di.MainNav

@ContributesTo(AppScope::class)
interface NavigationDi {

    @SingleIn(AppScope::class)
    @Provides
    @MainNav
    fun provideCicerone(): Cicerone<Router> = Cicerone.create(Router())

    @SingleIn(AppScope::class)
    @Provides
    fun provideRouter(@MainNav cicerone: Cicerone<Router>): Router = cicerone.router

}