package nekit.corporation.tariff.domain.model

data class CreateTariff(
    val name: String?,
    val interestRate: Double
)