package nekit.corporation.loan_shared.domain.model

data class Deposit(
    val id: Int = 0,
    val amount: Double,
    val description: String?
)
