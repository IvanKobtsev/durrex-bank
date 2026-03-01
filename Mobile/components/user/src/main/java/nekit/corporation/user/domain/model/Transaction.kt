package nekit.corporation.user.domain.model

import java.time.Instant

data class Transaction(
    val id: Long,
    val accountId: Int,
    val type: TransactionTypeDomain,
    val amount: Double,
    val balanceBefore: Double,
    val balanceAfter: Double,
    val relatedAccountId: Int? = null,
    val description: String? = null,
    val createdAt: Instant
)