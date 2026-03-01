package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    @SerialName("0") Deposit,
    @SerialName("1") Withdraw,
    @SerialName("2") Transfer,
    @SerialName("3") Debit
}