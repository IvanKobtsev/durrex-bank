package nekit.corporation.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.domain.Currency
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.Credit
import nekit.corporation.models.LoanRowModel


sealed interface MainState : ScreenState {

    data object Loading : MainState

    data class Content(
        val loanSum: Int,
        val isCurrencyMenuOpen: Boolean,
        val selectedCurrency: Currency,
        val credits: ImmutableList<Credit>,
        val accounts: ImmutableList<Account>
    ) : MainState
}
