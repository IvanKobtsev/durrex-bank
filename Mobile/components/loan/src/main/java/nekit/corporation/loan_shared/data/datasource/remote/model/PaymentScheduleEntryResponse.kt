package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class PaymentScheduleEntryResponse(
    val id: Int,
    @SerialName("dueDate") @Contextual
    val dueDate: Instant,
    val amount: Double,
    @SerialName("isPaid")
    val isPaid: Boolean,
    @SerialName("paidAt")@Contextual
    val paidAt: Instant? = null
)