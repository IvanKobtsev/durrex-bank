package nekit.corporation.loan_shared.domain.model

data class Transfer(
    val targetAccountId: Int,
    val amount: Double,
    val description: String? = null
)