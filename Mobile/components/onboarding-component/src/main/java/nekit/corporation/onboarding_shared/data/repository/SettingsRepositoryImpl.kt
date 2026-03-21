package nekit.corporation.onboarding_shared.data.repository

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import nekit.corporation.onboarding_shared.data.datasource.local.SettingsDataStore
import nekit.corporation.onboarding_shared.domain.model.SettingsModel
import nekit.corporation.onboarding_shared.domain.model.toSettings
import nekit.corporation.onboarding_shared.domain.model.toSettingsModel
import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository

@Inject
@ContributesBinding(AppScope::class)
class SettingsRepositoryImpl(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override suspend fun getSettings(): SettingsModel {
        return settingsDataStore.data.first().toSettingsModel()
    }

    override suspend fun updateSettings(transform: (SettingsModel) -> SettingsModel) {
        settingsDataStore.updateData { settings ->
            transform(settings.toSettingsModel()).toSettings()
        }
    }
}