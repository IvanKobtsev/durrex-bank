package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class AccountResponse(
    val id: Int,
    val ownerId: Int,
    val balance: Double,
    val currency: String,
    val status: AccountStatus,
    @SerialName("createdAt") @Contextual val createdAt: OffsetDateTime,
    @SerialName("closedAt")  @Contextual val closedAt: OffsetDateTime? = null
)