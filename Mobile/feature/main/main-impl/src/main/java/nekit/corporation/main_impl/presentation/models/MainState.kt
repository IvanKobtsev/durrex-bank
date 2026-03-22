package nekit.corporation.main_impl.presentation.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.Credit


sealed interface MainState : ScreenState {

    data object Loading : MainState

    data class Content(
        val loanSum: Int,
        val isCurrencyMenuOpen: Boolean,
        val selectedCurrency: Currency,
        val credits: ImmutableList<Credit>,
        val allAccounts: ImmutableList<Account>,
        val accounts: ImmutableList<Account>,
        val showHidden: Boolean,
        val hidden: ImmutableList<Int>
    ) : MainState
}
