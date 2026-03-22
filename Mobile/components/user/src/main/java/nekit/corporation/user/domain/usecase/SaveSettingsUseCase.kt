package nekit.corporation.user.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Settings

@Inject
class SaveSettingsUseCase (
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(settings: Settings) {
        return userRepository.saveSettings(settings)
    }
}