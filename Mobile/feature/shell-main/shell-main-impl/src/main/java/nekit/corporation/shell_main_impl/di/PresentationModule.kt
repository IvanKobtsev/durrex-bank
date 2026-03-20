package nekit.corporation.di

import com.github.terrakok.cicerone.Cicerone
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Module
import dagger.Provides
import nekit.corporation.common.AppScope
import nekit.corporation.navigation.MainBottomBarRouter


@Module
@ContributesTo(AppScope::class)
object PresentationModule {


    @SingleIn(AppScope::class)
    @Provides
    fun provideRouter() = MainBottomBarRouter()

    @SingleIn(AppScope::class)
    @Provides
    fun provideCiceroneRouter(mainBottomBarRouter: MainBottomBarRouter) =
        Cicerone.create(mainBottomBarRouter)
}