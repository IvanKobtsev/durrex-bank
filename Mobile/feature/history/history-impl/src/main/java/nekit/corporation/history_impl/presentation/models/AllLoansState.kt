package nekit.corporation.history_impl.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Credit

internal sealed interface AllLoansState : ScreenState {

    data object Loading : AllLoansState

    data class Component(
        val loans: ImmutableList<Credit>,
    ) : AllLoansState
}
