package nekit.corporation.onboarding_shared.domain.usecase

import nekit.corporation.onboarding_shared.domain.model.SettingsModel
import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend fun execute(transform: (SettingsModel) -> SettingsModel) =
        settingsRepository.updateSettings(transform)
}
