package nekit.corporation.auth.data.datasource.remote.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class RawTokenResponse(
    val token: String
)
