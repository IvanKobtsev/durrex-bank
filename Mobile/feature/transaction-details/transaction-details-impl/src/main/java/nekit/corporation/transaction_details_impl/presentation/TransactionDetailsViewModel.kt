package nekit.corporation.transaction_details.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.transaction_details.R
import nekit.corporation.transaction_details.navigation.TransactionDetailsNavigation
import nekit.corporation.transaction_details.presentation.model.TransactionDetailsEvent
import nekit.corporation.transaction_details.presentation.model.TransactionDetailsState
import nekit.corporation.transaction_details.presentation.model.TransactionInteractions
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure

import javax.inject.Inject

class TransactionDetailsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val loanDetailsNavigation: TransactionDetailsNavigation
) : StatefulViewModel<TransactionDetailsState>(), TransactionInteractions {

    override fun createInitialState(): TransactionDetailsState {
        return TransactionDetailsState.Loading
    }

    override fun onBack() = loanDetailsNavigation.onBack()

    fun init(accountId: Int, transactionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val transaction =
                    accountRepository.getTransactions(accountId)
                        .minByOrNull { it.id == transactionId }!!

                updateState {
                    TransactionDetailsState.Content(
                        transaction = transaction
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
                  //  offerEvent(TransactionDetailsEvent.ShowToast(R.string.no_connections_error))
                }

                is NotFoundFailure -> {
                 //   offerEvent(TransactionDetailsEvent.ShowToast(R.string.not_found_error))
                }

                is ServerFailure, is UnknownFailure, is BadRequestFailure -> {
                    // offerEvent(TransactionDetailsEvent.ShowToast(R.string.strange_error))
                }

            }
        } catch (e: Throwable) {
            //offerEvent(TransactionDetailsEvent.ShowToast(R.string.strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    private companion object {
        const val TAG = "LoanDetailsViewModel"
    }
}