package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssueCreditRequest(
    @SerialName("accountId")
    val accountId: Int,
    @SerialName("tariffId")
    val tariffId: Int,
    val amount: Double
)