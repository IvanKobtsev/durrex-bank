package nekit.corporation.data.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import nekit.corporation.data.local.AppDataBase
import nekit.corporation.data.local.Database

@ContributesTo(AppScope::class)
interface DataModule {

    @Provides
    fun provideAppDatabase(appContext: Context): AppDataBase =
        Database.build(appContext)
}
