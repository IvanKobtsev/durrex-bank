package nekit.corporation.auth.domain.repository

import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.model.TokenDto

interface AuthRepository {

    suspend fun login(credentials: Credentials): TokenDto

    suspend fun register(credentials: RegisterModel)

    suspend fun getToken(): TokenDto?

    suspend fun saveToken(token: TokenDto)
}
