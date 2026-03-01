package nekit.corporation.auth.domain.usecase

import nekit.corporation.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetCredentialsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke() = authRepository.getToken()
}