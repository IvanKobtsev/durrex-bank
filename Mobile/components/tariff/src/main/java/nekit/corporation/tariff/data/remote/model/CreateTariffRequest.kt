package nekit.corporation.tariff.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTariffRequest(
    val name: String? = null,
    @SerialName("interestRate")
    val interestRate: Double
)