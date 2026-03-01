package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountCommand(
    val ownerId: Int,
    val currency: String = "RUB"
)