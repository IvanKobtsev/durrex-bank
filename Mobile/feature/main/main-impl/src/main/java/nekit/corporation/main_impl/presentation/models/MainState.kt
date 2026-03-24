package nekit.corporation.main_impl.presentation.models

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
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
        val hidden: ImmutableList<Int>,
        val isLoading: Boolean
    ) : MainState {
        companion object {
            val default = Content(
                loanSum = 5_000,
                isCurrencyMenuOpen = false,
                selectedCurrency = Currency.RUB,
                credits = persistentListOf(),
                accounts = persistentListOf(),
                showHidden = false,
                hidden = persistentListOf(),
                allAccounts = persistentListOf(),
                isLoading = false,
            )
        }
    }
}
