package nekit.corporation.auth.domain.usecase

import android.util.Log
import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: AuthRepository) {

    suspend fun execute(credentials: Credentials) {
        val token = repository.login(credentials)
        Log.d("RAG", "token $token")
        repository.saveToken(token)
    }
}
