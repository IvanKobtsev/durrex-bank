package nekit.corporation.user.domain.usecase

import nekit.corporation.user.domain.UserRepository
import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(id: Int): User {
        return userRepository.getUser(id)
    }
}