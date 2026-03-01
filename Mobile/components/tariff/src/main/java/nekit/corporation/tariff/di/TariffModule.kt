package nekit.corporation.tariff.di

import com.squareup.anvil.annotations.optional.SingleIn
import dagger.Module
import dagger.Provides
import nekit.corporation.common.AppScope
import nekit.corporation.tariff.data.TariffRepositoryImpl
import nekit.corporation.tariff.domain.TariffRepository

@Module
@SingleIn(AppScope::class)
object TariffModule {

    @Provides
    fun provideTariffRepository(impl: TariffRepositoryImpl): TariffRepository = impl
}