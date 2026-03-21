package nekit.corporation.onboarding_shared.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.onboarding_shared.domain.repository.SettingsRepository

@Inject
class GetSettingsUseCase constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend fun execute() = settingsRepository.getSettings()
}
