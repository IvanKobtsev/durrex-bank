package nekit.corporation.auth.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    val name: String,
    val password: String
)
