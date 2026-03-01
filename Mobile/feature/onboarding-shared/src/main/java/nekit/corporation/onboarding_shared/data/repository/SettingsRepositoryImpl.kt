package nekit.corporation.onboarding_shared.data.repository

import kotlinx.coroutines.flow.first
import nekit.corporation.onboarding_shared.data.datasource.local.SettingsDataStore
import nekit.corporation.onboarding_shared.domain.model.SettingsModel
import nekit.corporation.onboarding_shared.domain.model.toSettings
import nekit.corporation.onboarding_shared.domain.model.toSettingsModel
import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
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