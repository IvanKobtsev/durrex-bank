package nekit.corporation.auth.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val name: String,
    val role: String
)
