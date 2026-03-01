package nekit.corporation.auth.domain

import nekit.corporation.auth.data.datasource.local.model.CredentialsModel
import nekit.corporation.auth.data.datasource.local.model.TokenLocal
import nekit.corporation.auth.data.datasource.remote.model.AuthDto
import nekit.corporation.auth.data.datasource.remote.model.RawTokenResponse
import nekit.corporation.auth.data.datasource.remote.model.RegisterDto
import nekit.corporation.auth.domain.model.Credentials
import nekit.corporation.auth.domain.model.RegisterModel
import nekit.corporation.auth.domain.model.TokenDto

fun Credentials.toAuthDto() = AuthDto(
    name = login,
    password = password
)

fun RegisterModel.toCredentials() = Credentials(
    login = login,
    password = password
)

fun Credentials.toCredentialsModel() = CredentialsModel(
    login = login,
    password = password
)

fun RegisterModel.toRegisterDto() = RegisterDto(
    password = password,
    email = email,
    userName = login,
    firstName = firstName,
    lastName = lastName,
    phone = phone
)

fun TokenDto.toTokenLocal() = TokenLocal(
    token = token,
    expiresAt = expiresAt
)

fun TokenLocal.toToken() = if (token != null && expiresAt != null) TokenDto(
    token = token,
    expiresAt = expiresAt
) else null
