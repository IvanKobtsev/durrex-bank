package nekit.corporation.history_impl.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Transaction

internal data class MenuState(
    val transactions: ImmutableList<Transaction>
) : ScreenState
