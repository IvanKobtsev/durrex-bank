package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    @SerialName("0")
    DEPOSIT,

    @SerialName("1")
    WITHDRAW,

    @SerialName("2")
    TRANSFER,

    @SerialName("3")
    DEBIT
}