package nekit.corporation.auth.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterDto(
    val email: String,
    val userName: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String
)