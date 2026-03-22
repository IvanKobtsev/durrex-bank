package nekit.corporation.onboarding_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.onboarding_shared.domain.model.SettingsModel
import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository

@Inject
class UpdateSettingUseCase(
    private val settingsRepository: SettingsRepository
) {

    suspend fun execute(transform: (SettingsModel) -> SettingsModel) =
        settingsRepository.updateSettings(transform)
}
