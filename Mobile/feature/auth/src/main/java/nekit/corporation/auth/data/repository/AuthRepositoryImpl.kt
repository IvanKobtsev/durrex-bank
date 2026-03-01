package nekit.corporation.auth.data.repository

import kotlinx.coroutines.flow.first
import nekit.corporation.auth.data.datasource.local.AuthDataStore
import nekit.corporation.auth.data.datasource.remote.AuthApi
import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.model.TokenDto
import nekit.corporation.auth.domain.repository.AuthRepository
import nekit.corporation.auth.domain.toAuthDto
import nekit.corporation.auth.domain.toRegisterDto
import nekit.corporation.auth.domain.toToken
import nekit.corporation.auth.domain.toTokenLocal
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataStore: AuthDataStore,
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(credentials: Credentials): TokenDto {
        return authApi.login(credentials.toAuthDto())
    }

    override suspend fun register(credentials: RegisterModel) {
        authApi.register(credentials.toRegisterDto())
    }

    override suspend fun getToken(): TokenDto? {
        return authDataStore.data.first().toToken()
    }

    override suspend fun saveToken(token: TokenDto) {
        authDataStore.updateData { token.toTokenLocal() }
    }
}
