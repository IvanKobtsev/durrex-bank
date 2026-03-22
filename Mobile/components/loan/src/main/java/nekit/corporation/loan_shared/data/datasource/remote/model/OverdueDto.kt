package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class OverdueDto(
    val entryId: Int,
    val creditId: Int,
    val dueDate: Instant,
    val amount: Int,
    val daysOverdue: Int,
)
