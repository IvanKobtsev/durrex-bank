package nekit.corporation.loan_shared.domain.model

data class PagedTransactions(
    val items: List<Transaction>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int
)