package nekit.corporation.auth.data.datasource.local.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenLocal(
    val token: String?,
    val expiresAt: String?,
)
