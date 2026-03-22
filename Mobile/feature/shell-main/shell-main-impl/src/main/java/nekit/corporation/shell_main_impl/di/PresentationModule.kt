package nekit.corporation.shell_main_impl.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import nekit.corporation.shell_main_impl.navigation.MainBottomBarRouter

@ContributesTo(AppScope::class)
interface PresentationModule {


    @SingleIn(AppScope::class)
    @Provides
    fun provideRouter(): MainBottomBarRouter = MainBottomBarRouter()

    @SingleIn(AppScope::class)
    @Provides
    fun provideCiceroneRouter(mainBottomBarRouter: MainBottomBarRouter): Cicerone<Router> =
        Cicerone.create(mainBottomBarRouter)
}