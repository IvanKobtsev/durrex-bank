package nekit.corporation.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.models.LoanRowModel

sealed interface AllLoansState : ScreenState {

    data object Loading : AllLoansState

    data class Component(
        val loans: ImmutableList<Credit>,
    ) : AllLoansState
}
