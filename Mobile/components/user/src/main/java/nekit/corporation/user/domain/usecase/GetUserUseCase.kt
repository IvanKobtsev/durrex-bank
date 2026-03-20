package nekit.corporation.user.domain.usecase

import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): User {
        return userRepository.getUser()
    }
}