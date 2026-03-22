package nekit.corporation.user.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Settings

@Inject
class GetSettingsUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): Settings {
        return userRepository.getSettings()
    }
}