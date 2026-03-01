package nekit.corporation.data.di

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import nekit.corporation.common.AppScope
import nekit.corporation.data.local.AppDataBase
import nekit.corporation.data.local.Database

@Module
@ContributesTo(AppScope::class)
object DataModule {

    @Provides
    fun provideAppDatabase(appContext: Context): AppDataBase =
        Database.build(appContext)
}
