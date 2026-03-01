package nekit.corporation.auth.domain.usecase

import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.repository.AuthRepository
import nekit.corporation.auth.domain.toCredentials
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend fun execute(credentials: RegisterModel) {
        repository.register(credentials)
        val token = repository.login(credentials.toCredentials())
        repository.saveToken(token)
    }
}
