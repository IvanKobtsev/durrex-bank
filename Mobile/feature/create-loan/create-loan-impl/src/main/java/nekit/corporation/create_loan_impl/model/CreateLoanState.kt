package nekit.corporation.create_loan_impl.model

import kotlinx.collections.immutable.ImmutableList
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
    val isLoading: Boolean
) : ScreenState