package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class WithdrawRequest(
    val amount: Double,
    val description: String? = null
)