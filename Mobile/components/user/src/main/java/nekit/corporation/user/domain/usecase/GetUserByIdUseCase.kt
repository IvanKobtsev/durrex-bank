package nekit.corporation.user.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.User

@Inject
class GetUserByIdUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(id: Int): User {
        return userRepository.getUser(id)
    }
}