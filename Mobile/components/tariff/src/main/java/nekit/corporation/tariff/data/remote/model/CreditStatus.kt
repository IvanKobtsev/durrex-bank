package nekit.corporation.tariff.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CreditStatus {
    @SerialName("0")
    ACTIVE,
    @SerialName("1")
    CLOSED
}
