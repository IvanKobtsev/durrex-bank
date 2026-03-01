package nekit.corporation.tariff.domain.model

data class Tariff(
    val id: Int,
    val name: String?,
    val interestRate: Double
)