package nekit.corporation.user.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class TransactionResponse(
    val id: Long,
    @SerialName("accountId")
    val accountId: Int,
    val type: TransactionType,
    val amount: Double,
    @SerialName("balanceBefore")
    val balanceBefore: Double,
    @SerialName("balanceAfter")
    val balanceAfter: Double,
    @SerialName("relatedAccountId")
    val relatedAccountId: Int? = null,
    val description: String? = null,
    @SerialName("createdAt") @Contextual
    val createdAt: Instant
)