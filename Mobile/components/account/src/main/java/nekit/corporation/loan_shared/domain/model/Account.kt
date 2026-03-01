package nekit.corporation.loan_shared.domain.model

import nekit.corporation.loan_shared.data.datasource.remote.model.AccountStatus
import java.time.OffsetDateTime

data class Account(
    val id: Int,
    val ownerId: Int,
    val balance: Double,
    val currency: String,
    val status: AccountStatus,
    val createdAt: OffsetDateTime,
    val closedAt: OffsetDateTime? = null
)
