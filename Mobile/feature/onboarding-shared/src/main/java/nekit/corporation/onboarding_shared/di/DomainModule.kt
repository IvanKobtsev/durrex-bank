package nekit.corporation.onboarding_shared.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import nekit.corporation.common.AppScope
import nekit.corporation.onboarding_shared.data.repository.SettingsRepositoryImpl
import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository

@Module
@ContributesTo(AppScope::class)
interface DomainModule {

    @Binds
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}