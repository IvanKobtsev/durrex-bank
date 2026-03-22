package nekit.corporation.user.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings

@Inject
class UpdateThemeUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(theme: Scheme): Settings {
        return userRepository.updateTheme(theme)
    }
}