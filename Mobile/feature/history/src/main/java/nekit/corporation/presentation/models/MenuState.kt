package nekit.corporation.presentation.models

import kotlinx.collections.immutable.ImmutableList
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.user.domain.model.Transaction

data class MenuState(
    val transactions: ImmutableList<Transaction>
) : ScreenState
