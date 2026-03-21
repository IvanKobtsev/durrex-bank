package nekit.corporation.auth.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.auth.domain.repository.AuthRepository

@Inject
class GetCredentialsUseCase(
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke() = authRepository.getToken()
}