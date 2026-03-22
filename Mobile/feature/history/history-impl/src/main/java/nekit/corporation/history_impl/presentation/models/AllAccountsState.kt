package nekit.corporation.history_impl.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Account

internal sealed interface AllAccountsState : ScreenState {

    data object Loading : AllAccountsState

    data class Component(
        val accounts: ImmutableList<Account>,
    ) : AllAccountsState
}
