package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class TransactionResponse(
    val id: Long,
    val accountId: Int,
    val type: TransactionType,
    val amount: Double,
    val balanceBefore: Double,
    val balanceAfter: Double,
    @SerialName("relatedAccountId") val relatedAccountId: Int? = null,
    val description: String? = null,
    @SerialName("createdAt")  @Contextual val createdAt: OffsetDateTime
)
