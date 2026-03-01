package nekit.corporation.presentation.model

import nekit.corporation.architecture.presentation.ScreenState

sealed interface LoanStateState : ScreenState {

    data object Init : LoanStateState

    data class Approved(
        val amount: Int,
        val period: String,
    ) : LoanStateState

    data object Rejected : LoanStateState
}