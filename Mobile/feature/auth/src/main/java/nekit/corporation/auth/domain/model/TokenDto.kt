package nekit.corporation.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    val token: String,
    val expiresAt: String,
)