package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class CreditResponse(
    val id: Int,
    @SerialName("clientId")
    val clientId: Int,
    @SerialName("accountId")
    val accountId: Int,
    @SerialName("tariffName")
    val tariffName: String? = null,
    val amount: Double,
    @SerialName("remainingBalance")
    val remainingBalance: Double,
    val status: CreditStatus,
    @SerialName("issuedAt") @Contextual
    val issuedAt: Instant
)