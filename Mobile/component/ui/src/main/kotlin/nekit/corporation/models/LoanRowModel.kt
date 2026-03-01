package nekit.corporation.models

data class LoanRowModel(
    val id: Long,
    val number: Long,
    val state: LoanUiState,
    val sum: Int,
    val date: String,
    val currency: Int,
)