package nekit.corporation.user.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponseOfTransactionResponse(
    val items: List<TransactionResponse>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int
)