package nekit.corporation.loan_shared.domain.model

import java.time.Instant

data class Credit(
    val id: Int,
    val clientId: Int,
    val accountId: Int,
    val tariffName: String?,
    val amount: Double,
    val remainingBalance: Double,
    val status: CreditStatusDomain,
    val issuedAt: Instant
)