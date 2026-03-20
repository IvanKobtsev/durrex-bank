package nekit.corporation.transaction_details.presentation.model

import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Transaction

sealed interface TransactionDetailsState : ScreenState {

    data object Loading : TransactionDetailsState

    data class Content(
        val transaction: Transaction,
    ) : TransactionDetailsState
}
