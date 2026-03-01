package nekit.corporation.presentation.model

import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Account

sealed interface AccountDetailsState : ScreenState {

    data object Loading : AccountDetailsState

    data class Content(
        val account: Account,
    ) : AccountDetailsState
}
