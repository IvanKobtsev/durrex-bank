package nekit.corporation.user.domain.model

data class Debit(
    val amount: Double,
    val description: String? = null
)