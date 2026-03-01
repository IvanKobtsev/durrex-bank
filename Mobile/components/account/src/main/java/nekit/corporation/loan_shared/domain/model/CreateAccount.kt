package nekit.corporation.loan_shared.domain.model

data class CreateAccount(
    val ownerId: Int,
    val currency: String
)
