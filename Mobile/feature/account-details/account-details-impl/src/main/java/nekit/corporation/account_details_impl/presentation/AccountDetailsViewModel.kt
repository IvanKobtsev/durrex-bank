package nekit.corporation.account_details_impl.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import nekit.corporation.account_details_impl.R
import nekit.corporation.account_details_impl.navigation.AccountDetailsNavigator
import nekit.corporation.account_details_impl.presentation.model.AccountDetailsEvent
import nekit.corporation.account_details_impl.presentation.model.AccountDetailsInteractions
import nekit.corporation.account_details_impl.presentation.model.AccountDetailsState
import nekit.corporation.architecture.presentation.StatefulViewModel
import nekit.corporation.loan_shared.domain.model.Debit
import nekit.corporation.loan_shared.domain.model.Deposit
import nekit.corporation.loan_shared.domain.model.Transaction
import nekit.corporation.loan_shared.domain.model.TransactionTypeDomain
import nekit.corporation.loan_shared.domain.repository.AccountRepository
import nekit.corporation.account_details_impl.presentation.model.AccountDetailsEvent.ShowToast
import nekit.corporation.user.domain.usecase.GetSettingsUseCase
import nekit.corporation.user.domain.usecase.UpdateHiddenIdsUseCase
import nekit.corporation.util.domain.common.CommonBackendFailure
import nekit.corporation.util.domain.common.ForbiddenFailure
import nekit.corporation.util.domain.common.NoConnectionFailure
import nekit.corporation.util.domain.common.NotFoundFailure

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

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is CommonBackendFailure) {
            handleError(throwable)
        } else {
            handleUnknownError(throwable)
        }
        Log.d(TAG, throwable.message.toString())
    }

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                loadAccountDetails(id)
            } finally {
                updateStateOf<AccountDetailsState.Content> {
                    copy(isLoading = false)
                }
            }
            observeTransactionUpdates(id)
        }
    }

    private suspend fun loadAccountDetails(id: Int) = supervisorScope {
        updateState { AccountDetailsState.Content.default }
        val accountDeferred = async { accountRepository.getAccount(id) }
        val transactionsDeferred = async { accountRepository.getTransactions(id) }
        val settingsDeferred = async { getSettingsUseCase() }

        listOf(
            launch {
                val account = accountDeferred.await()
                updateStateOf<AccountDetailsState.Content> {
                    copy(
                        account = account,
                        isDeleteCan = account.balance == 0.0
                    )
                }
            },
            launch {
                val transactions = transactionsDeferred.await()
                updateStateOf<AccountDetailsState.Content> {
                    copy(
                        transactions = transactions.toImmutableList()
                    )
                }
            },
            launch {
                val settings = settingsDeferred.await()
                val account = accountDeferred.await()
                updateStateOf<AccountDetailsState.Content> {
                    copy(
                        isHidden = settings.hiddenAccountIds.contains(account.id)
                    )
                }
            }
        ).joinAll()


    }

    private suspend fun observeTransactionUpdates(id: Int) {
        accountRepository.getTransactionHubEvents(accountId = id).collect {
            Log.d(TAG, "updated")
            val transactions = accountRepository.getTransactions(id)
            updateStateOf<AccountDetailsState.Content> {
                copy(
                    transactions = transactions.toImmutableList()
                )
            }
        }
    }

    override fun onBack() = navigator.back()

    override fun onApplyClick() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val currentState =
                currentScreenState as? AccountDetailsState.Content ?: return@launch

            updateStateOf<AccountDetailsState.Content> { copy(isLoading = true) }

            when (currentState.selectedOperation) {
                TransactionTypeDomain.Deposit -> {
                    currentState.account?.id?.let {
                        accountRepository.deposit(
                            it,
                            Deposit(amount = currentState.sum.toDouble(), description = null)
                        )
                    }
                }

                TransactionTypeDomain.Debit -> {
                    currentState.account?.id?.let {
                        accountRepository.debit(
                            it,
                            Debit(amount = currentState.sum.toDouble(), description = null)
                        )
                    }
                }

                else -> Unit
            }

            updateStateOf<AccountDetailsState.Content> { copy(isLoading = false) }
        }
    }

    override fun onSumChange(sum: String) {
        updateStateOf<AccountDetailsState.Content> {
            val isApplyEnabled =
                sum.isNotEmpty() && (
                        (account?.balance != null && sum.toDouble() <= account.balance && selectedOperation == TransactionTypeDomain.Debit) ||
                                selectedOperation != TransactionTypeDomain.Debit
                        )
            copy(sum = sum, isApplyButtonEnable = isApplyEnabled)
        }
    }

    override fun onDeleteClick() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            updateStateOf<AccountDetailsState.Content> { copy(isLoading = true) }
            val result = (currentScreenState as? AccountDetailsState.Content)?.account?.id?.let {
                accountRepository.closeAccount(it)
            }
            if (result != null) {
                navigator.back()
            } else {
                updateStateOf<AccountDetailsState.Content> { copy(isLoading = false) }
            }
        }
    }

    override fun onHide() {
        viewModelScope.launch(exceptionHandler) {
            try {
                updateStateOf<AccountDetailsState.Content> {
                    copy(isLoading = true)
                }
                when (val state = currentScreenState) {
                    is AccountDetailsState.Content if state.isHidden -> {
                        Log.d(TAG, "state.account: ${state.account}")
                        updateHiddenIdsUseCase(
                            added = listOf(),
                            removed = state.account?.id?.let { listOf(it) } ?: listOf()
                        )
                    }

                    is AccountDetailsState.Content -> {
                        Log.d(TAG, "state.account1: ${state.account}")
                        updateHiddenIdsUseCase(
                            added = state.account?.id?.let { listOf(it) } ?: listOf(),
                            removed = listOf()
                        )
                    }

                    else -> updateHiddenIdsUseCase(
                        added = listOf(),
                        removed = listOf()
                    )
                }.let {
                    updateStateOf<AccountDetailsState.Content> {
                        copy(isHidden = it.hiddenAccountIds.contains(account?.id))
                    }
                }
            } finally {
                updateStateOf<AccountDetailsState.Content> {
                    copy(isLoading = false)
                }

            }
        }
    }

    override fun onSelectOperation(operation: TransactionTypeDomain) {
        updateStateOf<AccountDetailsState.Content> {
            val isApplyEnabled = sum.isNotEmpty() && (
                    (account?.balance != null && sum.toDouble() <= account.balance && operation == TransactionTypeDomain.Debit) ||
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