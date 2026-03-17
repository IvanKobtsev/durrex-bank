package nekit.corporation.auth.data.datasource.local.model

import kotlinx.serialization.Serializable

@Serializable
data class CredentialsModel(
    val login: String,
    val password: String
)
