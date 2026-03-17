package nekit.corporation.tariff.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Binds
import dagger.Module
import dagger.Provides
import nekit.corporation.common.AppScope
import nekit.corporation.tariff.data.TariffRepositoryImpl
import nekit.corporation.tariff.domain.TariffRepository

@Module
@SingleIn(AppScope::class)
@ContributesTo(AppScope::class)
abstract class TariffModule() {

    @Binds
    abstract fun provideTariffRepository(impl: TariffRepositoryImpl): TariffRepository
}