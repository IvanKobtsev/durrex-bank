package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AccountStatus {
    @SerialName("0") Open,
    @SerialName("1") Closed
}