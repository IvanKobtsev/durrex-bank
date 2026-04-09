package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class RatingDto(
    val clientId: Int,
    val score: Int,
    val calculatedAt: Instant
)
