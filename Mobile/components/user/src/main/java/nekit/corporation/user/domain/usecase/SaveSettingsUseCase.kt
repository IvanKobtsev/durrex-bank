package nekit.corporation.user.domain.usecase

import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Settings
import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class SaveSettingsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(settings: Settings) {
        return userRepository.saveSettings(settings)
    }
}