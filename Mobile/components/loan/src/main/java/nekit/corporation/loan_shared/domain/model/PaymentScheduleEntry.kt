package nekit.corporation.loan_shared.domain.model

import java.time.Instant

data class PaymentScheduleEntry(
    val id: Int,
    val dueDate: Instant,
    val amount: Double,
    val isPaid: Boolean,
    val paidAt: Instant?
)