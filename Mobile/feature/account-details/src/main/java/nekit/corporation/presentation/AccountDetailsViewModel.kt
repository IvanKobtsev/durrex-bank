package nekit.corporation.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.account_details.R
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.navigation.AccountDetailsNavigation
import nekit.corporation.presentation.model.AccountDetailsEvent
import nekit.corporation.presentation.model.AccountDetailsInteractions
import nekit.corporation.presentation.model.AccountDetailsState
import nekit.corporation.user.domain.GetUserUseCase
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure
import javax.inject.Inject

class AccountDetailsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userUserUseCase: GetUserUseCase,
    private val loanDetailsNavigation: AccountDetailsNavigation
) : StatefulViewModel<AccountDetailsState>(), AccountDetailsInteractions {

    override fun createInitialState(): AccountDetailsState {
        return AccountDetailsState.Loading
    }

    override fun onBack() = loanDetailsNavigation.onBack()

    override fun onDebitClick() {
        TODO("Not yet implemented")
    }

    override fun onDepositClick() {
        TODO("Not yet implemented")
    }

    override fun onDepositSumChange(deposit: String) {
        TODO("Not yet implemented")
    }

    override fun onDebiSumChange(debit: String) {
        TODO("Not yet implemented")
    }

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val user = userUserUseCase()
                val accounts = accountRepository.getAllAccounts(
                    user.id,
                )

                updateState {
                    AccountDetailsState.Content(
                        account = accounts.first { it.id == id }
                    )
                }
            }
        }
    }

    private suspend fun reduceError(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CommonBackendFailure) {
            when (e) {
                is NoConnectionFailure -> {
                    offerEvent(AccountDetailsEvent.ShowToast(R.string.no_connections_error))
                }

                is NotFoundFailure -> {
                    offerEvent(AccountDetailsEvent.ShowToast(R.string.not_found_error))
                }

                is ServerFailure, is UnknownFailure, is BadRequestFailure ->
                    offerEvent(AccountDetailsEvent.ShowToast(R.string.strange_error))

            }
        } catch (e: Throwable) {
            offerEvent(AccountDetailsEvent.ShowToast(R.string.strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    private companion object {
        const val TAG = "LoanDetailsViewModel"
    }
}