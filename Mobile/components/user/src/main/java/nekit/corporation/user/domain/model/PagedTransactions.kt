package nekit.corporation.user.domain.model

data class PagedTransactions(
    val items: List<Transaction>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int
)