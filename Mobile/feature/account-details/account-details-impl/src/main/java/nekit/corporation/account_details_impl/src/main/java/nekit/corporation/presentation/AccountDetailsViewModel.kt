package nekit.corporation.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import nekit.corporation.account_details.R
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.model.Debit
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.navigation.AccountDetailsNavigation
import nekit.corporation.presentation.model.AccountDetailsEvent
import nekit.corporation.presentation.model.AccountDetailsInteractions
import nekit.corporation.presentation.model.AccountDetailsState
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure
import javax.inject.Inject

class AccountDetailsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val navigator: AccountDetailsNavigation
) : StatefulViewModel<AccountDetailsState>(), AccountDetailsInteractions {

    override fun createInitialState(): AccountDetailsState {
        return AccountDetailsState.Loading
    }

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            reduceError {
                val accounts = async { accountRepository.getAllAccounts() }
                val transactions = async { accountRepository.getTransactions(id) }
                val account = accounts.await().first { it.id == id }
                updateState {
                    AccountDetailsState.Content(
                        account = account,
                        isApplyButtonEnable = false,
                        sum = "",
                        selectedOperation = TransactionTypeDomain.DEBIT,
                        isOperationTypeOpen = false,
                        isLoading = false,
                        transactions = transactions.await().toImmutableList(),
                        isDeleteCan = account.balance == 0.0
                    )
                }
            }
        }
    }

    override fun onBack() = navigator.onBack()

    override fun onApplyClick() {
        viewModelScope.launch(Dispatchers.IO) {
            updateStateOf<AccountDetailsState.Content> {
                copy(isLoading = true)
            }
            updateStateOf<AccountDetailsState.Content> {
                reduceError {
                    if (selectedOperation == TransactionTypeDomain.DEPOSIT) {
                        accountRepository.deposit(
                            account.id,
                            Deposit(amount = sum.toDouble(), description = null)
                        )
                    } else if (selectedOperation == TransactionTypeDomain.DEBIT) {
                        accountRepository.debit(
                            account.id,
                            Debit(amount = sum.toDouble(), description = null)
                        )
                    }

                }
                val accountD = async { accountRepository.getAccount(account.id) }
                val transactions = async { accountRepository.getTransactions(account.id) }
                copy(
                    isLoading = false,
                    transactions = transactions.await()
                        .toImmutableList(),
                    account = accountD.await()
                )
            }
            updateStateOf<AccountDetailsState.Content> {
                copy(
                    isLoading = false,
                    isDeleteCan = account.balance == 0.0
                )
            }
        }
    }

    override fun onSumChange(sum: String) {
        updateStateOf<AccountDetailsState.Content> {
            copy(
                sum = sum,
                isApplyButtonEnable = sum.isNotEmpty() &&
                        (sum.toDouble() <= account.balance && selectedOperation == TransactionTypeDomain.DEBIT
                                || TransactionTypeDomain.DEBIT != selectedOperation),
            )
        }
    }

    override fun onDeleteClick() {
        viewModelScope.launch(Dispatchers.IO) {
            updateStateOf<AccountDetailsState.Content> {
                copy(isLoading = true)
            }
            updateStateOf<AccountDetailsState.Content> {
                reduceError {
                    accountRepository.closeAccount(account.id)
                    navigator.onBack()
                }
                copy(isLoading = false)
            }

        }
    }

    override fun onSelectOperation(operation: TransactionTypeDomain) {
        updateStateOf<AccountDetailsState.Content> {
            copy(
                selectedOperation = operation,
                isApplyButtonEnable = sum.isNotEmpty() &&
                        (sum.toDouble() <= account.balance && selectedOperation == TransactionTypeDomain.DEBIT
                                || TransactionTypeDomain.DEBIT != selectedOperation)
            )
        }
    }

    override fun onDismiss() {
        updateStateOf<AccountDetailsState.Content> {
            copy(isOperationTypeOpen = false)
        }
    }

    override fun onOpen() {
        updateStateOf<AccountDetailsState.Content> {
            copy(isOperationTypeOpen = true)
        }
    }

    override fun onTransactionOpen(transaction: Transaction) {
        navigator.toTransaction(transaction.accountId, transaction.id)
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