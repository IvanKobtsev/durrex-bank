package nekit.corporation.loan_shared.domain.model

import kotlin.time.Instant

data class Overdue(
    val entryId: Int,
    val creditId: Int,
    val dueDate: Instant,
    val amount: Int,
    val daysOverdue: Int,
)
