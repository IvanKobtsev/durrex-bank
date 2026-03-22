package nekit.corporation.loan_details_impl.presentation.model

import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.CreditDetail

internal sealed interface LoanDetailsState : ScreenState {

    data object Loading : LoanDetailsState

    data class Content(
        val credit: CreditDetail,
    ) : LoanDetailsState
}
