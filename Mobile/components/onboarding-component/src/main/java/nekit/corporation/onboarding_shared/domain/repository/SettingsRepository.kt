package nekit.corporation.onboarding_shared.domain.repository

import nekit.corporation.onboarding_shared.domain.model.SettingsModel

interface SettingsRepository {

    suspend fun getSettings(): SettingsModel

    suspend fun updateSettings(transform: (SettingsModel) -> SettingsModel)
}
