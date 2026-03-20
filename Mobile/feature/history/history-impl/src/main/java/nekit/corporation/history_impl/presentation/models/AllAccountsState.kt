package nekit.corporation.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Account

sealed interface AllAccountsState : ScreenState {

    data object Loading : AllAccountsState

    data class Component(
        val accounts: ImmutableList<Account>,
    ) : AllAccountsState
}
