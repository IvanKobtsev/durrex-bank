package nekit.corporation.account_details_impl.presentation.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import nekit.corporation.architecture.presentation.ScreenState
import nekit.corporation.loan_shared.domain.model.Account
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain

sealed interface AccountDetailsState : ScreenState {

    data object Loading : AccountDetailsState

    data class Content(
        val account: Account?,
        val isApplyButtonEnable: Boolean,
        val sum: String,
        val selectedOperation: TransactionTypeDomain,
        val isOperationTypeOpen: Boolean,
        val isLoading: Boolean,
        val transactions: ImmutableList<Transaction>,
        val isDeleteCan: Boolean,
        val isHidden: Boolean
    ) : AccountDetailsState {

        companion object {
            val default = Content(
                account = null,
                isApplyButtonEnable = false,
                sum = "",
                selectedOperation = TransactionTypeDomain.Deposit,
                isOperationTypeOpen = false,
                isLoading = true,
                transactions = persistentListOf(),
                isDeleteCan = false,
                isHidden = false
            )
        }
    }
}
