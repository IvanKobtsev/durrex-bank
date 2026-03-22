package nekit.corporation.loan_shared.domain.model

import kotlin.time.Instant

data class Rating(
    val clientId: Int,
    val rating: Int,
    val calculatedAt: Instant
)
