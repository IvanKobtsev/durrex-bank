package nekit.corporation.loan_shared.data.datasource.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDetails(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null
)
