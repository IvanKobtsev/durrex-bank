package nekit.corporation.user.domain

import nekit.corporation.user.domain.model.User
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : suspend () -> User by userRepository::getUser