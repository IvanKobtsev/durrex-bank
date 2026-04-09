package nekit.corporation.create_loan_impl.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.tariff.domain.model.Tariff

data class CreateLoanState(
    val selectedAccount: AccountUi?,
    val selectedTariff: Tariff?,
    val tariffs: ImmutableList<Tariff>,
    val isTariffOpen: Boolean,
    val isAccountOpen: Boolean,
    val accounts: ImmutableList<AccountUi>,
    val amount: Double,
    val isLoading: Boolean,
    val isFatalError: Boolean,
) : ScreenState {
    companion object {
        val default = CreateLoanState(
            selectedAccount = null,
            selectedTariff = null,
            tariffs = persistentListOf(),
            isTariffOpen = false,
            isAccountOpen = false,
            accounts = persistentListOf(),
            amount = 0.0,
            isLoading = true,
            isFatalError = false,
        )
    }
}