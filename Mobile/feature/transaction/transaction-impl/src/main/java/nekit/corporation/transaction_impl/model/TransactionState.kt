package nekit.corporation.transaction.model

import androidx.annotation.StringRes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.transaction_impl.model.AccountUi

data class TransactionState(
    val accountTo: String,
    @get:StringRes val accountToError: Int?,
    val accountFrom: AccountUi?,
    val userAccounts: ImmutableList<AccountUi>,
    val isLoading: Boolean,
    val sum: Double,
    val isCreateButtonEnabled: Boolean,
    val recipient: UserUi?,
    val user: UserUi?,
    val description: String,
    val isButtonEnable: Boolean
) : ScreenState {
    companion object {
        val DEFAULT = TransactionState(
            accountTo = "",
            accountToError = null,
            accountFrom = null,
            isLoading = false,
            sum = 0.0,
            userAccounts = persistentListOf(),
            isCreateButtonEnabled = false,
            user = null,
            recipient = null,
            description = "",
            isButtonEnable = false
        )
    }
}
