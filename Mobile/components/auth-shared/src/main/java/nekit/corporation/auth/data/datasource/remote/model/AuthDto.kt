package nekit.corporation.auth.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    @SerialName("userName")
    val name: String,
    val password: String
)
