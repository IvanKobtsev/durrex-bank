package nekit.corporation.user.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.Scheme
import nekit.corporation.user.domain.model.Settings

@Inject
class UpdateHiddenIdsUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(added: List<Int>, removed: List<Int>): Settings {
        return userRepository.updateHidden(added, removed)
    }
}