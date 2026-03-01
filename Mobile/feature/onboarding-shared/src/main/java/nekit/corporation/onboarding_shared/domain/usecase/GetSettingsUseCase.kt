package nekit.corporation.onboarding_shared.domain.usecase

import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend fun execute() = settingsRepository.getSettings()
}
