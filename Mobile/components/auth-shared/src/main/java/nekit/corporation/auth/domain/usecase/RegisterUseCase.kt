package nekit.corporation.auth.domain.usecase

import dev.zacsweers.metro.Inject
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.repository.AuthRepository
import nekit.corporation.auth.domain.toCredentials

@Inject
class RegisterUseCase (private val repository: AuthRepository) {

    suspend fun execute(credentials: RegisterModel) {
        repository.register(credentials)
        val token = repository.login(credentials.toCredentials())
        repository.saveToken(token)
    }
}
