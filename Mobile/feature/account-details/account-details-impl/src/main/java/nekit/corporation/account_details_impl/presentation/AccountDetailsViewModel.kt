package nekit.corporation.account_details_impl.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import nekit.corporation.account_details_impl.R
import nekit.corporation.account_details_impl.navigation.AccountDetailsNavigator
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.model.Debit
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.presentation.model.AccountDetailsEvent.*
import nekit.corporation.account_details_impl.presentation.model.AccountDetailsInteractions
import nekit.corporation.account_details_impl.presentation.model.AccountDetailsState
import nekit.corporation.user.data.model.UpdateHiddenAccountsDto
import nekit.corporation.user.domain.usecase.GetSettingsUseCase
import nekit.corporation.user.domain.usecase.UpdateHiddenIdsUseCase
import nekit.corporation.util.domain.common.BadRequestFailure
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure
import nekit.corporation.util.domain.common.ServerFailure
import nekit.corporation.util.domain.common.UnknownFailure

@Inject
@ViewModelKey(AccountDetailsViewModel::class)
@ContributesIntoMap(
    AppScope::class, binding = binding<ViewModel>()
)
class AccountDetailsViewModel(
    private val accountRepository: AccountRepository,
    private val navigator: AccountDetailsNavigator,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateHiddenIdsUseCase: UpdateHiddenIdsUseCase
) : StatefulViewModel<AccountDetailsState>(), AccountDetailsInteractions {

    override fun createInitialState(): AccountDetailsState {
        return AccountDetailsState.Loading
    }

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val accounts = async {
                fallback(
                    { accountRepository.getAllAccounts() },
                    { reduceError { throw it } })
            }
            val transactions = async {
                fallback(
                    { accountRepository.getTransactions(id) },
                    { reduceError { throw it } })
            }
            val account = accounts.await()?.first { it.id == id }
            val settings = async {
                fallback(
                    { getSettingsUseCase() },
                    { reduceError { throw it } }
                )
            }.await()
            if (account != null && settings != null)
                updateState {
                    AccountDetailsState.Content(
                        account = account,
                        isApplyButtonEnable = false,
                        sum = "",
                        selectedOperation = TransactionTypeDomain.Debit,
                        isOperationTypeOpen = false,
                        isLoading = false,
                        transactions = transactions.await()?.toImmutableList()
                            ?: persistentListOf(),
                        isDeleteCan = account.balance == 0.0,
                        isHidden = settings.hiddenAccountIds.contains(account.id)
                    )
                }
        }
        viewModelScope.launch {
            accountRepository.getTransactionHubEvents(accountId = id).collect {
                Log.d(TAG, "updated")
                async {
                    fallback(
                        { accountRepository.getTransactions(id) },
                        { reduceError { throw it } })
                }.await()?.let {
                    updateStateOf<AccountDetailsState.Content> {
                        copy(
                            transactions = it.toImmutableList()
                        )
                    }
                }
            }
        }
    }

    override fun onBack() = navigator.back()

    override fun onApplyClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentState =
                currentScreenState as? AccountDetailsState.Content ?: return@launch

            updateStateOf<AccountDetailsState.Content> { copy(isLoading = true) }

            safeCall {
                when (currentState.selectedOperation) {
                    TransactionTypeDomain.Deposit -> {
                        accountRepository.deposit(
                            currentState.account.id,
                            Deposit(amount = currentState.sum.toDouble(), description = null)
                        )
                    }

                    TransactionTypeDomain.Debit -> {
                        accountRepository.debit(
                            currentState.account.id,
                            Debit(amount = currentState.sum.toDouble(), description = null)
                        )
                    }

                    else -> Unit
                }
            }

            updateStateOf<AccountDetailsState.Content> { copy(isLoading = false) }
        }
    }

    override fun onSumChange(sum: String) {
        updateStateOf<AccountDetailsState.Content> {
            val isApplyEnabled =
                sum.isNotEmpty() && (
                        (sum.toDouble() <= account.balance && selectedOperation == TransactionTypeDomain.Debit) ||
                                selectedOperation != TransactionTypeDomain.Debit
                        )
            copy(sum = sum, isApplyButtonEnable = isApplyEnabled)
        }
    }

    override fun onDeleteClick() {
        viewModelScope.launch(Dispatchers.IO) {
            updateStateOf<AccountDetailsState.Content> { copy(isLoading = true) }

            val result = safeCall {
                accountRepository.closeAccount(
                    (currentScreenState as? AccountDetailsState.Content)?.account?.id
                        ?: return@safeCall
                )
            }

            if (result != null) {
                navigator.back()
            } else {
                updateStateOf<AccountDetailsState.Content> { copy(isLoading = false) }
            }
        }
    }

    override fun onHide() {
        viewModelScope.launch {
            updateStateOf<AccountDetailsState.Content> {
                copy(isLoading = true)
            }
            val state = currentScreenState

            fallback(
                {
                    if (state is AccountDetailsState.Content && state.isHidden) {
                        updateHiddenIdsUseCase(
                            added = listOf(),
                            removed = listOf(state.account.id)
                        )
                    } else if (state is AccountDetailsState.Content) {
                        updateHiddenIdsUseCase(
                            added = listOf(state.account.id),
                            removed = listOf()
                        )
                    } else
                        updateHiddenIdsUseCase(
                            added = listOf(),
                            removed = listOf()
                        )
                }, {
                    handleUnknownError(it)
                }
            )?.let {
                updateStateOf<AccountDetailsState.Content> {
                    copy(isHidden = it.hiddenAccountIds.contains(account.id))
                }
            }
            updateStateOf<AccountDetailsState.Content> {
                copy(isLoading = false)
            }
        }
    }

    override fun onSelectOperation(operation: TransactionTypeDomain) {
        updateStateOf<AccountDetailsState.Content> {
            val isApplyEnabled = sum.isNotEmpty() && (
                    (sum.toDouble() <= account.balance && operation == TransactionTypeDomain.Debit) ||
                            operation != TransactionTypeDomain.Debit
                    )

            copy(
                selectedOperation = operation,
                isApplyButtonEnable = isApplyEnabled
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
                    offerEvent(ShowToast(R.string.no_connections_error))
                }

                is NotFoundFailure -> {
                    offerEvent(ShowToast(R.string.not_found_error))
                }

                is ServerFailure, is UnknownFailure, is BadRequestFailure ->
                    offerEvent(ShowToast(R.string.strange_error))

                is ForbiddenFailure -> navigator.toAuth()
            }
            Log.d(TAG, "error: ${e.message}")
        } catch (e: Throwable) {
            offerEvent(ShowToast(R.string.strange_error))
            Log.d(TAG, e.message.toString())
        }
    }

    private suspend fun <T> safeCall(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: CommonBackendFailure) {
            handleError(e)
            null
        } catch (e: Throwable) {
            handleUnknownError(e)
            null
        }
    }

    private fun handleError(e: CommonBackendFailure) {
        when (e) {
            is NoConnectionFailure -> offerEvent(ShowToast(R.string.no_connections_error))
            is NotFoundFailure -> offerEvent(ShowToast(R.string.not_found_error))
            is ForbiddenFailure -> navigator.toAuth()
            else -> offerEvent(ShowToast(R.string.strange_error))
        }
        Log.d(TAG, "Backend error: ${e.message}")
    }

    private fun handleUnknownError(e: Throwable) {
        offerEvent(ShowToast(R.string.strange_error))
        Log.d(TAG, "Unknown error: ${e.message}")
    }

    private companion object {
        const val TAG = "AccountDetailsViewModel"
    }
}