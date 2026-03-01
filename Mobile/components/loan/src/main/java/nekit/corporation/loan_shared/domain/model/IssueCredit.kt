package nekit.corporation.loan_shared.domain.model

data class IssueCredit(
    val accountId: Int,
    val tariffId: Int,
    val amount: Double
)